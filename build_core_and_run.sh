#!/bin/sh
./dist/bin/target/xdir-dist-bin-*/bin/xdir.sh stop
mvn clean install -DskipTests -f core/pom.xml && mvn clean install -DskipPackBinary -f dist/bin/pom.xml
if [ "$?" != "0" ]; then
    exit -1
fi

cp -R ./dist/bin/src/test/local/overlay/* ./dist/bin/target/xdir-dist-bin-*/
chmod a+x ./dist/bin/target/xdir-dist-bin-*/bin/xdir.sh &&  ./dist/bin/target/xdir-dist-bin-*/bin/xdir.sh start
tail -f ./dist/bin/target/xdir-dist-bin-*/var/logs/xdir.log
