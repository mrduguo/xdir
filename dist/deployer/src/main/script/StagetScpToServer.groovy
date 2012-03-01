
def sshCommand(cmd, ignoreError = false) {
    execShellCommand("ssh root@${project.properties.targetHostName} $cmd", ignoreError)
}

def execShellCommand(cmd, ignoreError = false) {
    println new Date().toLocaleString()+" executing $cmd"
    proc = cmd.execute()
    def result=new StringBuilder()
    Thread.start{
        proc.in.eachLine { line ->
            println line
            result.append(line)
            result.append("\n")
        }
    }
    Thread.start{
        proc.err.eachLine { line ->
            println "ERR: "+line
        }
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
    println "previousDeployment is:"+previousDeployment
    deployScript("previousDeployment='$previousDeployment'")
    deployScript('cp -r overlay/* \$tempDeployment/')
    deployScript('cp \$previousDeployment/data/conf/osgi.properties \$tempDeployment/data/classes/osgi.properties')
    deployScript('wget -O \$previousDeployment/backup-apps.zip http://127.0.0.1/admin/resources/jcr/apps.jcr')
    deployScript('jar -xf \$previousDeployment/backup-apps.zip')
    deployScript('cp -r apps/duguo.org \$tempDeployment/data/jcr/init/import/apps')
    deployScript('echo "favicon.ico/_hidden_node=true" >> \$tempDeployment/data/jcr/init/import/apps/duguo.org/pages/jcr.properties')
    deployScript('cp -r apps/admin/org/users \$tempDeployment/data/jcr/init/import/apps/admin/org')
    deployScript('cp -r apps/admin/pages/account/login \$tempDeployment/data/jcr/init/import/apps/admin/pages/account')
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
//deployScript("rm -rf /opt/xdir/deployer")



org.apache.commons.io.FileUtils.copyFileToDirectory(new File("${project.basedir}/../bin/target/xdir-dist-bin-${project.version}.tar.gz"),new File("${project.basedir}/target/classes/deployer"))
if(previousDeployment==null){
    sshCommand('mkdir -p /opt/xdir')
}
execShellCommand("rsync --checksum --recursive --delete ${project.basedir}/target/classes/deployer root@${project.properties.targetHostName}:/opt/xdir")
sshCommand("chmod a+x /opt/xdir/deployer/deploy.sh; /opt/xdir/deployer/deploy.sh")