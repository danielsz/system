#!/usr/bin/env bash
#
# Creates a test user and role for running the PostgreSQL test cases.
# In order to run this, you must be a PostgreSQL superuser.  Please
# note that JDBC connections are made over TCP/IP with password
# authentication, so you need to modify pg_hba.conf accordingly.
#
# Usage:
#
#    $ su - postgres  # Optional, run this as 'postgres' user
#    $ script/pg_test_setup.sh
#
#    Or:
#    $ sudo -u postgres bash pg_test_setup.sh
#

if [ "$(whoami)" != 'postgres' ]; then
    echo "Running this script requires PostgreSQL superuser access. Consider 'su - postgres' to run this as the default PostgreSQL superuser."
fi

command -v createdb >/dev/null 2>&1 || {
    echo >&2 "Cannot find 'createdb', please make sure Postgres is installed and configured"
    exit 1
}

command -v createuser >/dev/null 2>&1 || {
    echo >&2 "Cannot find 'createuser', please make sure Postgres is installed and configured"
    exit 1
}

echo "Creating test user..."
createuser system_test_user

echo "Creating test database..."
createdb system_test_db -O system_test_user
