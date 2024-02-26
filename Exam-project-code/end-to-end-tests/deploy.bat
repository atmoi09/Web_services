call docker image prune -f
call docker-compose up -d rabbitMq
call timeout /t 5
call docker-compose up -d dtu-pay-service account-service token-service payment-service report-service