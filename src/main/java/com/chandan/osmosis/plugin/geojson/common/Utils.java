package com.chandan.osmosis.plugin.geojson.common;

import com.chandan.geojson.model.CommonProperties;
import com.chandan.geojson.model.Geometry;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.TagCollection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

	private static ObjectMapper objectMapper = new ObjectMapper();

	public static String jsonEncode(Object o) throws JsonProcessingException {
		if (o == null)
			return "";
		return objectMapper.writeValueAsString(o);
	}

	@SuppressWarnings("unchecked")
	public static <T> T jsonDecode(byte[] data, TypeReference<T> t) throws Exception {

		if (data == null) {
			return null;
		}
		return (T) objectMapper.readValue(data, t);
	}
}
