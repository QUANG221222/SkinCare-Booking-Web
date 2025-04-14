FROM mysql:8

# Copy configuration file with read and execute permissions
COPY ./conf/my.cnf /etc/mysql/my.cnf
RUN chmod 500 /etc/mysql/my.cnf

# Copy SQL template and initialization script
COPY ./conf/init.sql.template /docker-entrypoint-initdb.d/init.sql.template
COPY ./conf/init-db.sh /docker-entrypoint-initdb.d/init-db.sh
RUN chmod +x /docker-entrypoint-initdb.d/init-db.sh