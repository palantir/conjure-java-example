FROM openjdk:15

RUN set -eux; \
    mkdir -p /opt/palantir/services;

ADD recipe-example-server/ /opt/palantir/services/recipe-example-server

RUN chmod +x /opt/palantir/services/recipe-example-server/bin/recipe-example-server

EXPOSE 8345

WORKDIR /opt/palantir/services/recipe-example-server/
ENTRYPOINT ["./bin/recipe-example-server", "server", "var/conf/conf.yml"]
