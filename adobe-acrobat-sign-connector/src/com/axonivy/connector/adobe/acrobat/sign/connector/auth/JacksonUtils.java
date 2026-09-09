package com.axonivy.connector.adobe.acrobat.sign.connector.auth;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ivyteam.ivy.environment.Ivy;
import jakarta.ws.rs.core.MediaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.jakarta.rs.json.JacksonJsonProvider;

public class JacksonUtils {
	private static final JaxRsClientJson JAXRS_CLIENT_JSON = new JaxRsClientJson();
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static JaxRsClientJson getJaxRsClientJsonProvider() {
		return JAXRS_CLIENT_JSON;
	}

	public static String writeObjectAsJson(Object entity) {
		try {
			return OBJECT_MAPPER.writeValueAsString(entity);
		} catch (JsonProcessingException e) {
			Ivy.log().warn(e.getMessage());
		}
		return null;
	}

	private static class JaxRsClientJson extends JacksonJsonProvider {
		@Override
		public JsonMapper locateMapper(Class<?> type, MediaType mediaType) {
			return super.locateMapper(type, mediaType).rebuild()
					// Accept generated API fields regardless of their JSON name casing.
					.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
					// Omit optional null values from requests to remote services.
					.changeDefaultPropertyInclusion(_ -> com.fasterxml.jackson.annotation.JsonInclude.Value.construct(
							Include.NON_NULL, Include.NON_NULL))
					.build();
		}
	}

	public static <T> T convertJsonToObject(String json, Class<T> objectType) {
		if (StringUtils.isEmpty(json)) {
			return null;
		}
		try {
			return OBJECT_MAPPER.readValue(json, objectType);
		} catch (JsonProcessingException e) {
			Ivy.log().warn(e.getMessage());
		}
		return null;
	}
}
