#!/bin/bash

./gradlew quarkusBuild

docker build -f src/main/docker/Dockerfile.jvm -t explorviz/landscape-service-jvm .

kind load docker-image explorviz/landscape-service-jvm:latest

kubectl apply -f manifest.yml