package org.duguo.xdir.service.codec.freemarker;

import org.duguo.xdir.spi.service.DynamicService;
import org.duguo.xdir.spi.service.StringCodec;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class FtlCodecActivator implements BundleActivator {

    private ServiceRegistration registration;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        registration= bundleContext.registerService(
                new String[]{StringCodec.class.getName(), DynamicService.class.getName()},
                new FtlCodec(),
                null);
    }
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        registration.unregister();
    }
}
