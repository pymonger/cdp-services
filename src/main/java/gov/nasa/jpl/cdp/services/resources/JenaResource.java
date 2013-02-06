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

import gov.nasa.jpl.cdp.jena.ARQ;
import gov.nasa.jpl.cdp.jena.Classification;
import gov.nasa.jpl.cdp.jena.Rules;
import gov.nasa.jpl.cdp.jena.TDB;
import gov.nasa.jpl.cdp.jena.Virtuoso;
import gov.nasa.jpl.cdp.provenance.OpmoEs;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

@Path("/services/cdp_jena")
public class JenaResource {

    /**
	 * Dump Virtuoso-backed Jena model.
	 * @return json: {"status": "<success|fail>",
	 *                "message": "<log message>"}.
	 *                
	 * Example client usage:
	 * curl -v -X POST http://localhost:5000/services/cdp_jena/dump?type=TURTLE
	 */
	@POST
	@Path("/dump")
	@Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String dump(
    		@QueryParam("type") String type
    		) {
		String log = "";
		String status = "success";
		JSONStringer json = new JSONStringer();
		
		// get virtuoso configuration
		String url = ResourceUtils.getVirtuosoURL();
		String username = ResourceUtils.getVirtuosoUsername();
		String password = ResourceUtils.getVirtuosoPassword();

		try {
	        // get model
	    	Model model = Virtuoso.getModel(url, username, password);
	    	ByteArrayOutputStream modStr = new ByteArrayOutputStream();
	    	model.write(modStr, type);
	    	
	    	// close
	    	model.close();
	    	
			log = modStr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			log = "Error:" + e;
			status = "fail";
		}
		Logger.global.info("# rules() " + status + ": " + log);
		try {
			json.object()
				.key("status").value(status)
				.key("message").value(log)
			.endObject();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return json.toString();
    }
	
    /**
	 * Save RDF to Virtuoso-backed Jena model.
	 * @return json: {"status": "<success|fail>",
	 *                "message": "<log message>"}.
	 *                
	 * Example client usage:
	 * curl -v -X POST --data-binary @manipon-family.ttl http://localhost:5000/services/cdp_jena/save?type=TURTLE
	 */
	@POST
	@Path("/save")
	@Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String save(
    		@QueryParam("type") String type,
    		InputStream text
    		) {
		String log = "";
		String status = "success";
		JSONStringer json = new JSONStringer();
		
		// get virtuoso configuration
		String url = ResourceUtils.getVirtuosoURL();
		String username = ResourceUtils.getVirtuosoUsername();
		String password = ResourceUtils.getVirtuosoPassword();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(text));
			Model model = Virtuoso.indexRDF(br, type, url, username, password, false);
			model.close();
		} catch (Exception e) {
			e.printStackTrace();
			log = "Error:" + e;
			status = "fail";
		}
		Logger.global.info("# save() " + status + ": " + log);
		try {
			json.object()
				.key("status").value(status)
				.key("message").value(log)
			.endObject();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return json.toString();
    }
	
	/**
	 * Apply rules to Virtuoso-backed Jena model and get inference model.
	 * @return json: {"status": "<success|fail>",
	 *                "message": "<log message>"}.
	 *                
	 * Example client usage:
	 * curl -v -X POST --data-binary @family.rules http://localhost:5000/services/cdp_jena/rules?type=TURTLE
	 */
	@POST
	@Path("/rules")
	@Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String rules(
    		@QueryParam("type") String type,
    		InputStream text
    		) {
		String log = "";
		String status = "success";
		JSONStringer json = new JSONStringer();
		
		// get virtuoso configuration
		String url = ResourceUtils.getVirtuosoURL();
		String username = ResourceUtils.getVirtuosoUsername();
		String password = ResourceUtils.getVirtuosoPassword();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(text));
			
	        // get model
	    	Model model = Virtuoso.getModel(url, username, password);
	    	
	    	// create reasoner
	        Reasoner reasoner = new GenericRuleReasoner(
	        		Rule.parseRules(Rule.rulesParserFromReader(br))
	        );
	    	
