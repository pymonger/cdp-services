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
import java.util.logging.Level;

import java.io.File;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import javax.ws.rs.core.UriBuilder;

/**
 * The main driver for running the services.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        //Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); JDK 1.6 convention
        Logger logger = Logger.global; // JDK 1.5 convention
        logger.setLevel(Level.ALL);


        /* ---------------------------------------------------------------------
         * load service settings from properties file in resources
         * ---------------------------------------------------------------------
         */

        Properties propertiesServices = new Properties();
        InputStream in = new Main().getClass().getResourceAsStream("/services.properties");
        propertiesServices.load(in);

        /* ---------------------------------------------------------------------
         * start up web container with given URI
         * ---------------------------------------------------------------------
         */

        String servicesBaseURIHostname = propertiesServices.getProperty("services.uri.hostname");
        String servicesBaseURIPort = propertiesServices.getProperty("services.uri.port");
        String servicesBaseURIString = "http://" + servicesBaseURIHostname + ":" + servicesBaseURIPort + "/"; // needs to end with "/"
        URI servicesBaseURI = UriBuilder.fromUri(servicesBaseURIString).build();

    	Map<String,String> initParams = new HashMap();
    	// load all java files under resources namespace
        initParams.put("com.sun.jersey.config.property.packages", "gov.nasa.jpl.cdp.services.resources");

        SelectorThread threadSelector = GrizzlyWebContainerFactory.create(servicesBaseURI, initParams);
        logger.info(String.format("Jersey app started with WADL available at %sapplication.wadl\nHit cntrl-c to stop it...", servicesBaseURI));

        // add shutdown hook
        Shutdown sh = new Shutdown(threadSelector);
        Runtime.getRuntime().addShutdownHook(sh);
    }    
}

class Shutdown extends Thread {
    SelectorThread ts;
    Shutdown(SelectorThread ts) {
        this.ts = ts;
    }

    public void run() {
        System.out.print("Initiating shutdown...");
        this.ts.stopEndpoint();
        System.out.println("done. Goodbye.");
    }
}
