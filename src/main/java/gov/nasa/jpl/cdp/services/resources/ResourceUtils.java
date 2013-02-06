package gov.nasa.jpl.cdp.services.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class ResourceUtils {
	
	/* get Properties from services.properties */
	public static Properties getProperties() {
		// get the services.properties file
		URL urlPropertiesServices = ResourceUtils.class
			.getResource("/services.properties");
		File filePropertiesServices= new File(urlPropertiesServices.getFile());

        // open the properties file
        Properties propertiesServices = new Properties();
        FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(filePropertiesServices);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        // load the properties from the file
        try {
			propertiesServices.load(fileInputStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return propertiesServices;
	}
	
	/* Return TDB directory from services.properties file */
	public static String getTdbDir() {
		// get jena TDB directory
		return getProperties().getProperty("jena.tdb.directory");
	}

	/* Return solr indexing URI */
	public static String getSolrIndexingServiceURI() {
		// get jena TDB directory
		return getProperties().getProperty("services.solr_indexing.uri");
	}
	
	/* Return Virtuoso URL */
	public static String getVirtuosoURL() {
		return getProperties().getProperty("virtuoso.url");
	}
	
	/* Return Virtuoso URL */
	public static String getVirtuosoUsername() {
		return getProperties().getProperty("virtuoso.username");
	}
	
	/* Return Virtuoso URL */
	public static String getVirtuosoPassword() {
		return getProperties().getProperty("virtuoso.password");
	}
}
