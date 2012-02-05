#!/bin/sh
./dist/bin/target/xdir-dist-bin-0.9.2-SNAPSHOT/bin/xdir.sh stop
mvn clean install -DskipTests -f core/pom.xml && mvn clean install -DskipPackBinary -f dist/bin/pom.xml && chmod a+x ./dist/bin/target/xdir-dist-bin-0.9.2-SNAPSHOT/bin/xdir.sh &&  ./dist/bin/target/xdir-dist-bin-0.9.2-SNAPSHOT/bin/xdir.sh start -console
