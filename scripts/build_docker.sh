#!/usr/bin/env bash
set -ex
cd "$(dirname "${BASH_SOURCE[0]}" )"/..

VERSION=$(git describe --tags --always --first-parent)
DEST=build/docker

rm -rf $DEST
mkdir -p $DEST/recipe-example-server/var/conf

cp ./scripts/Dockerfile $DEST/
tar -xf "./recipe-example-server/build/distributions/recipe-example-server-${VERSION}.tar" -C $DEST/recipe-example-server --strip-components=1
cp ./recipe-example-server/var/conf/conf.yml $DEST/recipe-example-server/var/conf

cd $DEST
docker build -t "palantirtechnologies/recipe-example-server:$VERSION" .
docker tag "palantirtechnologies/recipe-example-server:$VERSION" "palantirtechnologies/recipe-example-server:latest"

