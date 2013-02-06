/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *                        NASA Jet Propulsion Laboratory
 *                      California Institute of Technology
 *                        (C) 2010  All Rights Reserved
 *
 * <LicenseText>
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package gov.nasa.jpl.cdp.services;

import java.util.logging.Logger;

import org.junit.Test;
import static org.junit.Assert.*;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.MediaTypes;

/**
 * junit test for services.
 */
public class MainTest extends JerseyTest {

    public MainTest() throws Exception {
        super("gov.nasa.jpl.cdp.services.resources");
    }


    /**
     * Test if a WADL document is available at the relative path
     * "application.wadl".
     */
    @Test
    public void testApplicationWadl() {
    	WebResource webResource = resource();
        String serviceWadl = webResource.path("application.wadl").accept(MediaTypes.WADL).get(String.class);
        assertTrue(serviceWadl.length() > 0);
    }


//    /**
//     * Test the service.
//     */
//    @Test
//    public void testServicesJobCancel() {
//    	WebResource webResource = resource();
//        String responseMsg = webResource.path("services/job/cancel").get(String.class);
//        assertEquals("ok\n", responseMsg);
//    }

}
