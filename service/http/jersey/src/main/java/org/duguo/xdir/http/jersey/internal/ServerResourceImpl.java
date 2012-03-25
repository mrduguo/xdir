package org.duguo.xdir.http.jersey.internal;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.duguo.xdir.http.jersey.api.model.ServerInfo;
import org.duguo.xdir.http.jersey.api.resource.ServerResource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.lang.management.ManagementFactory;

public class ServerResourceImpl implements ServerResource {
    

    @Override
    public ServerInfo info() {
        ServerInfo serverInfo=new ServerInfo();
        serverInfo.setVersion(System.getProperty("xdir.server.version"));

        serverInfo.setBuildTime(System.getProperty("xdir.server.build.time"));

        long jvmUpTime = ManagementFactory.getRuntimeMXBean().getUptime();
        serverInfo.setUpTime(DurationFormatUtils.formatDurationWords(jvmUpTime,true,true));

        return serverInfo;
    }

    @Override
    public String ping() {
        if(System.getProperty("xdir.server.started.msg")==null){
            throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
        }else{
            return "OK";
        }
    }
}