#!/bin/bash

clear

REPOSITORY=$1

if [ -z "$REPOSITORY" ]
then
    REPOSITORY=dtdemos
fi

IMAGE=$REPOSITORY/dt-orders-customer-service

#./mvnw clean package
echo "Compiling Java"
./mvnw clean package -Dmaven.test.skip=true
./writeManifest.sh

docker build -t $IMAGE:1 . --build-arg APP_VERSION=1
docker build -t $IMAGE:2 . --build-arg APP_VERSION=2
docker build -t $IMAGE:3 . --build-arg APP_VERSION=3
docker build -t $IMAGE:4 . --build-arg APP_VERSION=4
docker build -t $IMAGE:5 . --build-arg APP_VERSION=5

docker tag $IMAGE:1 $IMAGE:1.0.0
docker tag $IMAGE:2 $IMAGE:2.0.0
docker tag $IMAGE:3 $IMAGE:3.0.0
docker tag $IMAGE:3 $IMAGE:4.0.0
docker tag $IMAGE:3 $IMAGE:5.0.0

echo "========================================================"
echo "Ready to push images ?"
echo "========================================================"
read -rsp "Press ctrl-c to abort. Press any key to continue"

echo "Pushing $IMAGE:1"
docker push $IMAGE:1
docker push $IMAGE:1.0.0

echo "Pushing $IMAGE:2"
docker push $IMAGE:2
docker push $IMAGE:2.0.0

echo "Pushing $IMAGE:3"
docker push $IMAGE:3
docker push $IMAGE:3.0.0

echo "Pushing $IMAGE:4"
docker push $IMAGE:4
docker push $IMAGE:4.0.0

echo "Pushing $IMAGE:5"
docker push $IMAGE:5
docker push $IMAGE:5.0.0