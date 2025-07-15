package com.axonivy.connector.adobe.acrobat.sign.connector.test.utils;

import com.axonivy.connector.adobe.acrobat.sign.connector.test.constants.AdobeTestConstants;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClientFeature;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.RestClient.Builder;

public class AdobeTestUtils {
  protected static final String TRANSIENT_DOCUMENTS = "TransientDocuments";
  protected static final String AGREEMENTS = "Agreements";

  public static void setUpConfigForContext(String contextName, AppFixture fixture, IApplication app,
      String clientName) {
    switch (contextName) {
      case AdobeTestConstants.REAL_CALL_CONTEXT_DISPLAY_NAME:
        setUpConfigForApiTest(fixture);
        break;
      case AdobeTestConstants.MOCK_SERVER_CONTEXT_DISPLAY_NAME:
        setUpConfigForMockServer(fixture, app, clientName);
        break;
      default:
        break;
    }
  }

  public static void setUpConfigForApiTest(AppFixture fixture) {
    String host = System.getProperty(AdobeTestConstants.HOST);
    String intergationKey = System.getProperty(AdobeTestConstants.INTERGRATION_KEY);
    String returnPage = System.getProperty(AdobeTestConstants.RETURN_PAGE);

    fixture.var("adobeAcrobatSignConnector.host", host);
    fixture.var("adobeAcrobatSignConnector.integrationKey", intergationKey);
    fixture.var("adobeAcrobatSignConnector.returnPage", returnPage);
  }

  public static void setUpConfigForMockServer(AppFixture fixture, IApplication app, String clientName) {
    fixture.var("adobeAcrobatSignConnector.host", "TESTHOST");
    fixture.var("adobeAcrobatSignConnector.integrationKey", "TESTUSER");
    RestClient restClient = RestClients.of(app).find(clientName);
    // change created client: use test url and a slightly different version of the DocuWare Auth feature
    Builder builder = RestClient.create(restClient.name()).uuid(restClient.uniqueId())
        .uri("http://{ivy.engine.host}:{ivy.engine.http.port}/{ivy.request.application}/api/adobeSignMock")
        .description(restClient.description()).properties(restClient.properties());

    for (RestClientFeature feature : restClient.features()) {
      builder.feature(feature.clazz());
    }

    builder.feature("ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature");
    restClient = builder.toRestClient();
    RestClients.of(app).set(restClient);
  }
}
