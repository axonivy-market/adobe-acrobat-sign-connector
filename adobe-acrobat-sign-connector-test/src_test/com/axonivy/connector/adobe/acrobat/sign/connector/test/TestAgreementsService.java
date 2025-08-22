package com.axonivy.connector.adobe.acrobat.sign.connector.test;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.connector.adobe.acrobat.sign.connector.rest.DownloadResult;
import com.axonivy.connector.adobe.acrobat.sign.connector.service.AdobeSignService;
import com.axonivy.utils.e2etest.context.MultiEnvironmentContextProvider;
import com.axonivy.connector.adobe.acrobat.sign.connector.AgreementsData;

import api.rest.v6.client.AgreementCreationInfo;
import api.rest.v6.client.AgreementCreationInfo.SignatureTypeEnum;
import api.rest.v6.client.AgreementCreationInfo.StateEnum;
import api.rest.v6.client.AgreementCreationInfoParticipantSetsInfo.RoleEnum;
import api.rest.v6.client.AgreementCreationResponse;
import api.rest.v6.client.AgreementDocuments;
import api.rest.v6.client.SigningUrlResponseSigningUrlSetInfos;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.rest.client.RestClients;

@IvyProcessTest(enableWebServer = true)
@ExtendWith(MultiEnvironmentContextProvider.class)
public class TestAgreementsService extends BaseSetup {

  protected static final String AGREEMENTS = "Agreements";
  private static final BpmElement testeeCreateAgreement =
      BpmProcess.path("connector/Agreements").elementName("createAgreement(AgreementCreationInfo)");

  private static final BpmElement testeeGetDocuments =
      BpmProcess.path("connector/Agreements").elementName("getDocuments(String)");

  private static final BpmElement testeeDownloadDocument =
      BpmProcess.path("connector/Agreements").elementName("dowloadDocument(String, String, String, Boolean)");

  private static final BpmElement testeeGetSigningUrls =
      BpmProcess.path("connector/Agreements").elementName("getSigningURLs(String,String)");


  @AfterEach
  void afterEach(IApplication app) {
    RestClients clients = RestClients.of(app);
    clients.remove(AGREEMENTS);
  }

  @TestTemplate
  public void createAgreement(BpmClient bpmClient, ExtensionContext context) throws IOException {
    AgreementCreationInfo agreement = createTestAgreement();
    ExecutionResult result =
        bpmClient.start().subProcess(testeeCreateAgreement).withParam("agreement", agreement).execute();
    AgreementsData data = result.data().last();
    if (isRealTest) {
      int error = (int) data.getError().getAttribute("RestClientResponseStatusCode");
      assertThat(error).isEqualTo(404);
    } else {
      AgreementCreationResponse response = data.getAgreementCreationResponse();
      assertThat(response).isNotNull();
      assertThat(response.getId()).isNotEmpty();
    }
  }

  @TestTemplate
  public void getDocuments(BpmClient bpmClient, ExtensionContext context) throws IOException {
    String agreementId = "test-agreement-id";

    ExecutionResult result =
        bpmClient.start().subProcess(testeeGetDocuments).withParam("agreementId", agreementId).execute();
    AgreementsData data = result.data().last();
    if (isRealTest) {
      int error = (int) data.getError().getAttribute("RestClientResponseStatusCode");
      assertThat(error).isEqualTo(404);
    } else {
      AgreementDocuments response = data.getDocuments();
      assertThat(response).isNotNull();
      assertThat(response.getDocuments()).isNotEmpty();
    }
  }

  @TestTemplate
  public void downloadDocument(BpmClient bpmClient, ExtensionContext context) throws IOException {
    if (isRealTest) {
      return; // Skip test in real-call context
    }
    String agreementId = "test-agreement-id";
    String documentId = "test-document-id";
    String filename = "sample.pdf";
    Boolean asFile = Boolean.TRUE;

    ExecutionResult result = bpmClient.start().subProcess(testeeDownloadDocument).withParam("agreementId", agreementId)
        .withParam("documentId", documentId).withParam("filename", filename).withParam("asFile", asFile).execute();
    AgreementsData data = result.data().last();
    DownloadResult downloadResult = data.getDownload();
    if (downloadResult.getError() != null) {
      Ivy.log().error(downloadResult.getError());
    }
    assertThat(downloadResult.getFile()).isNotNull();
  }

  @TestTemplate
  public void getSigningUrls(BpmClient bpmClient, ExtensionContext context) throws IOException {
    String agreementId = "test-agreement-id";
    String frameParent = "test";
    ExecutionResult result = bpmClient.start().subProcess(testeeGetSigningUrls).withParam("agreementId", agreementId)
        .withParam("frameParent", frameParent).execute();
    AgreementsData data = result.data().last();
    if (isRealTest) {
      int error = (int) data.getError().getAttribute("RestClientResponseStatusCode");
      assertThat(error).isEqualTo(404);
    } else {
      List<SigningUrlResponseSigningUrlSetInfos> signingUrls = data.getSigningUrls();
      assertThat(signingUrls).isNotEmpty();
    }
  }

  private AgreementCreationInfo createTestAgreement() {
    AgreementCreationInfo agreement = new AgreementCreationInfo();

    agreement.setName("test name");
    agreement.setMessage("Please sign this document!");
    agreement.setSignatureType(SignatureTypeEnum.ESIGN);
    agreement.setState(StateEnum.IN_PROCESS);

    // add signers
    AdobeSignService.getInstance().createParticipantInfoForEmail(Arrays.asList("testEmail@test.test"), RoleEnum.SIGNER);
    agreement.setParticipantSetsInfo(AdobeSignService.getInstance()
        .createParticipantInfoForEmail(Arrays.asList("testEmail@test.test"), RoleEnum.SIGNER));

    // add documentIds - need to be already transferred with the upload document
    // service
    agreement
        .setFileInfos(AdobeSignService.getInstance().createFileInfosForDocumentIds(Arrays.asList("test-document-id")));

    agreement.setEmailOption(AdobeSignService.getInstance().createAllDisabledSendOptions());
    return agreement;
  }

  @Override
  public String getClientName() {
    return AGREEMENTS;
  }
}
