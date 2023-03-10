package io.mosip.tf.idpass.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import io.mosip.tf.idpass.dto.JsonValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The IdentFields::parse() method will walk into the json tree and extracts out
 * the value of fields that we are interested.
 *
 * A second pass is then performed on the data that is gathered on the first
 * pass: 1) Must comply to mandatory attributes 2) Date must be valid date 3)
 * Total sum of bytes must fit QR code 4) Language preference localization
 *
 * Lastly, a preset fields is populated with their values.
 *
 */

public class IdentFields {

	/**
	 * The stack stk variable keeps track of nested key paths as the json tree is
	 * being traversed. It's possible future use is to be able to express json full
	 * path key name for extraction, such as:
	 *
	 * Record.Person.Id
	 *
	 * To specifically and unambiguously extract the 'Id' field.
	 */

	private Stack<String> stk = new Stack<>();

	/**
	 * The parseFields contains the gathered fields based on the configured list of
	 * fields of interest.
	 */

	private Map<String, Object> parsedFields = new HashMap<>();

	// Values populated from json config
	public static List<String> prefLangs = new ArrayList<>();

	/**
	 * The fieldsOfInterest member field are the member fields of the type class
	 * that contains the constrained type input fields for Ident.Builder class. The
	 * member field's name is search for in the configuration json, and the found
	 * json value must render to the member's type.
	 */

	List<String> fieldsOfInterest = new ArrayList<>();

	/**
	 * The GenderMap class is a DTO that reads the configured values in the json
	 * file corresponding to gender for each preferred language.
	 */

	static class GenderMap {
		public List<String> male;
		public List<String> female;
	}

	public static Map<String, IdentFields.GenderMap> genderMap = new HashMap<>();

	/**
	 * Sets the json configuration that defines the additional constraint rules
	 * associated with the IdentFieldsConstraint.java class. Whereas the class
	 * defines the type, the json defines the rest of the configurable parameters
	 * that includes the following:
	 *
	 * - which member fields are mandatory - the date format - preferred languages -
	 * the gender map to int for gender
	 *
	 * @param is Is the json input stream
	 * @throws IOException Standard exception
	 */

	public static void setConfig(InputStream is) throws IOException {
		String jsonstr = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines()
				.collect(Collectors.joining());

		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(jsonstr);
		JsonNode langs = node.get("prefLangs");

		for (JsonNode lang : langs) {
			Iterator<Map.Entry<String, JsonNode>> kv = lang.fields();
			if (kv.hasNext()) {
				Map.Entry<String, JsonNode> obj = kv.next();
				String langName = obj.getKey();
				prefLangs.add(langName);
				IdentFields.GenderMap gmap = mapper.treeToValue(obj.getValue(), IdentFields.GenderMap.class);
				genderMap.put(langName, gmap);
			}
		}
	}

	/**
	 * The defined member fields in 'clazz' are the fields of interest to look for.
	 * 
	 * @param clazz A decoupled type whose member fields defines the fields of
	 *              interest to search for in the input json
	 */

	public IdentFields(Class<?> clazz) throws IOException {
		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			fieldsOfInterest.add(fields[i].getName());
		}

		InputStream is = IdentFields.class.getClassLoader().getResourceAsStream("identfieldsconstraint.json");
		IdentFields.setConfig(is);
	}

	/**
	 * The parse method is a decoupled method, and currently it is indirectly
	 * associated with the IdentFieldsConstraint.java class.
	 *
	 * The IdentFields class extracts every fields of interest. These fields of
	 * interest are the member fields of 'clazz' parameter.
	 *
	 * @param jsonstr Any input json string
	 * @param clazz   The target type to construct based from the input json
	 * @return
	 * @throws IOException               Standard exception
	 * @throws NoSuchMethodException     Standard exception
	 * @throws IllegalAccessException    Standard exception
	 * @throws InvocationTargetException Standard exception
	 * @throws InstantiationException    Standard exception
	 */

	public static Object parse(String jsonstr, Class<?> clazz) throws IOException, NoSuchMethodException,
			IllegalAccessException, InstantiationException, InvocationTargetException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jnode = objectMapper.readTree(jsonstr);

		IdentFields idf = new IdentFields(clazz);
		idf.traverse(jnode);

		@SuppressWarnings("rawtypes")
		Constructor ctor = clazz.getDeclaredConstructor(Map.class);
		Object obj = ctor.newInstance(idf.parsedFields);
		return obj;
	}

	@SuppressWarnings({ "incomplete-switch", "unused" })
	private void traverse(JsonNode node) throws JsonProcessingException {
		String keyname = "";

		if (!stk.empty()) {
			Iterator<String> iter = stk.listIterator();
			List<String> keynames = new ArrayList<>();
			iter.forEachRemaining(keynames::add);
			keyname = keynames.get(keynames.size() - 1);
		}

		switch (node.getNodeType()) {
		case OBJECT:
			Iterator<Map.Entry<String, JsonNode>> kv = node.fields();
			while (kv.hasNext()) {
				Map.Entry<String, JsonNode> obj = kv.next();
				String key = obj.getKey();
				stk.push(key);
				traverse(obj.getValue());
				stk.pop();
			}

			break;

		case ARRAY:
			outer: for (JsonNode elem : node) {
				ObjectMapper mapper = new ObjectMapper();
				JsonValue localizedValue = null;

				try {
					localizedValue = mapper.treeToValue(elem, JsonValue.class);
				} catch (MismatchedInputException e) {
					continue;
				}

				/*
				 * skip check prefLangs for now for (String pref : prefLangs) { if
				 * (localizedValue.getLanguage().equals(pref)) { addKeyValue(keyname,
				 * localizedValue.getValue()); break outer; } }
				 */

				addKeyValue(keyname, localizedValue.getValue());
			}
			break;

		case STRING:
			// still need to check if the string is a json string
			try {
				JsonNode jsonnode = new ObjectMapper().readTree(node.asText());
				traverse(jsonnode);
			} catch (Exception e) {
				addKeyValue(keyname, node.asText());
			}
			break;

		case NUMBER:
			addKeyValue(keyname, node.numberValue());
			break;

		case BOOLEAN:
			addKeyValue(keyname, node.booleanValue());
			break;

		case NULL:
			addKeyValue(keyname, "");
			break;
		}
	}

	private void addKeyValue(String key, Object value) {
		if (fieldsOfInterest.contains(key)) {
			parsedFields.put(key, value);
		}
	}
}
