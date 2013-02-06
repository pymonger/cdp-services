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

import java.util.logging.Logger;
import java.util.Date;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

//@Path("/services/agent")
public class AgentResource {


    /**
	 * Logs that an agent was operated by another agent.
	 * @param name the name of the agent that was operated on.
	 * @param agent the name of the agent doing the operating.
	 * @param host the name of the machine that this log entry belongs to.
	 * @param pid the process id number that this log entry belongs to.
	 * @param info additional information.
	 * @return result.
	 */
//	@GET
//	@Path("/was_operated_by_agent")
//    @Produces("text/plain")
    public String agentWasOperatedByAgent(
    		@QueryParam("name") String name,
    		@QueryParam("agent") String agent,
            @QueryParam("timestamp") Date timestamp,
            @QueryParam("host") String host,
    		@QueryParam("pid") String pid,
            @QueryParam("info") String info
    		) {
		if (timestamp == null) timestamp = new Date();
        String log = "name=" + name + ", agent=" + agent  + ", timestamp=" +
            timestamp + ", host=" + host + ", pid=" + pid + ", info:" + info;
        Logger.global.info("# agentWasOperatedByAgent(): " + log);
        return log + "\n";
    }
}

