package com.jojos.bank.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The class defining the rest api
 *
 * @author karanikasg@gmail.com.
 */
@Path("/api")
public class ResourceApi {

	private static final Logger log = LoggerFactory.getLogger(ResourceApi.class);

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String get() {
		return "\n This is the REST API for money transfers between accounts via HTTPServer. " +
				"Use it with wisdom";
	}



}