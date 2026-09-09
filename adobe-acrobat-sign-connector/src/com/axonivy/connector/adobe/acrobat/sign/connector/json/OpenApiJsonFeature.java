package com.axonivy.connector.adobe.acrobat.sign.connector.json;

import com.axonivy.connector.adobe.acrobat.sign.connector.auth.JacksonUtils;

import ch.ivyteam.ivy.rest.client.mapper.JsonFeature;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.FeatureContext;
import tools.jackson.jakarta.rs.json.JacksonJsonProvider;

/**
 * JSON object mapper that complies with generated JAX-RS client pojos.
 *
 * @since 9.2
 */
public class OpenApiJsonFeature extends JsonFeature {
	@Override
	public boolean configure(FeatureContext context) {
		JacksonJsonProvider provider = JacksonUtils.getJaxRsClientJsonProvider();
		configure(provider, context.getConfiguration());
		context.register(provider, Priorities.ENTITY_CODER);
		return true;
	}
}
