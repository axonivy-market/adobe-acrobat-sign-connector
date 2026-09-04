package com.axonivy.connector.adobe.acrobat.sign.connector.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import ch.ivyteam.api.API;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@PermitAll
@Path("adobeSignMock")
public class AdobeSignAgreementsServiceMock {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("agreements")
	public Response createAgreement(String payload) {
		API.checkParameterNotNull(payload, "payload");
		return Response.status(201).entity(load("json/createAgreement.json")).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("agreements/{agreementId}/documents")
	public Response getDocuments(@PathParam(value = "agreementId") String agreementId) {
		API.checkParameterNotNull(agreementId, "agreementId");
		return Response.status(201).entity(load("json/getDocuments.json")).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("agreements/{agreementId}/documents/{documentId}")
	public Response downloadDocument(@PathParam(value = "agreementId") String agreementId,
			@PathParam(value = "documentId") String documentId) throws IOException {
		API.checkParameterNotNull(agreementId, "agreementId");
		return Response.status(200).entity(TestService.getSamplePdf()).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("agreements/{agreementId}/signingUrls")
	public Response getSigningUrls(@PathParam(value = "agreementId") String agreementId) {
		API.checkParameterNotNull(agreementId, "agreementId");
		return Response.status(201).entity(load("json/getSigningUrls.json")).build();
	}

	private static String load(String path) {
		try (InputStream is = AdobeSignAgreementsServiceMock.class.getResourceAsStream(path)) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to read resource: " + path);
		}
	}
}
