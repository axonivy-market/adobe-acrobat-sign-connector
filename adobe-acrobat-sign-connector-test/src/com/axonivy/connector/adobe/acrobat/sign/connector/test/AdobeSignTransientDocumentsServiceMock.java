package com.axonivy.connector.adobe.acrobat.sign.connector.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import ch.ivyteam.api.API;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@PermitAll
@Path("adobeSignMock")
public class AdobeSignTransientDocumentsServiceMock {

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Path("transientDocuments")
  public Response transientDocuemts(String payload) {
    API.checkParameterNotNull(payload, "payload");
	return Response.status(201).entity(load("json/uploadDocument.json")).build();
  }

  private static String load(String path) {
    try (InputStream is = AdobeSignTransientDocumentsServiceMock.class.getResourceAsStream(path)) {
      return IOUtils.toString(is, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      throw new RuntimeException("Failed to read resource: " + path);
    }
  }
}
