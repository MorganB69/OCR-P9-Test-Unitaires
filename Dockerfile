FROM postgres:9.4
COPY ./init/db/docker-entrypoint-initdb.d /docker-entrypoint-initdb.d