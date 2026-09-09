package com.axonivy.connector.adobe.acrobat.sign.connector.test;

import static com.axonivy.utils.e2etest.enums.E2EEnvironment.REAL_SERVER;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.connector.adobe.acrobat.sign.connector.test.constants.AdobeTestConstants;
import com.axonivy.utils.e2etest.utils.E2ETestUtils;

import ch.ivyteam.ivy.environment.AppFixture;

public abstract class BaseSetup {
  public abstract String getClientName();

  protected abstract List<String> getClientFeatures();

  protected boolean isRealTest;

  @BeforeEach
  public void beforeEach(ExtensionContext context, AppFixture fixture) {
    isRealTest = context.getDisplayName().equals(REAL_SERVER.getDisplayName());
    E2ETestUtils.determineConfigForContext(context.getDisplayName(), runRealEnv(fixture), runMockEnv(fixture));
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

  protected Runnable runMockEnv(AppFixture fixture) {
    return () -> {
      fixture.var("adobeAcrobatSignConnector.host", "TESTHOST");
      fixture.var("adobeAcrobatSignConnector.integrationKey", "TESTUSER");
      fixture.config("RestClients." + getClientName() + ".Url",
          "http://{ivy.engine.host}:{ivy.engine.http.port}/{ivy.request.application}/api/adobeSignMock");
      fixture.config("RestClients." + getClientName() + ".Features", getClientFeatures());
    };
  }
}
