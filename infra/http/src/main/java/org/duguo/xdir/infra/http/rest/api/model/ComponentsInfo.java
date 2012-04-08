package org.duguo.xdir.infra.http.rest.api.model;


import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;


/**
 * component information
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "hasFailedComponent","components"
})
@XmlRootElement(name = "components")
public class ComponentsInfo {
    protected boolean hasFailedComponent;
    @XmlElement(name="component")
    protected final List<ComponentInfo> components = new LinkedList<ComponentInfo>();

    public boolean isHasFailedComponent() {
        return hasFailedComponent;
    }

    public void setHasFailedComponent(boolean hasFailedComponent) {
        this.hasFailedComponent = hasFailedComponent;
    }

    public void add(ComponentInfo componentInfo){
        components.add(componentInfo);
    }

    public List<ComponentInfo> getComponents() {
        return components;
    }
}