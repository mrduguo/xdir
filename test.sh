#!/bin/bash  -x
./dist/bin/target/xdir-dist-bin-*/bin/xdir.sh stop
mvn clean install -Ddeploy-build $@

#  params:
#  -DwaitForStop -Dfirefox