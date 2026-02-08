#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DOCS_DIR="${ROOT_DIR}/docs"
STYLES_DIR="${DOCS_DIR}/stylesheets"
ACTIVE_CSS="${STYLES_DIR}/extra.css"
PID_FILE="${ROOT_DIR}/.mkdocs-preview.pid"
LOG_FILE="${ROOT_DIR}/.mkdocs-preview.log"
ADDR_FILE="${ROOT_DIR}/.mkdocs-preview.addr"
VENV_DIR="${ROOT_DIR}/.venv-docs"
VENV_BIN="${VENV_DIR}/bin"
REQ_FILE="${DOCS_DIR}/requirements.txt"
INCLUDE_DIR="${DOCS_DIR}/include"

HOST="${HOST:-127.0.0.1}"
PORT="${PORT:-8000}"
ADDR="${HOST}:${PORT}"
LIVERELOAD="${LIVERELOAD:-1}"

usage() {
  cat <<'USAGE'
Usage: scripts/docs-preview.sh <command> [theme]

Commands:
  start [theme]   Start mkdocs serve (themes: neon, glass, minimal)
  switch <theme>  Switch the active theme without restarting
  stop            Stop the running preview server
  status          Show status and preview URL
  list            List available themes

Environment:
  HOST (default: 127.0.0.1)
  PORT (default: 8000)
USAGE
}

ensure_python() {
  if ! command -v python3 >/dev/null 2>&1; then
    echo "python3 not found. Please install Python 3."
    exit 1
  fi
}

ensure_venv() {
  ensure_python
  if [[ ! -d "${VENV_DIR}" ]]; then
    python3 -m venv "${VENV_DIR}"
  fi
}

ensure_mkdocs() {
  ensure_venv
  if [[ ! -x "${VENV_BIN}/mkdocs" ]]; then
    if [[ ! -f "${REQ_FILE}" ]]; then
      echo "Docs requirements not found at ${REQ_FILE}"
      exit 1
    fi
    echo "Installing docs dependencies into ${VENV_DIR}..."
    "${VENV_BIN}/python" -m pip install --upgrade pip >/dev/null
    "${VENV_BIN}/python" -m pip install -r "${REQ_FILE}"
  fi
}

is_port_available() {
  local port="${1}"
  "${VENV_BIN}/python" - <<PY >/dev/null 2>&1
import socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
try:
    s.bind(("${HOST}", ${port}))
    ok = True
except OSError:
    ok = False
finally:
    s.close()
raise SystemExit(0 if ok else 1)
PY
}

kill_port_listeners() {
  local port="${1}"
  local pids=""

  if command -v lsof >/dev/null 2>&1; then
    pids="$(lsof -t -iTCP:"${port}" -sTCP:LISTEN 2>/dev/null || true)"
  fi

  if [[ -n "${pids}" ]]; then
    echo "Stopping listeners on port ${port}: ${pids}"
    kill ${pids} >/dev/null 2>&1 || true
    sleep 0.5
    if command -v lsof >/dev/null 2>&1; then
      pids="$(lsof -t -iTCP:"${port}" -sTCP:LISTEN 2>/dev/null || true)"
      if [[ -n "${pids}" ]]; then
        kill -9 ${pids} >/dev/null 2>&1 || true
      fi
    fi
  fi
}

pick_port() {
  local base_port="${1}"
  local try_port
  for try_port in "${base_port}" 8001 8002 8003 8004 8005 8006 8007 8008 8009 8010; do
    if is_port_available "${try_port}"; then
      echo "${try_port}"
      return 0
    fi
  done
  return 1
}

ensure_styles_dir() {
  mkdir -p "${STYLES_DIR}"
}

ensure_include_dir() {
  mkdir -p "${INCLUDE_DIR}"
}

is_running() {
  if [[ -f "${PID_FILE}" ]]; then
    local pid
    pid="$(cat "${PID_FILE}")"
    if kill -0 "${pid}" >/dev/null 2>&1; then
      return 0
    fi
  fi
  return 1
}

