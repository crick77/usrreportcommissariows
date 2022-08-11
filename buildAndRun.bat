@echo off
call mvn clean package
call docker build -t it.usr/usrreportcommissariows .
call docker rm -f usrreportcommissariows
call docker run -d -p 9080:9080 -p 9443:9443 --name usrreportcommissariows it.usr/usrreportcommissariows