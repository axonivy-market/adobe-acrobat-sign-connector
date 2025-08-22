package com.axonivy.connector.adobe.acrobat.sign.connector.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import com.axonivy.connector.adobe.acrobat.sign.connector.TransientDocumentsData;
import com.axonivy.utils.e2etest.context.MultiEnvironmentContextProvider;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.security.ISession;

@IvyProcessTest(enableWebServer = true)
@ExtendWith(MultiEnvironmentContextProvider.class)
public class TestTransientDocumentsService extends BaseSetup {
  protected static final String TRANSIENT_DOCUMENTS = "TransientDocuments";
  private static final BpmElement testeeUploadDocument =
      BpmProcess.path("connector/TransientDocuments").elementName("uploadDocument(file)");

  @TestTemplate
  public void uploadFile(BpmClient bpmClient, ISession session, AppFixture fixture, IApplication app)
      throws IOException {
    java.io.File pdf = TestService.getSamplePdf();
    assertThat(pdf).isNotNull();

    ExecutionResult result = bpmClient.start().subProcess(testeeUploadDocument).withParam("file", pdf).execute();
    TransientDocumentsData data = result.data().last();
    assertThat(data.getId()).isNotEmpty();
  }

  @Override
  public String getClientName() {
    return TRANSIENT_DOCUMENTS;
  }
}
