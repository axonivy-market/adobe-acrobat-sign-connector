package com.axonivy.connector.adobe.acrobat.sign.connector.ui.bean;

import java.io.Serializable;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;

import com.axonivy.connector.adobe.acrobat.sign.connector.enums.AdobeVariable;
import com.axonivy.connector.adobe.acrobat.sign.connector.service.AdminSetupService;

@Named
@ViewScoped
public class AdminSetupBean implements Serializable {
	public String getVariableName(AdobeVariable var) {
		return var.getVariableName();
	}

	public String getRedirectUri() {
		return AdminSetupService.createRedirectUrl();
	}
}
