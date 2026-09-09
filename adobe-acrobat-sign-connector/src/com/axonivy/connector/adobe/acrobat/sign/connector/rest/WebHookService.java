package com.axonivy.connector.adobe.acrobat.sign.connector.rest;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import ch.ivyteam.ivy.environment.Ivy;

/**
 * REST service for Adobe Sign WebHook communication. This service provides methods for verification and to receive notifications.
 * <br/>
 * The service needs to be configured as WebHook in the Adobe Sign profile settings.
 * @see <a href="https://opensource.adobe.com/acrobat-sign/acrobat_sign_events/index.html#creating-a-webhook">WebHooks in Adobe Sign</a>
 *
 * @author jpl
 *
 */
@Path("/adobe/webhook")
@PermitAll
public class WebHookService {

	private static final String CLIENT_ID_HEADER = "X-ADOBESIGN-CLIENTID";

	@GET
	@Produces("application/json")
	public Response verification(@Context HttpHeaders headers) {
		Response response;
		String clientId = headers.getHeaderString(CLIENT_ID_HEADER);
		if(StringUtils.isNotBlank(clientId)) {
			response = Response.accepted().entity(new ClientIdResponse(clientId)).build();
		} else {
			response = Response.status(Status.NOT_ACCEPTABLE).build();
		}

		return response;
	}

	@POST
	@Consumes("application/json")
	public void notification(JsonNode node) {
		Ivy.log().info(node);
	}

	private record ClientIdResponse(String xAdobeSignClientId) {}

}
