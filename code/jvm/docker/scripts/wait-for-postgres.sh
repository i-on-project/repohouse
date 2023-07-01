#!/bin/sh
# wait-for-postgres.sh host cmd

set -e

host="$1"
shift
cmd="$@"

>&2 echo "Waiting for Postgres on $host"
until pg_isready -h "$host"; do
  >&2 echo "Postgres is unavailable - sleeping"
  sleep 1
done

>&2 echo "Postgres is up - executing command '$cmd'"
eval "$cmd"

<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
