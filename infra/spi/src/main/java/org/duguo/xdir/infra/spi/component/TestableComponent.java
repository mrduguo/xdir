package org.duguo.xdir.infra.spi.component;


/**
 *  Service component could be tested and reported by infra service
 */
public interface TestableComponent {

    /**
     * Get the component name
     *
     * @return component name
     */
    String getName();

    /**
     * Perform test on the component
     *
     * @return true - only the component is in health status
     * @throws Exception any exception which will fail the component
     */
    boolean performTest() throws Exception;
}