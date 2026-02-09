#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCS_DIR="${ROOT_DIR}/documentation"
PID_FILE="${ROOT_DIR}/.docusaurus-preview.pid"
LOG_FILE="${ROOT_DIR}/.docusaurus-preview.log"
ADDR_FILE="${ROOT_DIR}/.docusaurus-preview.addr"

HOST="${HOST:-127.0.0.1}"
PORT="${PORT:-3000}"
ADDR="${HOST}:${PORT}"

usage() {
  cat <<'USAGE'
Usage: scripts/docs-preview.sh <command>

Commands:
  start         Install dependencies (if needed) and start Docusaurus dev server
  stop          Stop the running preview server
  status        Show status and preview URL
  build         Build the static site for production

Environment:
  HOST (default: 127.0.0.1)
  PORT (default: 3000)
USAGE
}

ensure_node() {
  if ! command -v node >/dev/null 2>&1; then
    echo "node not found. Please install Node.js 18+."
    exit 1
  fi
  local node_major
  node_major="$(node -v | sed 's/v//' | cut -d. -f1)"
  if [[ "${node_major}" -lt 18 ]]; then
    echo "Node.js 18+ required. Found: $(node -v)"
    exit 1
  fi
}

ensure_deps() {
  ensure_node
  if [[ ! -d "${DOCS_DIR}/node_modules" ]]; then
    echo "Installing Docusaurus dependencies..."
    (cd "${DOCS_DIR}" && npm install)
  fi
}

is_port_available() {
  local port="${1}"
  if command -v lsof >/dev/null 2>&1; then
    if lsof -t -iTCP:"${port}" -sTCP:LISTEN >/dev/null 2>&1; then
      return 1
    fi
  fi
  return 0
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
  for try_port in "${base_port}" 3001 3002 3003 3004 3005; do
    if is_port_available "${try_port}"; then
      echo "${try_port}"
      return 0
    fi
  done
  return 1
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

start_server() {
  if is_running; then
    echo "Preview already running."
    status_server
    exit 0
  fi

  if [[ -f "${PID_FILE}" ]]; then
    rm -f "${PID_FILE}"
  fi

  ensure_deps

  local selected_port
  if ! selected_port="$(pick_port "${PORT}")"; then
    echo "No available port found between ${PORT} and 3005."
    exit 1
  fi

  ADDR="${HOST}:${selected_port}"
  echo "${ADDR}" > "${ADDR_FILE}"

  (
    cd "${DOCS_DIR}"
    nohup npx docusaurus start --host "${HOST}" --port "${selected_port}" > "${LOG_FILE}" 2>&1 &
    echo $! > "${PID_FILE}"
  )

  sleep 2
  if ! is_running; then
    rm -f "${PID_FILE}"
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

  echo "Preview may still be running; check manually."
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

build_site() {
  ensure_deps
  echo "Building Docusaurus site..."
  (cd "${DOCS_DIR}" && npx docusaurus build)
  echo "Build complete. Output in: ${DOCS_DIR}/build/"
}

command="${1:-}"
case "${command}" in
  start)
    start_server
    ;;
  stop)
    stop_server
    ;;
  status)
    status_server
    ;;
  build)
    build_site
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
