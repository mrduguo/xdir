#!/bin/sh -ex

#build
#scp & unpack binary
#copy config
#copy data
#copy overlay
#shutdown old instance
#update init script pointer
#start new instance




#build
mvn clean install
sed -n '/\<version\>/,/\<\/version\>/p' pom.xml
ls  ./dist/bin/target/xdir-dist-bin-*.tar.gz | cut -d'_' -f 2

#scp & unpack binary
scp ./dist/bin/target/xdir-dist-bin-*.tar.gz root@duguo.org:
ssh root@duguo.org tar -xzf xdir-dist-bin-*.tar.gz
ssh root@duguo.org rm xdir-dist-bin-*.tar.gz
version=`ssh root@duguo.org ls xdir-dist-bin-*`;echo $version



#copy config
#copy data
#copy overlay
#shutdown old instance
#update init script pointer
#start new instance