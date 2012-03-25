#!/bin/sh -ex
mvn clean install -DskipTests -f jersey/pom.xml
./bin/target/xdir-rest-bin-*/bin/xdir.sh stop

cp ./jersey/target/xdir-rest*.jar ./bin/target/xdir-rest-bin-*/bundles/system/xdir/rest/

export XDIR_OPTS="-Dfile.encoding=UTF-8 -Ddebug -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=127.0.0.1:8000"
./bin/target/xdir-rest-bin-*/bin/xdir.sh start
sleep 1
tail -f ./bin/target/xdir-rest-bin-*/var/logs/xdir.log
