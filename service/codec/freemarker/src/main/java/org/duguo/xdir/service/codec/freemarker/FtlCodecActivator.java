package org.duguo.xdir.service.codec.freemarker;

import org.duguo.xdir.spi.service.DynamicService;
import org.duguo.xdir.spi.service.StringCodec;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Dictionary;
import java.util.Hashtable;

public class FtlCodecActivator implements BundleActivator {

    private ServiceRegistration registration;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        registration=registerService(bundleContext, new FtlCodec());
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        registration.unregister();
    }

    protected ServiceRegistration registerService(BundleContext bundleContext,DynamicService serviceInstance) throws Exception {
        Dictionary properties=new Hashtable();
        properties.put(DynamicService.class.getName(),serviceInstance.getServiceName());
        return bundleContext.registerService(
                new String[]{StringCodec.class.getName(), DynamicService.class.getName()},
                serviceInstance,
                properties);
    }
}
