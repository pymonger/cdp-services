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

import gov.nasa.jpl.cdp.provenance.OpmoEs;

import java.util.logging.Logger;
import java.util.Date;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("/services/artifact")
public class ArtifactResource {

	
    /**
	 * Logs that an artifact was derived from another artifact.
	 * @param name the name of the derived artifact.
	 * @param artifact the name of the parent artifact.
	 * @param host the name of the machine that this log entry belongs to.
	 * @param pid the process id number that this log entry belongs to.
	 * @param info additional information.
	 * @return result.
	 */
	@GET
	@Path("/was_derived_from_artifact")
    @Produces("text/plain")
    public String artifactWasDerivedFromArtifact(
    		@QueryParam("derived_artifact") String derived_artifact,
    		@QueryParam("artifact") String artifact
    		) {
		
		// check args
		if(derived_artifact == null || artifact == null) {
			throw new WebApplicationException(Response.serverError()
					.entity("Missing argument derived_artifact or artifact.").
					type("text/plain").build());
        }
		
		// get virtuoso configuration
		String url = ResourceUtils.getVirtuosoURL();
		String username = ResourceUtils.getVirtuosoUsername();
		String password = ResourceUtils.getVirtuosoPassword();
		
		OpmoEs opmo = new OpmoEs(url, username, password, "websession");
		opmo.wasDerivedFrom(derived_artifact, artifact);
		opmo.close();
		
		Date timestamp = new Date();
        String log = "derived_artifact=" + derived_artifact + 
        	", artifact=" + artifact  +
            ", timestamp=" + timestamp;
        Logger.global.info("# artifactWasDerivedFromArtifact(): " + log);
        return log + "\n";
    }

    /**
	 * Logs that an artifact was generated by a process.
	 * @param name the name of the generated artifact.
	 * @param process the name of the generating process.
	 * @param host the name of the machine that this log entry belongs to.
	 * @param pid the process id number that this log entry belongs to.
	 * @param info additional information.
	 * @return result.
	 */
	@GET
	@Path("/was_generated_by_process")
    @Produces("text/plain")
    public String artifactWasGeneratedByProcess(
    		@QueryParam("artifact") String artifact,
    		@QueryParam("process") String process
    		) {
		
		// check args
		if(artifact == null || process == null) {
			throw new WebApplicationException(Response.serverError()
					.entity("Missing argument artifact or process.").
					type("text/plain").build());
        }
		
		// get virtuoso configuration
		String url = ResourceUtils.getVirtuosoURL();
		String username = ResourceUtils.getVirtuosoUsername();
		String password = ResourceUtils.getVirtuosoPassword();
		
		OpmoEs opmo = new OpmoEs(url, username, password, "websession");
		opmo.wasGeneratedBy(artifact, process);
		opmo.close();
		
		Date timestamp = new Date();
        String log = "artifact=" + artifact + ", process=" + process  +
            ", timestamp=" + timestamp;
        Logger.global.info("# artifactWasGeneratedByProcess(): " + log);
        return log + "\n";
    }
}