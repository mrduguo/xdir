package org.duguo.xdir.infra.http.rest.impl.resource;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.duguo.xdir.infra.http.rest.api.model.ComponentInfo;
import org.duguo.xdir.infra.http.rest.api.model.ComponentsInfo;
import org.duguo.xdir.infra.http.rest.api.model.PingOk;
import org.duguo.xdir.infra.http.rest.api.model.ServerInfo;
import org.duguo.xdir.infra.http.rest.api.resource.ServerResource;
import org.duguo.xdir.infra.spi.component.TestableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.lang.management.ManagementFactory;
import java.util.*;

public class ServerResourceImpl implements ServerResource,ApplicationContextAware,TestableComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ApplicationContext applicationContext;
    private String componentName = "Rest Api Server";

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
    public ComponentsInfo components() {
        ComponentsInfo componentsInfo = new ComponentsInfo();
        collectComponentsStatus(componentsInfo);
        sortComponentsByName(componentsInfo);
        returnAsPartialConentIfHasFailedComponent(componentsInfo);
        return componentsInfo;
    }

    @Override
    public PingOk ping() {
        return PingOk.INSTANCE;
    }


    private void returnAsPartialConentIfHasFailedComponent(ComponentsInfo componentsInfo) {
        if(componentsInfo.isHasFailedComponent()){
            throw new WebApplicationException(Response.status(HttpServletResponse.SC_PARTIAL_CONTENT).entity(componentsInfo).build());
        }
    }

    private void collectComponentsStatus(ComponentsInfo componentsInfo) {
        Map<String,TestableComponent> testableComponentMap = applicationContext.getBeansOfType(TestableComponent.class);
        for(TestableComponent testableComponent:testableComponentMap.values()){
            ComponentInfo componentInfo = new ComponentInfo();
            componentsInfo.add(componentInfo);
            componentInfo.setName(testableComponent.getName());
            try{
                long startTimestamp=System.currentTimeMillis();
                if(testableComponent.performTest()){
                    componentInfo.setResponseTime(System.currentTimeMillis()-startTimestamp);
                    componentInfo.setStatus("OK");
                    continue;
                }else{
                    logger.error("Component "+testableComponent.getName()+" test failed without exception");
                }
            }catch (Throwable ex){
                logger.error("Component "+testableComponent.getName()+" test failed",ex);
            }
            componentInfo.setStatus("FAILED");
            componentsInfo.setHasFailedComponent(true);
        }
    }

    private void sortComponentsByName(ComponentsInfo componentsInfo) {
        Collections.sort(componentsInfo.getComponents(), new Comparator<ComponentInfo>() {
            @Override
            public int compare(ComponentInfo o1, ComponentInfo o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
    @Override
    public String getName() {
        return componentName;
    }

    /**
     *
     * @return always return true if user can reach this endpoint.
     * @throws Exception - never happens
     */
    @Override
    public boolean performTest() throws Exception {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}