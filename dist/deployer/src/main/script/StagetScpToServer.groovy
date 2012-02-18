
def sshCommand(cmd, ignoreError = false) {
    execShellCommand("ssh root@${project.properties.targetHostName} $cmd", ignoreError)
}

def execShellCommand(cmd, ignoreError = false) {
    println new Date().toLocaleString()+" executing $cmd"
    proc = cmd.execute()
    def result=new StringBuilder()
    proc.in.eachLine { line ->
        println line
        result.append(line)
        result.append("\n")
    }
    proc.err.eachLine { line ->
        println "ERR: "+line
    }
    proc.waitFor()
    if (proc.exitValue()) {
        println proc.err.text
        if (ignoreError)
            return null
        else
            fail("exec $cmd failed")
    }
    result
}

def deployScript(line) {
    new File("${project.basedir}/target/classes/deployer/deploy.sh") << line+'\n'
}

def timeout = 120 * 1000

deployScript('#!/bin/bash -ex')
deployScript('cd /opt/xdir/deployer')
deployScript("tar -xzf xdir-dist-bin-${project.version}.tar.gz")
deployScript("touch xdir-dist-bin-${project.version}")

deployScript("tempDeployment='/opt/xdir/deployer/xdir-dist-bin-${project.version}'")
deployScript("newDeployment='/opt/xdir/xdir-${project.version}-" + new Date().format("yyMMdd-HHmmss")+"'")
def previousDeployment = sshCommand("file /opt/xdir/CURRENT", true)
if (previousDeployment != null) {
    previousDeployment = previousDeployment.substring(previousDeployment.indexOf("`") + 1, previousDeployment.indexOf("'"))
    deployScript("previousDeployment='$previousDeployment'")
    deployScript('cp -r overlay/* \$tempDeployment/')
    deployScript('cp \$previousDeployment/data/conf/osgi.properties \$tempDeployment/data/conf/osgi.properties')
    deployScript('wget -O \$previousDeployment/backup-website.zip http://127.0.0.1/admin/resources/jcr/xdir/default/websites.jcr')
    deployScript('jar -xf \$previousDeployment/backup-website.zip')
    deployScript('cp -r websites/duguo.org \$tempDeployment/data/jcr/repos/xdir/init/websites')
    deployScript('cp -r websites/admin/org/users \$tempDeployment/data/jcr/repos/xdir/init/websites/admin/org')
    deployScript('cp -r websites/admin/pages/account/login \$tempDeployment/data/jcr/repos/xdir/init/websites/admin/pages/account')
}
deployScript("mv \$tempDeployment \$newDeployment")


if (previousDeployment != null) {
    deployScript("/etc/init.d/xdir stop")
    deployScript("rm /opt/xdir/CURRENT")
}
deployScript("ln -s \$newDeployment /opt/xdir/CURRENT")
if (previousDeployment == null) {
    deployScript("ln -s /opt/xdir/CURRENT/bin/xdir.sh /etc/init.d/xdir")
    deployScript("update-rc.d xdir defaults")
}
deployScript("/etc/init.d/xdir start")
deployScript("cd /opt/xdir")
deployScript("rm -rf /opt/xdir/deployer")



org.apache.commons.io.FileUtils.copyFileToDirectory(new File("${project.basedir}/../bin/target/xdir-dist-bin-${project.version}.tar.gz"),new File("${project.basedir}/target/classes/deployer"))
if(previousDeployment==null){
    sshCommand('mkdir -p /opt/xdir')
}
execShellCommand("rsync --checksum --recursive --delete ${project.basedir}/target/classes/deployer root@${project.properties.targetHostName}:/opt/xdir")
sshCommand("chmod a+x /opt/xdir/deployer/deploy.sh; /opt/xdir/deployer/deploy.sh")