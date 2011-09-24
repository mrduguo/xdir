#!/bin/sh
mvn clean install && chmod a+x ./dist/bin/target/xdir-dist-bin-0.9.0-SNAPSHOT/bin/xdir.sh &&  ./dist/bin/target/xdir-dist-bin-0.9.0-SNAPSHOT/bin/xdir.sh start -debug -console
