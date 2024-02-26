#!/bin/bash
set -e
sleep 2
docker image prune -f
sleep 2
docker-compose up -d rabbitMq
sleep 10
docker-compose up -d dtu-pay-service payment-service account-service token-service report-service

