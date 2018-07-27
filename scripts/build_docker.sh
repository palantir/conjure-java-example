#!/usr/bin/env bash
set -ex
cd "$(dirname "${BASH_SOURCE[0]}" )"/..

VERSION=$(git describe --tags --always --first-parent)
DEST=build/docker

rm -rf $DEST
mkdir -p $DEST

cp ./scripts/Dockerfile $DEST/
cp "./recipe-example-server/build/distributions/recipe-example-server-${VERSION}.tar" $DEST/
cp ./recipe-example-server/var/conf/recipes.yml $DEST/

cd $DEST
docker build -t "palantir/recipe-example-server:$VERSION" .
docker tag "palantir/recipe-example-server:$VERSION" "palantir/recipe-example-server:latest"

