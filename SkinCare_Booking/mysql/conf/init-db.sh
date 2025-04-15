#!/bin/sh
set -e

# Create a copy of the template
cp /docker-entrypoint-initdb.d/init.sql.template /docker-entrypoint-initdb.d/init.sql

# Replace environment variables in the SQL file using sed
sed -i "s/\${MYSQL_DATABASE}/$MYSQL_DATABASE/g" /docker-entrypoint-initdb.d/init.sql
sed -i "s/\${MYSQL_USER}/$MYSQL_USER/g" /docker-entrypoint-initdb.d/init.sql
sed -i "s/\${MYSQL_PASSWORD}/$MYSQL_PASSWORD/g" /docker-entrypoint-initdb.d/init.sql

echo "Initialization SQL script created with environment variables."