#!/usr/bin/env bash

# wrapper for docker-compose
# all arguments are passed to docker-compose up

set -eux -o pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

# stop the services and clean up docker images
function clean() {
    set +e
    if [ -z "${prog_args}" ] || [ -n "${prog_args/*-d*/}" ]; then
        docker-compose down -v
    fi
    docker image prune -f
}

# set common compose args
function docker-compose() {
    if [ "${!compose_args*}" = "compose_args" ] && [ ${#compose_args[@]} -gt 0 ]; then
        command docker-compose "${compose_args[@]}" "${@}"
    else
        command docker-compose "${@}"
    fi
}

prog_args="${*:-}"
trap clean exit

"${RUN:-true}" || exit 0

declare -a deps=(postgres)

test ${#deps[@]} -eq 0 || docker-compose pull "${deps[@]}"
docker-compose build --pull
if [ ${#} -gt 0 ]; then
    docker-compose up -V "${@}"
else
    docker-compose up -V
fi
