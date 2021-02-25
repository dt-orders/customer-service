#!/bin/bash

REPOSITORY=$1
VERSION_TAG=$2

if [ -z "$REPOSITORY" ]
then
    REPOSITORY=dtdemos
fi

if [ -z "$VERSION_TAG" ]
then
    VERSION_TAG=1
fi

LAUNCH_DARKLY_SDK_KEY=$(cat creds.json | jq -r '.launch_darkly_sdk_key')

if [ -z "$LAUNCH_DARKLY_SDK_KEY" ]
then
    echo "Running with NO LAUNCH_DARKLY_SDK_KEY.  If you want it, then set in your creds.json file."
else
    echo "Running with LAUNCH_DARKLY_SDK_KEY = $LAUNCH_DARKLY_SDK_KEY"
fi

IMAGE=dt-orders-customer-service-ld
FULLIMAGE=$REPOSITORY/$IMAGE:$VERSION_TAG

echo "Compiling Java"
./mvnw clean package -Dmaven.test.skip=true
./writeManifest.sh

echo ""
echo "=============================================================="
echo "Ready to build image $FULLIMAGE ?"
echo "=============================================================="
read -rsp "Press ctrl-c to abort. Press any key to continue"
echo ""
echo "Building: $FULLIMAGE"
docker build -t $FULLIMAGE .

echo ""
echo "=============================================================="
echo "Ready to Run $FULLIMAGE"
echo "=============================================================="
read -rsp "Press ctrl-c to abort. Press any key to continue"
echo ""
echo "access app @ http://localhost:8080"
echo "" 

docker run -it -p 8080:8080 \
    --env LAUNCH_DARKLY_SDK_KEY=$LAUNCH_DARKLY_SDK_KEY \
    $FULLIMAGE

echo ""
echo "=============================================================="
echo "Ready to push $FULLIMAGE"
echo "=============================================================="
read -rsp "Press ctrl-c to abort. Press any key to continue"

docker push $FULLIMAGE