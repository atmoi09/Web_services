#!/bin/bash
set -e

#./build.sh

# Update the set of services and
# build and execute the system tests
pushd end-to-end-tests
./deploy.sh 
sleep 5
./test.sh
sleep 5
./stop_service.sh
popd

# Cleanup the build images
docker image prune -f

