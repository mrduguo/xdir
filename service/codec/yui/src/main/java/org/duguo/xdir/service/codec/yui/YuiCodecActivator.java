package org.duguo.xdir.service.codec.yui;

import org.duguo.xdir.spi.service.DynamicService;
import org.duguo.xdir.spi.service.StringCodec;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class YuiCodecActivator implements BundleActivator {

    private List<ServiceRegistration> registrations = new ArrayList<ServiceRegistration>();

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        registrations.add(registerService(bundleContext, new CssCompressCodec()));
        registrations.add(registerService(bundleContext, new JsMinifierCodec()));
        registrations.add(registerService(bundleContext, new HtmlCompressCodec()));
        registrations.add(registerService(bundleContext, new XmlCompressCodec()));
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        for(ServiceRegistration registration:registrations)
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
