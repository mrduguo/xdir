#!/bin/bash  -x
mvn clean install -Ddeploy-build $@

#  params:
#  -DwaitForStop -Dfirefox