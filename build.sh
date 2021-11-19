#!/bin/bash
cd monitor-mix
npm i
npm run build
mkdir ../proxy/src/main/resources/monitor-mix
cp -r build/* ../proxy/src/main/resources/monitor-mix
cd ../proxy
mvn clean package
