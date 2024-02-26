cd messaging-utilities-3.3
call buildwoclean.bat
cd..

cd token-service
call buildwoclean.bat
cd ..

cd account-service
call buildwoclean.bat
cd ..

cd payment-service
call buildwoclean.bat
cd ..

cd dtu-pay-service
call buildwoclean.bat
cd ..

timeout /t 5