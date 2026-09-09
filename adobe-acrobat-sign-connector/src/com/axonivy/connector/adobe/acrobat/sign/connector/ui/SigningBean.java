package com.axonivy.connector.adobe.acrobat.sign.connector.ui;

import java.io.Serializable;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;

import api.rest.v6.client.AgreementDocuments;

@Named
@ViewScoped
public class SigningBean implements Serializable {

	private String agreementId;
	private String documentId;
	private AgreementDocuments documents;

	public void createAgreement() {

	}

	public String getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(String agreementId) {
		this.agreementId = agreementId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public AgreementDocuments getDocuments() {
		return documents;
	}

	public void setDocuments(AgreementDocuments documents) {
		this.documents = documents;
	}
}
