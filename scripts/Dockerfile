FROM java:8u111-jdk-alpine

RUN set -eux; \
    rm -fr /var/cache/apk/*
    mkdir -p /opt/palantir/services; \

ADD recipe-example-server/ /opt/palantir/services/recipe-example-server

RUN chmod +x /opt/palantir/services/recipe-example-server/bin/recipe-example-server

EXPOSE 8000

WORKDIR /opt/palantir/services/recipe-example-server/
ENTRYPOINT ["./bin/recipe-example-server", "server", "var/conf/recipes.yml"]
