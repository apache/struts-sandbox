package org.apache.struts2.cdi;

import javax.inject.Inject;

/**
 * FooConsumer.
 *
 * @author Rene Gielen
 */
public class FooConsumer {

    @Inject
    private FooService fooService;

    public void foo() {
        System.out.println(fooService.getHello());
    }
}
