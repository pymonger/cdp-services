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

import gov.nasa.jpl.cdp.provenance.Opmo;
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
import javax.ws.rs.core.Response.Status;

@Path("/services/process")
public class ProcessResource {


    /**
	 * Logs that a process was controlled by an agent.
	 * @param name the name of the process.
	 * @param agent the name of the controlling agent.
	 * @param host the name of the machine that this log entry belongs to.
	 * @param pid the process id number that this log entry belongs to.
	 * @param info additional information.
	 * @return result.
	 */
	@GET
	@Path("/was_controlled_by_agent")
    @Produces("text/plain")
    public String processWasControlledByAgent(
    		@QueryParam("process") String process,
    		@QueryParam("agent") String agent,
    		@QueryParam("start_time") String start_time,
    		@QueryParam("end_time") String end_time
    		) {
		
		// check args
		if(process == null || agent == null) {
			throw new WebApplicationException(Response.serverError()
					.entity("Missing argument process or agent.").
					type("text/plain").build());
        }
		
		// get virtuoso configuration
		String url = ResourceUtils.getVirtuosoURL();
		String username = ResourceUtils.getVirtuosoUsername();
		String password = ResourceUtils.getVirtuosoPassword();
		
		OpmoEs opmo = new OpmoEs(url, username, password, "websession");
		if (start_time != null && end_time != null)
			opmo.wasControlledBy(process, agent, start_time, end_time);
		else if (start_time != null && end_time == null)
			opmo.wasControlledBy(process, agent, start_time);
		else
			opmo.wasControlledBy(process, agent);
		opmo.close();
		
		Date timestamp = new Date();
        String log = "process=" + process + ", agent=" + agent  + 
        	", timestamp=" + timestamp;
        Logger.global.info("# processWasControlledByAgent(): " + log);
        return log + "\n";
    }

    /**
	 * Logs that a process used an artifact.
	 * @param name the name of the process.
	 * @param artifact the name of the artifact used.
	 * @param host the name of the machine that this log entry belongs to.
	 * @param pid the process id number that this log entry belongs to.
	 * @param info additional information.
	 * @return result.
	 */
	@GET
	@Path("/used_artifact")
    @Produces("text/plain")
    public String processUsedArtifact(
    		@QueryParam("process") String process,
    		@QueryParam("artifact") String artifact
    		) {
		
		// check args
		if(process == null || artifact == null) {
			throw new WebApplicationException(Response.serverError()
					.entity("Missing argument process or artifact.").
					type("text/plain").build());
        }
		
		// get virtuoso configuration
		String url = ResourceUtils.getVirtuosoURL();
		String username = ResourceUtils.getVirtuosoUsername();
		String password = ResourceUtils.getVirtuosoPassword();
		
		OpmoEs opmo = new OpmoEs(url, username, password, "websession");
		opmo.used(process, artifact);
		opmo.close();
		
		Date timestamp = new Date();
        String log = "process=" + process + ", artifact=" + artifact  +
            ", timestamp=" + timestamp;
        Logger.global.info("# processUsedArtifact(): " + log);
        return log + "\n";
    }

    /**
	 * Logs that a process was triggered by another process.
	 * @param name the name of the process triggered.
	 * @param process the name of the process doing the triggering.
	 * @param host the name of the machine that this log entry belongs to.
	 * @param pid the process id number that this log entry belongs to.
	 * @param info additional information.
	 * @return result.
	 */
	@GET
	@Path("/was_triggered_by_process")
    @Produces("text/plain")
    public String processWasTriggeredByProcess(
    		@QueryParam("triggered_process") String triggered_process,
    		@QueryParam("process") String process
    		) {
		
		// check args
		if(process == null || triggered_process == null) {
			throw new WebApplicationException(Response.serverError()
					.entity("Missing argument process or triggered_process.").
					type("text/plain").build());
        }
		
		// get virtuoso configuration
		String url = ResourceUtils.getVirtuosoURL();
		String username = ResourceUtils.getVirtuosoUsername();
		String password = ResourceUtils.getVirtuosoPassword();
		
		OpmoEs opmo = new OpmoEs(url, username, password, "websession");
		opmo.wasTriggeredBy(triggered_process, process);
		opmo.close();
		
		Date timestamp = new Date();
        String log = "triggered_process=" + triggered_process + 
        	", process=" + process  +
            ", timestamp=" + timestamp;
        Logger.global.info("# processWasTriggeredByProcess(): " + log);
        return log + "\n";
    }

    /**
	 * Logs that a process started.
	 * @param name the name of the process.
	 * @param host the name of the machine that this log entry belongs to.
	 * @param pid the process id number that this log entry belongs to.
	 * @param info additional information.
	 * @return result.
	 */
	/*
	@GET
	@Path("/was_started_at")
    @Produces("text/plain")
    public String processWasStartedAt(
    		@QueryParam("name") String name,
            @QueryParam("host") String host,
            @QueryParam("timestamp") Date timestamp,
    		@QueryParam("pid") String pid,
            @QueryParam("info") String info
    		) {
		if (timestamp == null) timestamp = new Date();
        String log = "name=" + name + ", timestamp=" + timestamp +
            ", host=" + host + ", pid=" + pid + ", info:" + info;
        Logger.global.info("# processWasStartedAt(): " + log);
        return log + "\n";
    }
    */

    /**
	 * Logs that a process ended.
	 * @param name the name of the process.
	 * @param host the name of the machine that this log entry belongs to.
	 * @param pid the process id number that this log entry belongs to.
	 * @param info additional information.
	 * @return result.
	 */
	/*
	@GET
	@Path("/was_ended_at")
    @Produces("text/plain")
    public String processWasEndedAt(
    		@QueryParam("name") String name,
            @QueryParam("host") String host,
            @QueryParam("timestamp") Date timestamp,
    		@QueryParam("pid") String pid,
            @QueryParam("info") String info
    		) {
		if (timestamp == null) timestamp = new Date();
        String log = "name=" + name + ", timestamp=" + timestamp +
            ", host=" + host + ", pid=" + pid + ", info:" + info;
        Logger.global.info("# processWasEndedAt(): " + log);
        return log + "\n";
    }
    */

}
