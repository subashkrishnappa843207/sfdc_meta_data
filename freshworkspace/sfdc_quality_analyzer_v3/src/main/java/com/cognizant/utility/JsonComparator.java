package com.cognizant.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import io.restassured.path.json.JsonPath;

public final class JsonComparator {
	public static Map<String, List<Map<String, List<String>>>> RESULT_MAP = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public void fieldAnalysis(String jsonContent, String objectName)
			throws IOException, InstantiationException, IllegalAccessException {
		List<Map<?, ?>> list1 = capture("field" + objectName + ".json", "fields", ArrayList.class);
		Map<String, List<Map<?, ?>>> oldJsonMap = compareFields(list1, new ArrayList<Map<?, ?>>(), true, "label");

		List<Map<?, ?>> list2 = captureJson(jsonContent, "fields", ArrayList.class);
		Map<String, List<Map<?, ?>>> newJsonMap = compareFields(list2, new ArrayList<Map<?, ?>>(), true, "label");
		List<String> fieldList = compareList(oldJsonMap, newJsonMap, new ArrayList<String>());
		if (fieldList.size() > 0) {
			SFDCContext.generateJsonFiles(jsonContent, "field" + objectName);
		}
		addToResultMap(objectName, "field", fieldList);
	}

	@SuppressWarnings("unchecked")
	public void relatedListAnalysis(String objectName, String pageLayoutName, String jsonContent)
			throws IOException, InstantiationException, IllegalAccessException {
		List<Map<?, ?>> list1 = capture("relatedList" + objectName + pageLayoutName + ".json", "Metadata.relatedLists",
				ArrayList.class);
		Map<String, List<Map<?, ?>>> oldJsonMap = compareRelatedList(list1, new ArrayList<Map<?, ?>>(), true,
				"relatedList");

		List<Map<?, ?>> list2 = captureJson(jsonContent, "Metadata.relatedLists", ArrayList.class);
		Map<String, List<Map<?, ?>>> newJsonMap = compareRelatedList(list2, new ArrayList<Map<?, ?>>(), true,
				"relatedList");
		List<String> relatedList = compareList(oldJsonMap, newJsonMap, new ArrayList<String>());
		if (relatedList.size() > 0) {
			SFDCContext.generateJsonFiles(jsonContent, "relatedList" + objectName + pageLayoutName);
		}
		addToResultMap(objectName, "relatedList", relatedList);
	}

	@SuppressWarnings("unchecked")
	public void relatedListAnalysis(String objectName, String jsonContent)
			throws IOException, InstantiationException, IllegalAccessException {
		List<List<Map<?, ?>>> list1 = capture("relatedList" + objectName + ".json", "layouts.relatedLists",
				ArrayList.class);
		Map<String, List<Map<?, ?>>> oldJsonMap = compareRelatedList(list1, new ArrayList<Map<?, ?>>(), true, "label",
				objectName);

		List<List<Map<?, ?>>> list2 = captureJson(jsonContent, "layouts.relatedLists", ArrayList.class);
		Map<String, List<Map<?, ?>>> newJsonMap = compareRelatedList(list2, new ArrayList<Map<?, ?>>(), true, "label",
				objectName);
		List<String> relatedList = compareList(oldJsonMap, newJsonMap, new ArrayList<String>());
		if (relatedList.size() > 0) {
			SFDCContext.generateJsonFiles(jsonContent, "relatedList" + objectName);
		}
		addToResultMap("relatedList", relatedList);
	}

	@SuppressWarnings("unchecked")
	public void buttonAnalysis(List<String> buttonChangeList)
			throws IOException, InstantiationException, IllegalAccessException {
		List<Map<?, ?>> list1 = capture("button.json", "", ArrayList.class);
		Map<String, List<Map<?, ?>>> oldJsonMap = compareRelatedList(list1, new ArrayList<Map<?, ?>>(), true,
				"FullName");

		List<Map<?, ?>> list2 = captureJson(buttonChangeList.toString(), "", ArrayList.class);
		Map<String, List<Map<?, ?>>> newJsonMap = compareRelatedList(list2, new ArrayList<Map<?, ?>>(), true,
				"FullName");
		List<String> buttonList = compareList(oldJsonMap, newJsonMap, new ArrayList<String>());
		if (buttonList.size() > 0) {
			SFDCContext.generateJsonFiles(buttonChangeList.toString(), "button");
		}
		addToResultMap("button", buttonList);
	}

	public static <T> T capture(String filePath, String jsonPath, Class<T> objectType)
			throws InstantiationException, IllegalAccessException {
		try {
			return (T) new JsonPath(
					new File(System.getProperty("user.dir") + "//Analyzer//Results//Json_Reports//" + filePath))
							.getObject(jsonPath, objectType);
		} catch (Exception e) {
			return objectType.newInstance();
		}
	}

	public static <T> T capture(List<String> changeList, Class<T> objectType) {
		Gson gson = new Gson();
		String value = gson.toJson(changeList);
		return (T) gson.fromJson(value, objectType);
	}

	public static <T> T captureJson(String json, String jsonPath, Class<T> objectType) {
		return new JsonPath(json).getObject(jsonPath, objectType);
	}

