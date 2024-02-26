call build.bat

cd end-to-end-tests
call deploy.bat
timeout /t 5
call test.bat
timeout /t 5

cd ..

call docker image prune -f

pause