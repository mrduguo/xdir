#!/bin/sh -ex
chmod a+x ./dist/bin/target/xdir-dist-bin-*/bin/xdir.sh
export XDIR_OPTS="-Dfile.encoding=UTF-8 -Ddebug -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=127.0.0.1:8000"
./dist/bin/target/xdir-dist-bin-*/bin/xdir.sh start
sleep 1
tail -f ./dist/bin/target/xdir-dist-bin-*/var/logs/xdir.log
