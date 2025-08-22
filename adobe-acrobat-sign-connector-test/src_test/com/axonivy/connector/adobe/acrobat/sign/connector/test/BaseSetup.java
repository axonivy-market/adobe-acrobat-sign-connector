package com.axonivy.connector.adobe.acrobat.sign.connector.test;

import static com.axonivy.utils.e2etest.enums.E2EEnvironment.REAL_SERVER;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.connector.adobe.acrobat.sign.connector.test.constants.AdobeTestConstants;
import com.axonivy.utils.e2etest.utils.E2ETestUtils;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClientFeature;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.RestClient.Builder;

public abstract class BaseSetup {
  public abstract String getClientName();

  protected boolean isRealTest;

  @BeforeEach
  public void beforeEach(ExtensionContext context, AppFixture fixture, IApplication app) {
    isRealTest = context.getDisplayName().equals(REAL_SERVER.getDisplayName());
    E2ETestUtils.determineConfigForContext(context.getDisplayName(), runRealEnv(fixture), runMockEnv(fixture, app));
  }

  protected Runnable runRealEnv(AppFixture fixture) {
    return () -> {
      String host = System.getProperty(AdobeTestConstants.HOST);
      String intergationKey = System.getProperty(AdobeTestConstants.INTERGRATION_KEY);
      String returnPage = System.getProperty(AdobeTestConstants.RETURN_PAGE);

      fixture.var("adobeAcrobatSignConnector.host", host);
      fixture.var("adobeAcrobatSignConnector.integrationKey", intergationKey);
      fixture.var("adobeAcrobatSignConnector.returnPage", returnPage);
    };
  }

  protected Runnable runMockEnv(AppFixture fixture, IApplication app) {
    return () -> {
      fixture.var("adobeAcrobatSignConnector.host", "TESTHOST");
      fixture.var("adobeAcrobatSignConnector.integrationKey", "TESTUSER");
      RestClient restClient = RestClients.of(app).find(getClientName());
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
    };
  }
}
