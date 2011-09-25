#!/bin/sh
./dist/bin/target/xdir-dist-bin-0.9.0-SNAPSHOT/bin/xdir.sh stop
mvn clean install -DskipPackBinary && chmod a+x ./dist/bin/target/xdir-dist-bin-0.9.0-SNAPSHOT/bin/xdir.sh &&  ./dist/bin/target/xdir-dist-bin-0.9.0-SNAPSHOT/bin/xdir.sh start
mvn -P craw-links-it clean install -f tests/functional/pom.xml
