#!/bin/sh
mvn clean package && docker build -t it.usr/usrreportcommissariows .
docker rm -f usrreportcommissariows || true && docker run -d -p 9080:9080 -p 9443:9443 --name usrreportcommissariows it.usr/usrreportcommissariows