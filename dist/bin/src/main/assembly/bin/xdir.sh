#!/bin/bash
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# XDir Server Start Up Batch script
#
# Required ENV vars:
# ------------------
#   JAVA_HOME - location of a JDK home dir
#
# Optional ENV vars
# -----------------
#   XDIR_HOME - location of XDir's installed home dir
#   XDIR_OPTS - parameters passed to the Java VM when running server
#     e.g. to debug Maven itself, use
#if [ "x$1" = "xstart" ] ; then
#    XDIR_OPTS="-Ddebug -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=127.0.0.1:8000"
#fi
PROGNAME="$0"
ACTION="$1"

warn() {
    echo "WARN: ${PROGNAME}: $*"
}

die() {
    echo "ERROR: ${PROGNAME}: $*"
    exit 1
}

detectOS() {
    # OS specific support (must be 'true' or 'false').
    darwin=false;
    aix=false;
    os400=false;
    case "`uname`" in
        Darwin*)
            darwin=true
            ;;
        AIX*)
            aix=true
            ;;
        OS400*)
            os400=true
            ;;
    esac
    # For AIX, set an environment variable
    if $aix; then
         export LDR_CNTRL=MAXDATA=0xB0000000@DSA
         export IBM_JAVA_HEAPDUMP_TEXT=true
         echo $LDR_CNTRL
    fi
}


locateHome() {
	if [ -z "$XDIR_HOME" ] ; then
	  PRG="$PROGNAME"

	  # need this for relative symlinks
	  while [ -h "$PRG" ] ; do
	    ls=`ls -ld "$PRG"`
	    link=`expr "$ls" : '.*-> \(.*\)$'`
	    if expr "$link" : '/.*' > /dev/null; then
	      PRG="$link"
	    else
	      PRG="`dirname "$PRG"`/$link"
	    fi
	  done

	  saveddir=`pwd`

	  XDIR_HOME=`dirname "$PRG"`/..

	  # make it fully qualified
	  XDIR_HOME=`cd "$XDIR_HOME" && pwd`

	  cd "$saveddir"
	fi

        if [ ! -d "$XDIR_HOME/bin" ]; then
            die "XDIR_HOME is not valid: $XDIR_HOME"
        fi
}


locateJava() {
    # Setup the Java Virtual Machine

    if [ "x$JAVA" = "x" ]; then
	if [ -f "$XDIR_HOME/../jvm/linux/bin/java" ]; then
		JAVA_HOME="$XDIR_HOME/../jvm/linux"
	fi

        if [ "x$JAVA_HOME" = "x" ] && [ "$darwin" = "true" ]; then
            JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home
        fi
        if [ "x$JAVA_HOME" != "x" ]; then
            if [ ! -d "$JAVA_HOME" ]; then
                die "JAVA_HOME is not valid: $JAVA_HOME"
            fi
            JAVA="$JAVA_HOME/bin/java"
        else
            JAVA="java"
        fi
    fi
}


setupClasspath() {
    # Add the jars in the boot dir
    CLASSPATH="$XDIR_HOME/data/conf"
    for file in $XDIR_HOME/boot/*.jar
    do
        if [ -z "$CLASSPATH" ]; then
            CLASSPATH="$file"
        else
            CLASSPATH="$CLASSPATH:$file"
        fi
    done
}


init(){
    # Determine if there is special OS handling we must perform
    detectOS

    # Locate the XDir home directory
    locateHome

    # Locate the Java VM to execute
    locateJava

    setupClasspath
}

main() {
    init

    FINAL_COMMAND="$JAVA  -classpath $CLASSPATH -Dfile.encoding=UTF-8 -Dxdir.home=$XDIR_HOME $XDIR_OPTS org.duguo.xdir.osgi.bootstrap.Main $@"

    #echo "FINAL_COMMAND: $FINAL_COMMAND"
    if [ "$ACTION" = "start" ]; then
        mkdir -p $XDIR_HOME/var/logs
	    nohup $FINAL_COMMAND >& $XDIR_HOME/var/logs/console.log </dev/null &
	    sleep 2
        echo "XDir Started with log at $XDIR_HOME/var/logs/xdir.log"
    else
        $FINAL_COMMAND
    fi
    EXIT_CODE="$?"
}


case "$ACTION" in
    start)
        main "$@"
        ;;
    run)
        shift
        main "start" "$@"
        ;;

    stop)
        main "$@"
	    sleep 2
        ;;

    clean)
        main "stop"
		rm -rf $XDIR_HOME/var/*
        main "start"
        ;;

    status)
        main "$@"
        ;;

    restart)
        $0 clean
        $0 start
        ;;

    *)
    	echo "Usage: $0 {start|run|stop|status|restart|clean}" >&2
        exit 3
        ;;
esac

exit $EXIT_CODE