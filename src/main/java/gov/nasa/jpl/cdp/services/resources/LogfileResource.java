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

package gov.nasa.jpl.cdp.services.resources;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.json.JSONStringer;
import org.json.JSONException;

import com.google.common.io.Files;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import gov.nasa.jpl.cdp.jena.TDB;
import gov.nasa.jpl.cdp.log.log_parser;

@Path("/services/logfile")
public class LogfileResource {


    /**
     * Upload logfile.
     * @return json: {"status": "<success|fail>",
     *                "message": "<log message>"}.
     *                
     * Example client usage:
     * curl -v -X POST --data-binary @post_wcs http://localhost:5000/services/logfile/upload
     * curl -v -X POST --data "This is a test" http://localhost:5000/services/logfile/upload
     */
    @POST
    @Path("/upload")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String upload(
            InputStream text
            ) {
        String log;
        String status;
        String graphName = null;
        JSONStringer json = new JSONStringer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(text));
            log_parser lp = new log_parser(ResourceUtils.getVirtuosoURL(),
                    ResourceUtils.getVirtuosoUsername(), 
                    ResourceUtils.getVirtuosoPassword());
            graphName = lp.parse_log(br);

            if (graphName.equals(null)) {
                log = "Error reading in input: no line parsed.  Check input file and its path.";
                status = "fail";
            }else {
                log = "log file parsing is successful.";
                status = "success";
                
                /*
                // make REST call to index session that was just parsed
                MultivaluedMapImpl queryParams = new MultivaluedMapImpl();
                queryParams.add("graphName", graphName);
                Client client = Client.create();
                WebResource resource = client.resource(ResourceUtils.getSolrIndexingServiceURI());
                String response = resource.queryParams(queryParams).get(String.class);
                Logger.global.info("solr indexing REST call response: " + response);
                */
            }
        } catch (Exception e) {
            e.printStackTrace();
            log = "Error:" + e;
            status = "fail";
        }
        Logger.global.info("# upload() " + status + ": " + log + " Named Graph URI: " + graphName);
        try {
            json.object()
                .key("status").value(status)
                .key("message").value(log)
                .key("graphName").value(graphName)
            .endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
    
    
    /**
     * Upload logfile and dump provenance in a specific format.
     * @return string.
     *                
     * Example client usage:
     * curl -v -X POST --data-binary @cdp_prov.log \
     *     http://localhost:5000/services/logfile/dump?type=TURTLE > test.json
     * curl -v -X POST --data "This is a test" \
     *     http://localhost:5000/services/logfile/dump?type=RDF/XML > test.json
     */
    @POST
    @Path("/dump")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
        public String dump(
            @QueryParam("type") String type,
            InputStream text
            ) {
        String graphName = null;
        String output = null;
        Boolean success = false;
        JSONStringer json = new JSONStringer();
        log_parser lp = null;
        
        // import logfile into in-memory model
        com.hp.hpl.jena.rdf.model.Model ds = ModelFactory.createDefaultModel();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(text));
            lp = new log_parser(ds);
            graphName = lp.parse_log(br);

            if (graphName.equals(null)) {
                output = "Error reading in input: no line parsed.  Check input file and its path.";
            }else {
                output = "log file parsing was successful.";
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            output = "Error:" + e;
        }
        
        Logger.global.info("# dump() " + type + " " + ": " + " Named Graph URI: " + graphName);
        
        // get triples
        try {
            ByteArrayOutputStream modStr = new ByteArrayOutputStream();
            ds.write(modStr, type);
            output = modStr.toString();
            lp.close();
        } catch (Exception e) {
            e.printStackTrace();
            output = "Error:" + e;
        }
        
        // create return json
        try {
        	if (success) {
	            json.object()
	                .key("success").value(success)
	                .key("message").value(null)
	                .key("graphName").value(graphName)
	                .key("type").value(type)
	                .key("value").value(output)
	            .endObject();
        	}else {
        		json.object()
	                .key("success").value(success)
	                .key("message").value(output)
	                .key("graphName").value(graphName)
	                .key("type").value(type)
	                .key("value").value(null)
	            .endObject();
        	}
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}

