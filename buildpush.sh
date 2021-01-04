#!/bin/bash

clear

REPOSITORY=$1

if [ -z "$REPOSITORY" ]
then
    REPOSITORY=dtdemos
fi

GITHUB_ACCOUNT=$1
IMAGE=$REPOSITORY/dt-orders-customer-service

#./mvnw clean package
./mvnw clean package -Dmaven.test.skip=true

docker build -t $IMAGE:1 . --build-arg APP_VERSION=1
docker build -t $IMAGE:2 . --build-arg APP_VERSION=2
docker build -t $IMAGE:3 . --build-arg APP_VERSION=3

echo ""
echo "Ready to push images?"
read -rsp "Press ctrl-c to abort. Press any key to continue"

docker push $IMAGE:1
docker push $IMAGE:2
docker push $IMAGE:3