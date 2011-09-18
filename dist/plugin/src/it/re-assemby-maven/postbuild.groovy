assert new File(basedir, 'target/xdir-dist-it-re-assembly-maven-8.8.8/lib/maven-artifact-3.0.2.jar').exists()==true
assert new File(basedir, 'target/xdir-dist-it-re-assembly-maven-8.8.8/lib/maven-core-3.0.3.jar').exists()==true
assert new File(basedir, 'target/xdir-dist-it-re-assembly-maven-8.8.8/bin/foo.sh').exists()==true

assert new File(basedir, 'target/xdir-dist-it-re-assembly-maven-8.8.8/META_INF').exists()==false
assert new File(basedir, 'target/xdir-dist-it-re-assembly-maven-8.8.8/org/apache/maven/classrealm').exists()==false
assert new File(basedir, 'target/xdir-dist-it-re-assembly-maven-8.8.8/org/apache/maven/artifact/InvalidRepositoryException.class').exists()==false

assert new File(basedir, 'target/xdir-dist-it-re-assembly-maven-8.8.8/bin/mvnDebug').exists()==false
assert new File(basedir, 'target/xdir-dist-it-re-assembly-maven-8.8.8/boot/plexus-classworlds-2.4.jar').exists()==false
assert new File(basedir, 'target/xdir-dist-it-re-assembly-maven-8.8.8/lib/maven-core-3.0.2.jar').exists()==false