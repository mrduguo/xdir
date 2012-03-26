package org.duguo.xdir.spi.security;


import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Secure {
    int value() default Role.USER;
}
