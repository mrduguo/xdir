#!/bin/sh -x
mvn clean install -DskipTests
if [ "$?" != "0" ]; then
    exit -1
fi


export XDIR_OPTS="-Dfile.encoding=UTF-8 -Ddebug -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=127.0.0.1:8000"
chmod a+x ./bin/target/xdir-rest-bin-*/bin/xdir.sh &&  ./bin/target/xdir-rest-bin-*/bin/xdir.sh start
sleep 1
tail -f ./bin/target/xdir-rest-bin-*/var/logs/xdir.log
