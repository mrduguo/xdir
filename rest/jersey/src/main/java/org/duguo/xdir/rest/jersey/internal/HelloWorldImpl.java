package org.duguo.xdir.rest.jersey.internal;

import org.duguo.xdir.rest.jersey.api.model.Item;
import org.duguo.xdir.rest.jersey.api.resource.HelloWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HelloWorldImpl implements HelloWorld {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldImpl.class);
    private Item item;

    @Override
    public String getStringItem() {
        return item.toString();
    }

    @Override
    public Item getItem(String itemName, String itemValue) {
        return new Item(itemName,itemValue);
    }

    public void setItem(Item item) {
        this.item = item;
    }
}