set_theme() {
  local theme="${1:-minimal}"
  local src="${STYLES_DIR}/extra-${theme}.css"

  if [[ ! -f "${src}" ]]; then
    echo "Unknown theme: ${theme}"
    echo "Run: scripts/docs-preview.sh list"
    exit 1
  fi

  cp "${src}" "${ACTIVE_CSS}"
  touch "${ACTIVE_CSS}"
  echo "Active theme: ${theme}"
}

list_themes() {
  echo "Available themes:"
  echo "  - neon"
  echo "  - glass"
  echo "  - minimal"
}

start_server() {
  local theme="${1:-minimal}"

  if is_running; then
    echo "Preview already running."
    "${ROOT_DIR}/scripts/docs-preview.sh" status
    exit 0
  fi

  if [[ -f "${PID_FILE}" ]]; then
    rm -f "${PID_FILE}"
  fi

  ensure_mkdocs
  ensure_styles_dir
  ensure_include_dir
  set_theme "${theme}"

  local selected_port
  if ! selected_port="$(pick_port "${PORT}")"; then
    echo "No available port found between ${PORT} and 8010."
    exit 1
  fi

  ADDR="${HOST}:${selected_port}"
  echo "${ADDR}" > "${ADDR_FILE}"

  local serve_args=("${VENV_BIN}/mkdocs" "serve" "-f" "${ROOT_DIR}/mkdocs.yml" "-a" "${ADDR}")
  if [[ "${LIVERELOAD}" != "1" ]]; then
    serve_args+=("--no-livereload")
  fi

  (
    cd "${ROOT_DIR}"
    nohup "${serve_args[@]}" > "${LOG_FILE}" 2>&1 &
    echo $! > "${PID_FILE}"
  )

  sleep 0.7
  if ! is_running; then
    rm -f "${PID_FILE}"
    if [[ "${LIVERELOAD}" == "1" ]]; then
      echo "Live reload failed. Retrying without livereload..."
      LIVERELOAD="0" start_server "${theme}"
      return 0
    fi
    echo "Preview failed to start. Check ${LOG_FILE} for details."
    exit 1
  fi

  echo "Preview started at http://${ADDR}"
  echo "Log file: ${LOG_FILE}"
}

stop_server() {
  if ! is_running && [[ ! -f "${ADDR_FILE}" ]]; then
    kill_port_listeners "${PORT}"
    if ! is_running; then
      echo "No preview process is running."
      exit 0
    fi
  fi

  if [[ -f "${PID_FILE}" ]]; then
    local pid
    pid="$(cat "${PID_FILE}")"
    kill "${pid}" >/dev/null 2>&1 || true
  fi

  for _ in {1..20}; do
    if [[ -f "${PID_FILE}" ]] && kill -0 "$(cat "${PID_FILE}")" >/dev/null 2>&1; then
      sleep 0.2
      continue
    fi

    if [[ -f "${ADDR_FILE}" ]]; then
      local addr port
      addr="$(cat "${ADDR_FILE}")"
      port="${addr##*:}"
      kill_port_listeners "${port}"
    else
      kill_port_listeners "${PORT}"
    fi

    if ! is_running; then
      rm -f "${PID_FILE}"
      rm -f "${ADDR_FILE}"
      echo "Preview stopped."
      return 0
    fi
  done

  echo "Preview still running; try: kill -9 ${pid}"
}

status_server() {
  if is_running; then
    local pid
    pid="$(cat "${PID_FILE}")"
    if [[ -f "${ADDR_FILE}" ]]; then
      ADDR="$(cat "${ADDR_FILE}")"
    fi
    echo "Preview running (pid ${pid}) at http://${ADDR}"
    echo "Log file: ${LOG_FILE}"
  else
    echo "Preview not running."
  fi
}

command="${1:-}"
case "${command}" in
  start)
    start_server "${2:-minimal}"
    ;;
  switch)
    shift
    set_theme "${1:-}"
    ;;
  stop)
    stop_server
    ;;
  status)
    status_server
    ;;
  list)
    list_themes
    ;;
  -h|--help|"")
    usage
    ;;
  *)
    echo "Unknown command: ${command}"
    usage
    exit 1
    ;;
esac