	private synchronized void addToResultMap(String objectName, String content, List<String> resultList) {
		if (resultList.size() > 0) {
			boolean flag = false;
			Map<String, List<String>> map = new HashMap<>();
			map.put(content, resultList);
			if (RESULT_MAP.containsKey(objectName)) {
				List<Map<String, List<String>>> internList = RESULT_MAP.get(objectName);
				for (Map<String, List<String>> internMap : internList) {
					if (internMap.containsKey(content)) {
						flag = true;
						List<String> innerList = internMap.get(content);
						for (String value : resultList) {
							innerList.add(value);
						}
						internMap.put(content, innerList);
						break;
					}
				}
			}
			if (!flag)
				RESULT_MAP.computeIfAbsent(objectName, k -> new ArrayList<Map<String, List<String>>>()).add(map);
		}
	}

	private synchronized void addToResultMap(String content, List<String> resultList) {
		for (String result : resultList) {
			String objectName = result.split("\\.")[0];
			List<String> list = new ArrayList<>();
			list.add(result.split("\\.")[1]);
			addToResultMap(objectName, content, list);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, List<Map<?, ?>>> compareFields(List<Map<?, ?>> fieldList, List<Map<?, ?>> jsonList,
			boolean flag, String keyName) {
		Map<String, List<Map<?, ?>>> resultFieldsMap = new HashMap<>();
		for (Map<?, ?> fieldMap : fieldList) {
			String labelName = null;
			for (Map.Entry<?, ?> map : fieldMap.entrySet()) {
				Map<String, Object> resultMap = new HashMap<>();
				if (map.getValue() instanceof List<?> && !map.getKey().equals("referenceTo")) {
					compareFields((List<Map<?, ?>>) map.getValue(), jsonList, false, keyName);
				} else {
					resultMap.put((String) map.getKey(), map.getValue());
					jsonList.add(resultMap);
				}

				if (map.getKey().equals(keyName))
					labelName = (String) map.getValue();
			}
			resultFieldsMap.put(labelName, jsonList);
			if (flag)
				jsonList = new ArrayList<Map<?, ?>>();
		}
		return resultFieldsMap;
	}

	public static Map<String, List<Map<?, ?>>> compareRelatedList(List<Map<?, ?>> fieldList, List<Map<?, ?>> jsonList,
			boolean flag, String keyName) {
		Map<String, List<Map<?, ?>>> resultFieldsMap = new HashMap<>();
		if (fieldList.size() != 0) {
			for (Map<?, ?> fieldMap : fieldList) {
				String labelName = null;
				for (Map.Entry<?, ?> map : fieldMap.entrySet()) {
					Map<String, Object> resultMap = new HashMap<>();
					resultMap.put((String) map.getKey(), map.getValue());
					jsonList.add(resultMap);

					if (map.getKey().equals(keyName))
						labelName = (String) map.getValue();
				}
				resultFieldsMap.put(labelName, jsonList);
				if (flag)
					jsonList = new ArrayList<Map<?, ?>>();
			}
		}
		return resultFieldsMap;
	}

	public static Map<String, List<Map<?, ?>>> compareRelatedList(List<List<Map<?, ?>>> fieldList1,
			List<Map<?, ?>> jsonList, boolean flag, String keyName, String objectName) {
		Map<String, List<Map<?, ?>>> resultFieldsMap = new HashMap<>();
		for (List<Map<?, ?>> fieldList : fieldList1) {
			if (fieldList.size() != 0) {
				for (Map<?, ?> fieldMap : fieldList) {
					String labelName = null;
					for (Map.Entry<?, ?> map : fieldMap.entrySet()) {
						Map<String, Object> resultMap = new HashMap<>();
						resultMap.put((String) map.getKey(), map.getValue());
						jsonList.add(resultMap);

						if (map.getKey().equals(keyName))
							labelName = objectName + "." + (String) map.getValue();
					}
					resultFieldsMap.put(labelName, jsonList);
					if (flag)
						jsonList = new ArrayList<Map<?, ?>>();
				}
			}
		}
		return resultFieldsMap;
	}

	public static List<String> compareList(Map<String, List<Map<?, ?>>> oldJsonMap,
			Map<String, List<Map<?, ?>>> newJsonMap, List<String> resultList) {
		for (Map.Entry<String, List<Map<?, ?>>> map : oldJsonMap.entrySet()) {
			if (newJsonMap.containsKey(map.getKey())) {
				List<Map<?, ?>> oldJsonList = map.getValue();
				List<Map<?, ?>> newJsonList = newJsonMap.get(map.getKey());
				if (!oldJsonList.equals(newJsonList)) {
					resultList.add(map.getKey());
					System.out.println("Field got changed - " + map.getKey());
				}
			} else {
				resultList.add(map.getKey());
				System.out.println("Field deleted - " + map.getKey());
			}
		}

		for (Map.Entry<String, List<Map<?, ?>>> map : newJsonMap.entrySet()) {
			if (!oldJsonMap.containsKey(map.getKey())) {
				resultList.add(map.getKey());
				System.out.println("Field added - " + map.getKey());
			}
		}
		return resultList;
	}
}
