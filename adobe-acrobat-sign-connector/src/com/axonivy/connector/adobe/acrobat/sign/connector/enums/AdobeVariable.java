package com.axonivy.connector.adobe.acrobat.sign.connector.enums;

import ch.ivyteam.ivy.environment.Ivy;

public enum AdobeVariable {
	BASE_URI("adobeAcrobatSignConnector.baseUri"),
	HOST("adobeAcrobatSignConnector.host"),
	INTEGRATION_KEY("adobeAcrobatSignConnector.integrationKey"),
	RETURN_PAGE("adobeAcrobatSignConnector.returnPage"),
	PERMISSIONS("adobeAcrobatSignConnector.permissions"),
	CLIENT_ID("adobeAcrobatSignConnector.clientId"),
	CLIENT_SECRET("adobeAcrobatSignConnector.clientSecret"),
	APP_ID("adobeAcrobatSignConnector.appId"),
	SECRET_KEY("adobeAcrobatSignConnector.secretKey"),
	USE_APP_PERMISSIONS("adobeAcrobatSignConnector.useAppPermissions"),
	CODE("adobeAcrobatSignConnector.code"),
	USE_USER_PASS_FLOW_ENABLED("adobeAcrobatSignConnector.useUserPassFlow.enabled"),
	USE_USER_PASS_FLOW_USER("adobeAcrobatSignConnector.useUserPassFlow.user"),
	USE_USER_PASS_FLOW_PASS("adobeAcrobatSignConnector.useUserPassFlow.pass"),
	OAUTH_TOKEN("adobeAcrobatSignConnector.oauthToken"),
	ACCESS_TOKEN("adobeAcrobatSignConnector.accessToken"),
	AUTHENTICATION_URI("adobeAcrobatSignConnector.authenticationUri");

	private String variableName;

	private AdobeVariable(String variableName) {
		this.variableName = variableName;
	}

	public String getVariableName() {
		return variableName;
	}

	public String getValue() {
		return Ivy.var().get(variableName);
	}

	/**
	 * Sets new value to the Variable
	 * @param newValue
	 * @return old value that was set before. Empty string if it was not defined.
	 */
	public String updateValue(String newValue) {
		return Ivy.var().set(variableName, newValue);
	}
}