	    	// apply rules (get inference model)
	    	InfModel inf = Rules.getInferenceModel(model, reasoner);
	    	
	    	// write inferred model
	    	ByteArrayOutputStream infStr = new ByteArrayOutputStream();
	    	inf.write(infStr, type);
	    	
	    	// close
	    	inf.close();
	    	model.close();
	    	
			log = infStr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			log = "Error:" + e;
			status = "fail";
		}
		Logger.global.info("# rules() " + status + ": " + log);
		try {
			json.object()
				.key("status").value(status)
				.key("message").value(log)
			.endObject();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return json.toString();
    }
	
	/**
	 * SPARQL query the Virtuoso-backed Jena model and return result set JSON.
	 * @return json: {"status": "<success|fail>",
	 *                "message": "<log message>",
	 *                "arqResults": <Result Set object> }.
	 *                
	 * Example client usage:
	 * curl -v -X POST --data-binary @child-of.sparql http://localhost:5000/services/cdp_jena/query
	 */
	@POST
	@Path("/query")
	@Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String query(
    		InputStream text
    		) {
		JSONObject resJson = null;
		String log = "";
		String status = "success";
		JSONStringer json = new JSONStringer();
		
		// get virtuoso configuration
		String url = ResourceUtils.getVirtuosoURL();
		String username = ResourceUtils.getVirtuosoUsername();
		String password = ResourceUtils.getVirtuosoPassword();

		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(text));
			int lineCount = 1;
			String line = br.readLine();
			while (line != null) {
				sb.append(line + "\n");
				lineCount++;
				line = br.readLine();
			}
			br.close();
	        text.close();
			
	        // get model
	    	Model model = Virtuoso.getModel(url, username, password);
	    	
	    	// run sparql query
	    	String sparql = sb.toString();
	    	QueryExecution qexec = ARQ.getQueryExecution(model, sparql);
	    	ByteArrayOutputStream resStr = new ByteArrayOutputStream();
	    	try {
	        	ResultSet rs = qexec.execSelect();
	        	ResultSetFormatter.outputAsJSON(resStr, rs);
	        	resJson = new JSONObject(resStr.toString());
	        }
	        finally
	        {
	            // QueryExecution objects should be closed to free any 
	        	// system resources 
	            qexec.close();
	            model.close();
	        }
		} catch (Exception e) {
			e.printStackTrace();
			log = "Error:" + e;
			status = "fail";
		}
		//Logger.global.info("# query() " + status + ": " + log);
		try {
			json.object()
				.key("status").value(status)
				.key("message").value(log)
				.key("arqResults").value(resJson)
			.endObject();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return json.toString();
    }
	
	/**
	 * SPARQL query the Virtuoso-backed Jena model for triples and return formatted results.
	 * @return json: {"status": "<success|fail>",
	 *                "message": "<log message>",
	 *                "triples": "<triples>" }.
	 *                
	 * Example client usage:
	 * curl -v -X POST --data-binary @query_triples.sparql http://localhost:5000/services/cdp_jena/query_triples
	 */
	@POST
	@Path("/query_triples")
	@Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String query_triples(
    		InputStream text
    		) {
		JSONObject resJson = null;
		StringBuilder triples = new StringBuilder();
		String log = "";
		String status = "success";
		JSONStringer json = new JSONStringer();
		
		// get virtuoso configuration
		String url = ResourceUtils.getVirtuosoURL();
		String username = ResourceUtils.getVirtuosoUsername();
		String password = ResourceUtils.getVirtuosoPassword();

		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(text));
			int lineCount = 1;
			String line = br.readLine();
			while (line != null) {
				sb.append(line + "\n");
				lineCount++;
				line = br.readLine();
			}
			br.close();
	        text.close();
			
	        // get model
	    	Model model = Virtuoso.getModel(url, username, password);
	    	
	    	// run sparql query
	    	String sparql = sb.toString();
	    	QueryExecution qexec = ARQ.getQueryExecution(model, sparql);
	    	ByteArrayOutputStream resStr = new ByteArrayOutputStream();
	    	try {
	    		// get result set as json object
	        	ResultSet rs = qexec.execSelect();
	        	ResultSetFormatter.outputAsJSON(resStr, rs);
	        	resJson = new JSONObject(resStr.toString());
	        	
	        	// make sure this result set is for triples
	        	JSONArray vars = resJson.getJSONObject("head").getJSONArray("vars");
	        	if (vars.length() != 3)
	        		throw new RuntimeException("SPARQL query needs to return triples. Detected only "
	        				+ vars.length() + " variables returned.");
	        	
	        	// build triples
	        	JSONArray results = resJson.getJSONObject("results").getJSONArray("bindings");
	        	for (int i = 0; i < results.length(); i++) {
	        		JSONObject binding = results.getJSONObject(i);
	        		for (int j = 0; j < vars.length(); j++) {
	        			String type = binding.getJSONObject(vars.getString(j)).getString("type");
	        			String value = binding.getJSONObject(vars.getString(j)).getString("value");
	        			if (type.compareTo("uri") == 0) triples.append("<" + value + "> ");
	        			else if (type.compareTo("typed-literal") == 0)
	        				triples.append("\"" + value + "\"" + "^^<" + 
	        				  binding.getJSONObject(vars.getString(j)).getString("datatype") + "> ");
	        			else triples.append(value + " ");
	        		}
	        		triples.append(".\n");
	        	}
	        }
	        finally
	        {
	            // QueryExecution objects should be closed to free any 
	        	// system resources 
	            qexec.close();
	            model.close();
	        }
		} catch (Exception e) {
			e.printStackTrace();
			log = "Error:" + e;
			status = "fail";
		}
		Logger.global.info("# query_triples() " + status + ": " + log);
		try {
			json.object()
				.key("status").value(status)
				.key("message").value(log)
				.key("triples").value(triples.toString())
			.endObject();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return json.toString();
    }
	
	/**
	 * Import triples in to a model, run rules to generate inferred model, and return TURTLE.
	 * @return json: {"status": "<success|fail>",
	 *                "message": "<log message>",
	 *                "triples": <TURTLE> }.
	 *                
	 * Example client usage:
	 * curl -v -X POST --data-binary @getInferred.json http://localhost:5000/services/cdp_jena/infer_this
	 */
	@POST
	@Path("/infer_this")
	@Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String infer_this(
    		InputStream text
    		) {
		String log = "";
		String status = "success";
		JSONStringer json = new JSONStringer();
		JSONObject jsonInput = null;
		String triples = "";
		
		// read in json
		try {
			// build json request text
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(text));
			int lineCount = 1;
			String line = br.readLine();
			while (line != null) {
				sb.append(line + "\n");
				lineCount++;
				line = br.readLine();
			}
			br.close();
	        text.close();
	        
			// get JSONObject
			jsonInput = new JSONObject(sb.toString());
			
			// create in-memory model
			Model model = ModelFactory.createDefaultModel();
			
			// model.read() takes in buffered reader; write triples to model
			model.read(new BufferedReader(new StringReader(jsonInput.getString("triples"))), 
					null, "TURTLE");
			
			// create reasoner
	        Reasoner reasoner = new GenericRuleReasoner(
	        		Rule.parseRules(Rule.rulesParserFromReader(
	        				new BufferedReader(new StringReader(jsonInput.getString("rules")))
	        		)
	        	)
	        );
	    	
	    	// apply rules (get inference model)
	    	InfModel inf = Rules.getInferenceModel(model, reasoner);
	    	
	    	// write inferred model
	    	ByteArrayOutputStream infStr = new ByteArrayOutputStream();
	    	inf.write(infStr, "TURTLE");
	    	
	    	// write triples from inferred model
			triples = infStr.toString();
			
			// close
	    	inf.close();
	    	model.close();
	    	
		} catch (Exception e) {
			e.printStackTrace();
			log = "Error:" + e;
			status = "fail";
		}
		Logger.global.info("# infer_this() " + status + ": " + log);
		try {
			json.object()
				.key("status").value(status)
				.key("message").value(log)
				.key("triples").value(triples)
			.endObject();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return json.toString();
    }
	
	/**
	 * Import triples in to a model, run sparql on model, and return TURTLE.
	 * @return json: {"status": "<success|fail>",
	 *                "message": "<log message>",
	 *                "triples": <TURTLE> }.
	 *                
	 * Example client usage:
	 * curl -v -X POST --data-binary @query.json http://localhost:5000/services/cdp_jena/query_this
	 */
	@POST
	@Path("/query_this")
	@Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String query_this(
    		InputStream text
    		) {
		String log = "";
		String status = "success";
		JSONStringer json = new JSONStringer();
		JSONObject jsonInput = null;
		JSONObject resJson = null;
		String triples = "";
		
		// read in json
		try {
			// build json request text
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(text));
			int lineCount = 1;
			String line = br.readLine();
			while (line != null) {
				sb.append(line + "\n");
				lineCount++;
				line = br.readLine();
			}
			br.close();
	        text.close();
	        
			// get JSONObject
			jsonInput = new JSONObject(sb.toString());
			
			// create in-memory model
			Model model = ModelFactory.createDefaultModel();
			
			// model.read() takes in buffered reader; write triples to model
			model.read(new BufferedReader(new StringReader(jsonInput.getString("triples"))), 
					null, "TURTLE");
	    	
	        // run sparql query
	    	String sparql = jsonInput.getString("sparql");
	    	QueryExecution qexec = ARQ.getQueryExecution(model, sparql);
	    	ByteArrayOutputStream resStr = new ByteArrayOutputStream();
	    	try {
	        	ResultSet rs = qexec.execSelect();
	        	ResultSetFormatter.outputAsJSON(resStr, rs);
	        	resJson = new JSONObject(resStr.toString());
	        }
	        finally
	        {
	            // QueryExecution objects should be closed to free any 
	        	// system resources 
	            qexec.close();
	            model.close();
	        }
		} catch (Exception e) {
			e.printStackTrace();
			log = "Error:" + e;
			status = "fail";
		}
		//Logger.global.info("# query_this() " + status + ": " + log);
		try {
			json.object()
				.key("status").value(status)
				.key("message").value(log)
				.key("arqResults").value(resJson)
			.endObject();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return json.toString();
    }
	
	/**
	 * Import triples in to a model with RDFS schema and return TURTLE.
	 * @return json: {"status": "<success|fail>",
	 *                "message": "<log message>",
	 *                "triples": <TURTLE> }.
	 *                
	 * Example client usage:
	 * curl -v -X POST --data-binary @classify_this.json http://localhost:5000/services/cdp_jena/classify_this
	 */
	@POST
	@Path("/classify_this")
	@Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String classify_this(
    		InputStream text
    		) {
		String log = "";
		String status = "success";
		JSONStringer json = new JSONStringer();
		JSONObject jsonInput = null;
		String triples = "";
		
		// read in json
		try {
			// build json request text
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(text));
			int lineCount = 1;
			String line = br.readLine();
			while (line != null) {
				sb.append(line + "\n");
				lineCount++;
				line = br.readLine();
			}
			br.close();
	        text.close();
	        
			// get JSONObject
			jsonInput = new JSONObject(sb.toString());
			
			// get classified
			BufferedReader s = new BufferedReader(
					new StringReader(jsonInput.getString("schema")));
			BufferedReader d = new BufferedReader(
					new StringReader(jsonInput.getString("triples")));
	    	InfModel infmodel = Classification.getClassifiedModel(d, "TURTLE", s, "RDF/XML");
	    	
	    	// write inferred model
	    	ByteArrayOutputStream infStr = new ByteArrayOutputStream();
	    	infmodel.write(infStr, "TURTLE");
	    	
	    	// write triples from inferred model
			triples = infStr.toString();
			
			// close
			infmodel.close();
	    	
		} catch (Exception e) {
			e.printStackTrace();
			log = "Error:" + e;
			status = "fail";
		}
		Logger.global.info("# classify_this() " + status + ": " + log);
		try {
			json.object()
				.key("status").value(status)
				.key("message").value(log)
				.key("triples").value(triples)
			.endObject();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return json.toString();
    }
}

