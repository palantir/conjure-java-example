FROM java:8u111-jdk-alpine

RUN set -eux; \
    apk --update add tar; \
    mkdir -p /opt/palantir/services/recipe-example-server/; \
    mkdir -p /opt/palantir/services/recipe-example-server/var/conf

COPY recipe-example-server*.tar /opt/palantir/services/recipe-example-server/server.tar
RUN tar -xf /opt/palantir/services/recipe-example-server/server.tar -C  /opt/palantir/services/recipe-example-server/ --strip-components=1
ADD recipes.yml /opt/palantir/services/recipe-example-server/var/conf

RUN chmod +x /opt/palantir/services/recipe-example-server/bin/recipe-example-server

EXPOSE 8000

WORKDIR /opt/palantir/services/recipe-example-server/
ENTRYPOINT ["./bin/recipe-example-server", "server", "var/conf/recipes.yml"]
