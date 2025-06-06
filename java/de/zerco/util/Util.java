package de.zerco.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Util {

	public static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String LOWER_CASE = UPPER_CASE.toLowerCase();
	public static final String DIGITS = "0123456789";
	public static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?";
	public static final String CHARACTER_SET = UPPER_CASE + LOWER_CASE + DIGITS + SPECIAL_CHARACTERS;
	public static final String[] NUMBERS = { "", " one", " two", " three", " four", " five", " six", " seven", " eight", " nine", " ten", " eleven", " twelve", " thirteen", " fourteen", " fifteen", " sixteen", " seventeen", " eighteen", " nineteen" };
    public static final String[] TENS = { "", " ten", " twenty", " thirty", " forty", " fifty", " sixty", " seventy", " eighty", " ninety" };
    public static final Map<String, Object> WORDS = new HashMap<>();
    public static final String FILE_PATH = "C:\\Users\\shett\\json_schema.xlsx";
    
    static {
        WORDS.put("zero", 0);
        WORDS.put("one", 1);
        WORDS.put("two", 2);
        WORDS.put("three", 3);
        WORDS.put("four", 4);
        WORDS.put("five", 5);
        WORDS.put("six", 6);
        WORDS.put("seven", 7);
        WORDS.put("eight", 8);
        WORDS.put("nine", 9);
        WORDS.put("ten", 10);
        WORDS.put("eleven", 11);
        WORDS.put("twelve", 12);
        WORDS.put("thirteen", 13);
        WORDS.put("fourteen", 14);
        WORDS.put("fifteen", 15);
        WORDS.put("sixteen", 16);
        WORDS.put("seventeen", 17);
        WORDS.put("eighteen", 18);
        WORDS.put("nineteen", 19);
        WORDS.put("twenty", 20);
        WORDS.put("thirty", 30);
        WORDS.put("forty", 40);
        WORDS.put("fifty", 50);
        WORDS.put("sixty", 60);
        WORDS.put("seventy", 70);
        WORDS.put("eighty", 80);
        WORDS.put("ninety", 90);
        WORDS.put("hundred", 100);
    }
    
    public static int convertWordToNumber(String word) {
        String[] words = word.split("\\s+");
        int totalValue = 0;
        int currentValue = 0;
        for (String element : words) {
            if ("hundred".equals(element)) {
                currentValue *= 100;
            } else if ("thousand".equals(element)) {
                currentValue *= 1000;
                totalValue += currentValue;
                currentValue = 0;
            } else if ("lakh".equals(element)) {
                currentValue *= 100000;
                totalValue += currentValue;
                currentValue = 0;
            } else if ("crore".equals(element)) {
                currentValue *= 10000000;
                totalValue += currentValue;
                currentValue = 0;
            } else {
                Integer value = (Integer) WORDS.get(element);
                   currentValue += value;
            }
        }
        return totalValue + currentValue;
    }

	public static String generateCaptcha(int size) {
        if (size < 4) {
            throw new IllegalArgumentException("Captcha size must be at least 4");
        }
        Random random = new Random();
        String captcha = "";
        captcha += UPPER_CASE.charAt(random.nextInt(UPPER_CASE.length()));
        captcha += LOWER_CASE.charAt(random.nextInt(LOWER_CASE.length()));
        captcha += DIGITS.charAt(random.nextInt(DIGITS.length()));
        captcha += SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length()));
        for (int i = 4; i < size; i++) {
            captcha += CHARACTER_SET.charAt(random.nextInt(CHARACTER_SET.length()));
        }
        return captcha;
    }
	
	public static List<Object> findValuesForKey(String json, String key, boolean flag) {
        List<Object> values = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            findValuesForKey(jsonObject, key, values, flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

	public static void findValuesForKey(Object object, String key, List<Object> values, boolean flag) {
		if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;
			for (String label : jsonObject.keySet()) {
				Object data = jsonObject.get(label);
				if (label.equals(key)) {
					if (flag) {
						if (data instanceof String) {
							values.add(data);
						}
					} else {
						values.add(data);
					}
				}
				if (data instanceof JSONObject || data instanceof JSONArray) {
					findValuesForKey(data, key, values, flag);
				}
			}
		} else if (object instanceof JSONArray) {
			JSONArray arrayObject = (JSONArray) object;
			for (Object element : arrayObject) {
				findValuesForKey(element, key, values, flag);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object getValueForKey(String key, Map<String, Object> data) {
		String[] keys = key.split("\\.");
        Object value = data;
        for (String label : keys) {
            if (!(value instanceof Map)) {
                return null; 
            }
            value = ((Map<String, Object>) value).get(label);
        }
        return value;
	}
	
	public static String convertNumberToWord(int number) {
    	if (number <  0) return "";
        String word = "";
        if (number == 0) {
            word = "zero";
        } else if (number < 20) {
            word = NUMBERS[number];
        } else if (number < 100) {
            int tens = number / 10;
            int ones = number % 10;
            word = TENS[tens];
            if (ones > 0) {
                word += NUMBERS[ones];
            }
        } else if (number < 1000) {
            int hundreds = number / 100;
            int remainder = number % 100;
            word = NUMBERS[hundreds] + " hundred";
            if (remainder > 0) {
                word += " " + convertToDigit(remainder);
            }
        } else if (number < 100000) {
            int thousands = number / 1000;
            int remainder = number % 1000;
            word = convertToDigit(thousands) + " thousand";
            if (remainder > 0) {
                word += " " + convertNumberToWord(remainder);
            }
        } else if (number < 10000000) {
            int lakhs = number / 100000;
            int remainder = number % 100000;
            word = convertToDigit(lakhs) + " lakh";
            if (remainder > 0) {
                word += " " + convertNumberToWord(remainder);
            }
        } else if (number < 1000000000) {
            int crores = number / 10000000;
            int remainder = number % 10000000;
            word = convertToDigit(crores) + " crore";
            if (remainder > 0) {
                word += " " + convertNumberToWord(remainder);
            }
        }
        return word.trim();
    }
	
	public static String convertToDigit(int number) {
    	if (number < 0) return "";
        if (number < 20) {
            return NUMBERS[number];
        } else {
            int tens = number / 10;
            int ones = number % 10;
            return TENS[tens] + (ones > 0 ? NUMBERS[ones] : "");
        }
    }
	
	public static String convertMapToMarkdown(Map<String, Object> map) {
	    return convertMapToMarkdown(map, 0);
	}

	@SuppressWarnings("unchecked")
	public static String convertMapToMarkdown(Object object, int indentation) {
	    String result = "";
	    String space = "  ".repeat(indentation); 
	    if (object instanceof Map<?, ?>) {
	        Map<String, Object> map = (Map<String, Object>) object;
	        for (Map.Entry<String, Object> entry : map.entrySet()) {
	            Object value = entry.getValue();
	            if (value instanceof Map<?, ?> || value instanceof List<?>) {
	                result += space + "- **" + entry.getKey() + "**:\n";
	                result += convertMapToMarkdown(value, indentation + 1);
	            } else {
	                result += space + "- **" + entry.getKey() + "**: " + getValue(value) + "\n";
	            }
	        }
	    } else if (object instanceof List<?>) {
	        List<Object> list = (List<Object>) object;
	        for (Object element : list) {
	            if (element instanceof Map<?, ?> || element instanceof List<?>) {
	                result += space + "- \n";
	                result += convertMapToMarkdown(element, indentation + 1);
	            } else {
	                result += space + "- " + getValue(element) + "\n";
	            }
	        }
	    }
	    return result;
	}

	public static String getValue(Object value) {
	    return (value == null) ? "null" : value.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRequiredKeyValues(Map<String, Object> map, Map<String, Object> metadata) {
		Map<String, Object> result = new HashMap<>();
		for (String key : map.keySet()) {
	        Object metaValue = metadata.get(key);
	        boolean isIgnored = false;
	        if (metaValue instanceof Map<?, ?>) {
	            Map<String, Object> metaMap = (Map<String, Object>) metaValue;
	            Object flag = metaMap.get("is_ignore");
	            isIgnored = flag.equals(true);
	        }
	        if (!isIgnored) {
	            result.put(key, map.get(key));
	        }
	    }
	    return result;
	}
	
	public static String convertExcelToJson(String path) {
	    String result = "";
	    XSSFWorkbook workbook = null;
	    try {
	        workbook = new XSSFWorkbook(new File(path));
	        XSSFSheet sheet = workbook.getSheetAt(0);
	        ObjectMapper objectMapper = new ObjectMapper();
	        ObjectNode json = objectMapper.createObjectNode();
	        for (Row row : sheet) {
	            if (row.getRowNum() == 0 || row == null || row.getCell(0) == null) continue;
	            String key = row.getCell(0).getStringCellValue().trim();
	            ObjectNode jsonNode = objectMapper.createObjectNode();
	            Cell typeKey = row.getCell(1);
	            if (typeKey != null && typeKey.getCellType() == CellType.STRING) {
	                String datatype = typeKey.getStringCellValue().trim();
	                if (!datatype.isEmpty()) {
	                    jsonNode.put("type", datatype(datatype));
	                }
	            }
	            Cell descriptionKey = row.getCell(2);
	            if (descriptionKey != null) {
	                String description = descriptionKey.getStringCellValue().trim();
	                if (!description.isEmpty()) {
	                    jsonNode.put("description", description);
	                }
	            }
	            Cell enumKey = row.getCell(3);
	            if (enumKey != null) {
	                String enumValues = enumKey.getStringCellValue().trim();
	                if (!enumValues.isEmpty()) {
	                    jsonNode.set("enum", convertToEnumArray(enumValues, objectMapper));
	                }
	            }
	            json.set(key, jsonNode);
	        }
	        result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        closeWorkbook(workbook);
	    }
	    return result;
	}

	public static void closeWorkbook(Workbook workbook) {
		try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public static String datatype(String type) {
	    switch (type.toLowerCase()) {
	        case "int": return "number";
	        case "true":
	        case "false":
	        case "boolean": return "boolean";
	        case "string": return "string";
	        default: return type;
	    }
	}
 
	public static ArrayNode convertToEnumArray(String values, ObjectMapper mapper) {
	    ArrayNode enumArray = mapper.createArrayNode();
	    for (String value : values.split(",")) {
	        value = value.trim();
	        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
	            enumArray.add(Boolean.parseBoolean(value));
	        } else {
	            try {
	                enumArray.add(Integer.parseInt(value));
	            } catch (NumberFormatException e) {
	                enumArray.add(value);
	            }
	        }
	    }
	    return enumArray;
	}

	public static void main(String[] args) {
//		String input = "{\"name\":\"my name is tejaswini\",\"place\":\"i am from hyd\",\"studies\":\"i am an electrical engineer\",\"properties\":{\"name\":\"shiva\"},\"array\":[{\"name\":\"jagadeesh\"},{\"name\":\"mahesh\",\"properties\":{\"name\":\"shashi\"}}],\"map\":{\"array\":[{\"name\":\"shankar\"}]},\"fields\":[{\"array\":[{\"name\":1}],\"name\":2}]}";
//	    System.out.println(getString(input, "name", false));
//        System.out.println("Captcha: " + generateCaptcha(4));
        Map<String, Object> reqData = Map.of("name", "tejaswini", 
        	    "properties", Map.of(
        	        "name", "Shetty",
        	        "surname", "shetty"
        	    )
        	);
        System.out.println(getValueForKey("name", reqData));
//        Map<String, Object> json = new HashMap<>();
//        json.put("name", "Tejaswini");
//        Map<String, Object> details = new HashMap<>();
//        details.put("age", 24);;
//        Map<String, Object> address = new HashMap<>();
//        address.put("city", "Hyderabad");
//        address.put("state", "Telangana");
//        details.put("address", address);
//        List<Object> skills = new ArrayList<>();
//        skills.add("Java");
//        skills.add("SQL");
//        skills.add("postgre");
//        skills.add("oracle");
//        skills.add("mongo");
//        json.put("skills", skills);
//        json.put("skillset", details);
//        json.put("details", details);
//		String json = "{\"array\":[1,2,3,{\"name\":\"tejaswini\",\"address\":\"hyd\"},[1,2,3]],\"boolean\":true,\"null\":null,\"number\":123,\"object\":{\"a\":\"b\",\"c\":\"d\",\"e\":\"f\"},\"map\":{\"address\":\"wave rock\",\"map\":{\"location\":\"hyd\"},\"array\":[1,2,3]},\"string\":\"Hello World\",\"heading\":\"Heading\",\"sub_heading\":\"Sub Heading\",\"text\":\"i am text\"}";
//		String json = "{\"type\":\"object\",\"description\":\"Top-level page schema including definition and info\",\"properties\":{\"definition\":{\"type\":\"object\",\"description\":\"Definition container for page widgets\",\"properties\":{\"widgets\":{\"type\":\"array\",\"description\":\"Array of widgets on the page\",\"items\":{\"type\":\"object\",\"description\":\"Widget entry with definition and metadata\",\"properties\":{\"definition\":{\"$ref\":\"#/$defs/block_widget\"},\"info\":{\"type\":\"object\",\"description\":\"Metadata for the widget\",\"properties\":{\"type\":{\"type\":\"string\",\"enum\":[\"block\",\"list\",\"form\"],\"description\":\"Widget type (block, form, list)\"}},\"required\":[\"type\"],\"additionalProperties\":false}},\"required\":[\"definition\",\"info\"],\"additionalProperties\":false}}},\"required\":[\"widgets\"],\"additionalProperties\":false},\"info\":{\"type\":\"object\",\"description\":\"Page-level metadata\",\"properties\":{\"type\":{\"type\":\"string\",\"enum\":[\"page\"],\"description\":\"Page type or category\"}},\"required\":[\"type\"],\"additionalProperties\":false}},\"required\":[\"definition\",\"info\"],\"additionalProperties\":false,\"$defs\":{\"block_widget\":{\"type\":\"object\",\"description\":\"Defines a block widget that serves as a container for nested widgets, allowing for flexible and reusable UI components.\",\"properties\":{\"id\":{\"type\":\"string\",\"description\":\"A unique identifier for this block, used to distinguish it within the page builder.\"},\"pbId\":{\"type\":\"string\",\"description\":\"The unique Page Builder ID associated with this block, linking it to the overall page structure.\"},\"style\":{\"type\":\"string\",\"description\":\"Inline CSS styles or a reference to predefined styles for this block, allowing for precise visual control.\"},\"widgets\":{\"type\":\"array\",\"description\":\"A collection of nested widgets contained within this block. Each widget can be a list, form, or another block, supporting deeply nested structures.\",\"items\":{\"type\":\"object\",\"description\":\"Definition of a nested widget. The specific structure depends on the widget type specified in 'info'.\",\"properties\":{\"definition\":{\"anyOf\":[{\"$ref\":\"#/$defs/list_widget\"},{\"$ref\":\"#/$defs/form_widget\"},{\"$ref\":\"#/$defs/block_widget\"}],\"description\":\"The detailed configuration for the nested widget, referencing either a list, form, or another block definition.\"},\"info\":{\"type\":\"object\",\"description\":\"Metadata describing the nested widget, including its type and unique identifier.\",\"properties\":{\"id\":{\"type\":\"string\",\"description\":\"The unique identifier for the nested widget.\"},\"type\":{\"type\":\"string\",\"description\":\"The type of the nested widget, determining which definition is used. Common types include 'list', 'form', and 'block'.\"}},\"required\":[\"id\",\"type\"],\"additionalProperties\":false}},\"required\":[\"definition\",\"info\"],\"additionalProperties\":false}}},\"required\":[\"id\",\"pbId\",\"style\",\"widgets\"],\"additionalProperties\":false},\"list_widget\":{\"type\":\"object\",\"description\":\"Configuration for a List/Grid widget. This structure is specifically required if 'info.type' equals 'list'.\",\"properties\":{\"id\":{\"type\":\"string\",\"description\":\"A unique identifier for the list, used to distinguish it from other lists within the application.\"},\"isGlobalSearch\":{\"type\":\"boolean\",\"description\":\"A flag to enable or disable global search functionality within the list. When set to 'true', the list supports searching across all visible data.\"},\"pbId\":{\"type\":\"string\",\"description\":\"The unique ID associated with the Page Builder that manages the list configuration and rendering.\"},\"showTitle\":{\"type\":\"boolean\",\"description\":\"Indicates whether the list title should be displayed on the user interface. Set to 'true' to make the title visible.\"},\"style\":{\"type\":\"string\",\"description\":\"A string containing CSS styles to customize the appearance of the list, such as font size, color, and layout adjustments.\"},\"title\":{\"type\":\"string\",\"description\":\"The title displayed at the top of the list, providing context or a label for the list's content.\"},\"cols\":{\"type\":\"array\",\"description\":\"An array containing the column definitions that structure the list's data. Each element represents a column configuration and should follow the structure defined in 'list_col_definition'.\",\"items\":{\"$ref\":\"#/$defs/list_col_definition\"}},\"properties\":{\"type\":\"object\",\"description\":\"Additional rendering and behavior configurations for the list/grid widget.\",\"properties\":{\"type\":{\"type\":\"string\",\"description\":\"Specifies the display type of the widget, e.g., 'table'.\"},\"pagination\":{\"type\":\"object\",\"description\":\"Pagination configuration with fixed values.\",\"properties\":{\"position\":{\"type\":\"string\",\"enum\":[\"bottom\"],\"description\":\"Fixed position of the pagination controls.\"},\"displayFormat\":{\"type\":\"string\",\"enum\":[\"Total {{totalRecords?totalRecords:0}}, Pages {{page+1}} - {{totalPages}}\"],\"description\":\"Fixed format string for pagination status display.\"},\"size\":{\"type\":\"string\",\"enum\":[\"10\"],\"description\":\"Fixed number of records per page.\"}},\"required\":[\"position\",\"displayFormat\",\"size\"],\"additionalProperties\":false},\"web\":{\"type\":\"boolean\",\"description\":\"Whether the widget is displayed on the web interface.\"},\"mobile\":{\"type\":\"boolean\",\"description\":\"Whether the widget is displayed on the mobile interface.\"},\"lazy\":{\"type\":\"boolean\",\"description\":\"Enables lazy loading for data.\"},\"shrinkToFit\":{\"type\":\"boolean\",\"description\":\"If false, disables automatic column resizing.\"},\"filter\":{\"type\":\"object\",\"description\":\"Filter configuration for the widget.\",\"properties\":{\"action\":{\"type\":\"object\",\"description\":\"Actions for filter operations.\",\"properties\":{\"submit\":{\"type\":\"object\",\"description\":\"Submit button configuration.\",\"properties\":{\"label\":{\"type\":\"string\",\"description\":\"Text shown on the submit button.\"}},\"required\":[\"label\"],\"additionalProperties\":false}},\"required\":[\"submit\"],\"additionalProperties\":false},\"position\":{\"type\":\"string\",\"description\":\"Position of the filter component, e.g., 'left'.\"},\"displayStyle\":{\"type\":\"string\",\"description\":\"Style used to display filters, e.g., 'dropdown'.\"},\"sortPosition\":{\"type\":\"string\",\"description\":\"Position of sort options relative to the filter.\"},\"submissionMode\":{\"type\":\"string\",\"description\":\"Mode in which filter data is submitted, e.g., 'all'.\"}},\"required\":[\"action\",\"position\",\"displayStyle\",\"sortPosition\",\"submissionMode\"],\"additionalProperties\":false},\"api\":{\"type\":\"object\",\"description\":\"datasource for list widget\",\"properties\":{\"datasourceType\":{\"type\":\"string\",\"enum\":[\"api\"],\"description\":\"Datasource type\"},\"method\":{\"type\":\"string\",\"enum\":[\"get\"],\"description\":\"Api url method type\"},\"url\":{\"type\":\"string\",\"pattern\":\"^api/([^/]+)/(list|select|delete|save-update)/([^/]+)$\",\"examples\":[\"api/user/list/user-list\"],\"description\":\"Api url, here api structure (api/<<entity/table>>/(list|select|delete|save-update)/<<random api code>>), here '<<>>' refered as wildcards you should replace it dynamically. code doesn't start with number. it always be alphabetical.\"}},\"required\":[\"datasourceType\",\"method\",\"url\"],\"additionalProperties\":false}},\"required\":[\"type\",\"pagination\",\"web\",\"mobile\",\"lazy\",\"shrinkToFit\",\"filter\",\"api\"],\"additionalProperties\":false},\"actions\":{\"type\":\"object\",\"description\":\"Actions available in the list for groups and rows.\",\"properties\":{\"group\":{\"type\":\"object\",\"description\":\"Group-level actions configuration.\",\"properties\":{\"displayStyle\":{\"type\":\"string\",\"enum\":[\"button\"],\"description\":\"Display style for group actions.\"},\"items\":{\"type\":\"array\",\"description\":\"An array of actions available at group level.\",\"items\":{\"type\":\"object\",\"properties\":{\"label\":{\"type\":\"string\",\"description\":\"label for the button\"},\"actions\":{\"type\":\"array\",\"description\":\"Actions executed when button fires.\",\"items\":{\"$ref\":\"#/$defs/action_definition\"}}},\"required\":[\"label\",\"actions\"],\"additionalProperties\":false}}},\"required\":[\"displayStyle\",\"items\"],\"additionalProperties\":false},\"row\":{\"type\":\"object\",\"description\":\"Row-level actions configuration.\",\"properties\":{\"displayStyle\":{\"type\":\"string\",\"enum\":[\"button\"],\"description\":\"Display style for row actions.\"}},\"required\":[\"displayStyle\"],\"additionalProperties\":false}},\"required\":[\"group\",\"row\"],\"additionalProperties\":false},\"triggers\":{\"type\":\"array\",\"description\":\"Event triggers for row action/selection\",\"items\":{\"type\":\"object\",\"description\":\"Trigger event configuration\",\"properties\":{\"type\":{\"type\":\"string\",\"enum\":[\"rowselection\"],\"description\":\"Trigger event type (click, ...)\"},\"actions\":{\"type\":\"array\",\"description\":\"Actions executed when trigger fires\",\"items\":{\"$ref\":\"#/$defs/action_definition\"}}},\"required\":[\"type\",\"actions\"],\"additionalProperties\":false}},\"globalFilter\":{\"type\":\"object\",\"description\":\"Global filter for the specified list\",\"properties\":{\"placeholder\":{\"type\":\"string\",\"description\":\"Specifies the display type of the widget, e.g., 'table'.\"}},\"required\":[\"placeholder\"],\"additionalProperties\":false}},\"required\":[\"id\",\"isGlobalSearch\",\"pbId\",\"showTitle\",\"style\",\"title\",\"cols\",\"properties\",\"actions\",\"triggers\",\"globalFilter\"],\"additionalProperties\":false},\"list_col_definition\":{\"type\":\"object\",\"description\":\"Configuration for a single column in a list, defining its structure and behavior.\",\"properties\":{\"name\":{\"type\":\"string\",\"description\":\"The internal identifier for the column, used for data binding and backend processing.If the column type is choose add '.name' as sufix\"},\"label\":{\"type\":\"string\",\"description\":\"The user-facing header text for the column, displayed at the top of the list.\"},\"type\":{\"type\":\"string\",\"description\":\"The data type of the column, specifying how the data should be formatted and displayed. Common types include 'text', 'number', 'date', and 'boolean'.\"},\"sort\":{\"type\":\"boolean\",\"description\":\"Indicates whether the column should be sortable in the list. Set to 'true' to enable sorting.\"}},\"required\":[\"name\",\"label\",\"type\",\"sort\"],\"additionalProperties\":false},\"form_widget\":{\"type\":\"object\",\"description\":\"Configuration for a Form widget. This structure is required if 'info.type' equals 'form'.\",\"properties\":{\"id\":{\"type\":\"string\",\"description\":\"A unique identifier for the form, used to differentiate it from other forms within the application.Only use underscore ('_') eg : 'user_form'\"},\"pbId\":{\"type\":\"string\",\"description\":\"The unique ID associated with the Page Builder that manages this form's structure and behavior.\"},\"showTitle\":{\"type\":\"boolean\",\"description\":\"Determines whether the form title should be displayed. Set to 'true' to make the title visible to the user.\"},\"style\":{\"type\":\"string\",\"description\":\"A string containing CSS styles to customize the form's appearance, including colors, fonts, and layout properties.\"},\"title\":{\"type\":\"string\",\"description\":\"The user-visible title of the form, typically displayed at the top of the form to describe its purpose.\"},\"hidden\":{\"type\":\"boolean\",\"description\":\"Controls the visibility of the form. Set to 'true' to hide the form from the user interface.\"},\"fields\":{\"type\":\"array\",\"description\":\"An array containing grouped form fields. Each item represents a logical grouping of related fields, defined by the 'group_definition' schema.\",\"items\":{\"$ref\":\"#/$defs/group_definition\"}}},\"required\":[\"id\",\"pbId\",\"showTitle\",\"style\",\"title\",\"hidden\",\"fields\"],\"additionalProperties\":false},\"group_definition\":{\"type\":\"object\",\"description\":\"Defines a logical grouping of related form fields, typically used to organize complex forms into sections.\",\"properties\":{\"title\":{\"type\":\"string\",\"description\":\"The title displayed at the top of the group, providing context or a heading for the contained fields.\"},\"containerClass\":{\"type\":\"string\",\"enum\":[\"row justify-content-center\",\"d-flex justify-content-center flex-wrap gap-2\"],\"description\":\"A CSS class applied to the group container. use (d-flex justify-content-center flex-wrap gap-2) for buttons and for remaining use (row justify-content-center) \"},\"fieldItems\":{\"type\":\"array\",\"description\":\"An array containing the form fields within this group. Each field is defined according to the 'form_field_definition' schema.\",\"items\":{\"$ref\":\"#/$defs/form_field_definition\"}},\"id\":{\"type\":\"string\",\"description\":\"A unique identifier for the group, used to differentiate it from other groups in the form.\"}},\"required\":[\"title\",\"containerClass\",\"fieldItems\",\"id\"],\"additionalProperties\":false},\"form_field_definition\":{\"description\":\"Form fields based on its type. If the field type is 'text' choose 'text_field_properties'. If the field type is 'date' and 'datetime' and 'time' choose 'date_field_properties'.If the field type is 'number' choose 'number_field_properties'.If the field type is 'select' choose 'select_field_properties'.If the field type is 'textarea' choose 'textarea_field_properties'.If the field type is 'boolean' choose 'boolean_field_properties'. If you want buttons use button_field_properties.If the field type is 'image' choose 'image_field_properties'.If the field type is 'file' choose 'file_field_properties'\",\"anyOf\":[{\"$ref\":\"#/$defs/text_field_properties\"},{\"$ref\":\"#/$defs/date_field_properties\"},{\"$ref\":\"#/$defs/number_field_properties\"},{\"$ref\":\"#/$defs/select_field_properties\"},{\"$ref\":\"#/$defs/textarea_field_properties\"},{\"$ref\":\"#/$defs/button_field_properties\"},{\"$ref\":\"#/$defs/boolean_field_properties\"},{\"$ref\":\"#/$defs/image_field_properties\"},{\"$ref\":\"#/$defs/file_field_properties\"}]},\"action_definition\":{\"description\":\"Union of supported action types\",\"anyOf\":[{\"$ref\":\"#/$defs/component_action_definition\"},{\"$ref\":\"#/$defs/api_action_definition\"},{\"$ref\":\"#/$defs/form_valid_action_definition\"},{\"$ref\":\"#/$defs/goto_action_definition\"}]},\"component_action_definition\":{\"type\":\"object\",\"description\":\"Action to open or submit a component\",\"properties\":{\"type\":{\"type\":\"string\",\"enum\":[\"component\"],\"description\":\"Action type identifier\"},\"title\":{\"type\":\"string\",\"description\":\"Action title\"},\"icon\":{\"type\":\"string\",\"description\":\"Icon name\"},\"modalSize\":{\"type\":\"string\",\"description\":\"Modal window size\",\"enum\":[\"lg\",\"sm\",\"md\"]},\"showPopverTitle\":{\"type\":\"boolean\",\"description\":\"Show popover header\"},\"formViewMode\":{\"type\":\"boolean\",\"description\":\"Open form in view-only mode\"},\"componentId\":{\"type\":\"string\",\"description\":\"Target component ID ('definition.id')\"},\"action\":{\"type\":\"string\",\"enum\":[\"reload\",\"reset\",\"submit\",\"update\",\"openModal\",\"closeModal\",\"show\",\"hide\",\"setData\",\"delete\"],\"description\":\"Primary component action (submit)\"},\"actions\":{\"type\":\"array\",\"description\":\"Optional nested actions for further handling\",\"items\":{\"$ref\":\"#/$defs/action_definition\"}}},\"required\":[\"type\",\"title\",\"icon\",\"modalSize\",\"showPopverTitle\",\"formViewMode\",\"componentId\",\"action\",\"actions\"],\"additionalProperties\":false},\"api_action_definition\":{\"type\":\"object\",\"description\":\"Action to invoke an API with response handling\",\"properties\":{\"type\":{\"type\":\"string\",\"enum\":[\"api\"],\"description\":\"Action type identifier\"},\"title\":{\"type\":\"string\",\"description\":\"Action title\"},\"icon\":{\"type\":\"string\",\"description\":\"Icon name\"},\"method\":{\"type\":\"string\",\"enum\":[\"post\",\"get\"],\"description\":\"HTTP method to call\"},\"currentData\":{\"type\":\"boolean\",\"description\":\"Include current form data\"},\"url\":{\"type\":\"string\",\"pattern\":\"^api/([^/]+)/(list|select|delete|save-update)/([^/]+)$\",\"examples\":[\"api/user/list/user-list\"],\"description\":\"Api url, here api structure (api/<<entity/table>>/(list|select|delete|save-update)/<<generate random api code>>), here '<<>>' refered as wildcards you should replace it dynamically. STRICTLY REPLACE THE WILDCARDS.code doesn't start with number. it always be alphabetical.\"},\"actions\":{\"type\":\"array\",\"description\":\"Everytime you should want to generate both 'success' and 'failure' actions\",\"items\":{\"type\":\"object\",\"description\":\"This actions are sub actions for main action\",\"properties\":{\"type\":{\"type\":\"string\",\"enum\":[\"success\",\"failure\"],\"description\":\"Outcome type (success/failure)\"},\"title\":{\"type\":\"string\",\"description\":\"Outcome title\"},\"icon\":{\"type\":\"string\",\"description\":\"Icon name\"},\"clickable\":{\"type\":\"boolean\",\"description\":\"Outcome button clickable\"},\"dragDisabled\":{\"type\":\"boolean\",\"description\":\"Outcome element draggable\"},\"actions\":{\"type\":\"array\",\"description\":\"Optional nested actions for further handling\",\"items\":{\"$ref\":\"#/$defs/action_definition\"}}},\"required\":[\"type\",\"title\",\"icon\",\"clickable\",\"dragDisabled\",\"actions\"],\"additionalProperties\":false}}},\"required\":[\"type\",\"title\",\"icon\",\"method\",\"currentData\",\"url\",\"actions\"],\"additionalProperties\":false},\"form_valid_action_definition\":{\"type\":\"object\",\"description\":\"Action for form validation outcomes\",\"properties\":{\"type\":{\"type\":\"string\",\"enum\":[\"formValid\"],\"description\":\"Action type identifier\"},\"title\":{\"type\":\"string\",\"description\":\"Action title\"},\"componentId\":{\"type\":\"string\",\"description\":\"Tag the id in the definition block, on the selected form\"},\"icon\":{\"type\":\"string\",\"description\":\"Icon name\"},\"actions\":{\"type\":\"array\",\"description\":\"STRICTLY FOLLOW:Every time you should want to generate both 'valid' and 'invalid' actions\",\"items\":{\"type\":\"object\",\"description\":\"This actions are sub actions for main action\",\"properties\":{\"type\":{\"type\":\"string\",\"enum\":[\"valid\",\"invalid\"],\"description\":\"Outcome type (valid/invalid)\"},\"title\":{\"type\":\"string\",\"description\":\"Outcome title\"},\"icon\":{\"type\":\"string\",\"description\":\"Icon name\"},\"clickable\":{\"type\":\"boolean\",\"description\":\"Outcome button clickable\"},\"dragDisabled\":{\"type\":\"boolean\",\"description\":\"Outcome element draggable\"},\"actions\":{\"type\":\"array\",\"description\":\"Optional nested actions for further handling\",\"items\":{\"$ref\":\"#/$defs/action_definition\"}}},\"required\":[\"type\",\"title\",\"icon\",\"clickable\",\"dragDisabled\",\"actions\"],\"additionalProperties\":false}}},\"required\":[\"type\",\"title\",\"icon\",\"actions\",\"componentId\"],\"additionalProperties\":false},\"goto_action_definition\":{\"type\":\"object\",\"description\":\"Action for GOTO Page\",\"properties\":{\"type\":{\"type\":\"string\",\"enum\":[\"page\"],\"description\":\"Action type identifier\"},\"title\":{\"type\":\"string\",\"description\":\"Action title\"},\"icon\":{\"type\":\"string\",\"description\":\"Icon name\"},\"source\":{\"type\":\"string\",\"description\":\"source of the page\",\"enum\":[\"internal\",\"external\"]},\"target\":{\"type\":\"string\",\"description\":\"target for this page has to open weather in new tab or same tab it self\",\"enum\":[\"self\",\"new\"]},\"url\":{\"type\":\"string\",\"description\":\"Destination URL\"},\"actions\":{\"type\":\"array\",\"description\":\"Validation outcome actions\",\"items\":{\"$ref\":\"#/$defs/action_definition\"}}},\"required\":[\"type\",\"title\",\"icon\",\"source\",\"target\",\"url\",\"actions\"],\"additionalProperties\":false},\"text_field_properties\":{\"type\":\"object\",\"description\":\"properties of the field, if minlength and maxlength are '0' don't initial those keys\",\"properties\":{\"name\":{\"type\":\"string\",\"description\":\"Field data key\"},\"type\":{\"type\":\"string\",\"enum\":[\"text\"],\"description\":\"based on the field, choose type wisely\"},\"title\":{\"type\":\"string\",\"description\":\"Label displayed next to the field\"},\"id\":{\"type\":\"string\",\"description\":\"Unique field identifier\"},\"showTitle\":{\"type\":\"boolean\",\"description\":\"Show or hide field title\"},\"properties\":{\"type\":\"object\",\"description\":\"based on type select the suitable properties\",\"properties\":{\"placeholder\":{\"type\":\"string\",\"description\":\"placeholedr message of this field\"},\"required\":{\"type\":\"boolean\",\"description\":\"When the field is required only then make it true\"},\"minLength\":{\"type\":\"number\",\"description\":\"You should think, based on field type, set the minimum length.if it is required only\"},\"maxLength\":{\"type\":\"number\",\"description\":\"You should think, based on field type, set the maximun length.if it is required only\"},\"pattern\":{\"type\":\"string\",\"description\":\"You should think, based on field type, set the suitable pattern.if it is required only\"},\"messages\":{\"type\":\"object\",\"description\":\"properties of the field\",\"properties\":{\"minlength\":{\"type\":\"string\",\"description\":\"Minimum length warning message\"},\"maxlength\":{\"type\":\"string\",\"description\":\"Maximum length warning message\"},\"pattern\":{\"type\":\"string\",\"description\":\"Pattern warning message\"},\"required\":{\"type\":\"string\",\"description\":\"Required warning message\"}},\"required\":[\"minlength\",\"maxlength\",\"pattern\",\"required\"],\"additionalProperties\":false}},\"required\":[\"placeholder\",\"required\",\"minLength\",\"maxLength\",\"pattern\",\"messages\"],\"additionalProperties\":false}},\"required\":[\"name\",\"type\",\"title\",\"id\",\"showTitle\",\"properties\"],\"additionalProperties\":false},\"date_field_properties\":{\"type\":\"object\",\"description\":\"properties of the date field\",\"properties\":{\"name\":{\"type\":\"string\",\"description\":\"Field data key\"},\"type\":{\"type\":\"string\",\"enum\":[\"date\",\"datetime\",\"daterange\",\"time\"],\"description\":\"based on the field, choose type wisely\"},\"title\":{\"type\":\"string\",\"description\":\"Label displayed next to the field\"},\"id\":{\"type\":\"string\",\"description\":\"Unique field identifier\"},\"showTitle\":{\"type\":\"boolean\",\"description\":\"Show or hide field title\"},\"properties\":{\"type\":\"object\",\"description\":\"based on type select the suitable properties\",\"properties\":{\"placeholder\":{\"type\":\"string\",\"description\":\"placeholder message of this field\"},\"dateFormat\":{\"type\":\"string\",\"description\":\"Format for date display and input\"},\"selectionMode\":{\"type\":\"string\",\"enum\":[\"single\",\"multiple\",\"range\"],\"description\":\"Date selection mode\"},\"required\":{\"type\":\"boolean\",\"description\":\"When the field is required only then make it true\"},\"placement\":{\"type\":\"string\",\"enum\":[\"top\",\"bottom\",\"left\",\"right\"],\"description\":\"Placement of date picker\"},\"minMode\":{\"type\":\"string\",\"enum\":[\"day\",\"month\",\"year\"],\"description\":\"Minimum selection mode for date picker\"},\"maxDate\":{\"type\":\"string\",\"description\":\"Maximum selectable date (can be 'current_date' or specific date)\"},\"displayFormat\":{\"type\":\"string\",\"description\":\"HTML template for displaying formatted date with additional calculations\"},\"messages\":{\"type\":\"object\",\"description\":\"validation messages for the field\",\"properties\":{\"required\":{\"type\":\"string\",\"description\":\"Required validation message\"}},\"required\":[\"required\"],\"additionalProperties\":false}},\"required\":[\"placeholder\",\"dateFormat\",\"selectionMode\",\"required\",\"placement\",\"minMode\",\"maxDate\",\"displayFormat\",\"messages\"],\"additionalProperties\":false}},\"required\":[\"name\",\"type\",\"title\",\"showTitle\",\"id\",\"properties\"],\"additionalProperties\":false},\"number_field_properties\":{\"type\":\"object\",\"description\":\"properties of the number field\",\"properties\":{\"name\":{\"type\":\"string\",\"description\":\"Field data key\"},\"type\":{\"type\":\"string\",\"enum\":[\"number\"],\"description\":\"based on the field, choose type wisely\"},\"label\":{\"type\":\"string\",\"description\":\"Label displayed for the field\"},\"title\":{\"type\":\"string\",\"description\":\"Title displayed next to the field\"},\"id\":{\"type\":\"string\",\"description\":\"Unique field identifier\"},\"showTitle\":{\"type\":\"boolean\",\"description\":\"Show or hide field title\"},\"properties\":{\"type\":\"object\",\"description\":\"based on type select the suitable properties\",\"properties\":{\"placeholder\":{\"type\":\"string\",\"description\":\"placeholder message of this field\"},\"required\":{\"type\":\"boolean\",\"description\":\"When the field is required only then make it true\"},\"minLength\":{\"type\":\"string\",\"description\":\"You should think, based on field type, set the minimum length\"},\"maxLength\":{\"type\":\"string\",\"description\":\"You should think, based on field type, set the maximum length\"},\"inlineStyle\":{\"type\":\"object\",\"description\":\"Inline styling properties for the field\",\"properties\":{\"borderPosition\":{\"type\":\"string\",\"enum\":[\"border-left\",\"border-right\",\"border-top\",\"border-bottom\"],\"description\":\"Border position styling\"}},\"required\":[\"borderPosition\"],\"additionalProperties\":false},\"messages\":{\"type\":\"object\",\"description\":\"validation messages for the field\",\"properties\":{\"minlength\":{\"type\":\"string\",\"description\":\"Minimum length warning message\"},\"maxlength\":{\"type\":\"string\",\"description\":\"Maximum length warning message\"},\"required\":{\"type\":\"string\",\"description\":\"Required warning message\"}},\"required\":[\"required\",\"minlength\",\"maxlength\"],\"additionalProperties\":false}},\"required\":[\"placeholder\",\"required\",\"minLength\",\"maxLength\",\"validation\",\"inlineStyle\",\"messages\"],\"additionalProperties\":false}},\"required\":[\"name\",\"type\",\"label\",\"title\",\"showTitle\",\"id\",\"properties\"],\"additionalProperties\":false},\"select_field_properties\":{\"type\":\"object\",\"description\":\"properties of the select field\",\"properties\":{\"name\":{\"type\":\"string\",\"description\":\"Field data key\"},\"type\":{\"type\":\"string\",\"enum\":[\"select\"],\"description\":\"based on the field, choose type wisely\"},\"label\":{\"type\":\"string\",\"description\":\"Label displayed for the field\"},\"title\":{\"type\":\"string\",\"description\":\"Title displayed next to the field\"},\"id\":{\"type\":\"string\",\"description\":\"Unique field identifier\"},\"showTitle\":{\"type\":\"boolean\",\"description\":\"Show or hide field title\"},\"properties\":{\"type\":\"object\",\"description\":\"based on type select the suitable properties\",\"properties\":{\"displayStyle\":{\"type\":\"string\",\"enum\":[\"select\"],\"description\":\"Visual display style for the select field\"},\"placeholder\":{\"type\":\"string\",\"description\":\"placeholder message for the select field\"},\"required\":{\"type\":\"boolean\",\"description\":\"When the field is required only then make it true\"},\"appendToBody\":{\"type\":\"boolean\",\"description\":\"Append dropdown to document body instead of parent element\"},\"search\":{\"type\":\"boolean\",\"description\":\"Enable search/filter functionality in dropdown\"},\"mode\":{\"type\":\"string\",\"enum\":[\"view\",\"edit\"],\"description\":\"Field interaction mode\"},\"inlineStyle\":{\"type\":\"object\",\"description\":\"Inline styling properties for the field\",\"properties\":{\"borderPosition\":{\"type\":\"string\",\"enum\":[\"border-left\",\"border-right\",\"border-top\",\"border-bottom\"],\"description\":\"Border position styling\"}},\"required\":[\"borderPosition\"],\"additionalProperties\":false},\"messages\":{\"type\":\"object\",\"description\":\"validation messages for the field\",\"properties\":{\"required\":{\"type\":\"string\",\"description\":\"Required warning message\"}},\"required\":[\"required\"],\"additionalProperties\":false}},\"required\":[\"displayStyle\",\"placeholder\",\"required\",\"appendToBody\",\"search\",\"mode\",\"inlineStyle\",\"messages\"],\"additionalProperties\":false},\"dataSource\":{\"type\":\"object\",\"description\":\"Data source configuration for dynamic options\",\"properties\":{\"type\":{\"type\":\"string\",\"enum\":[\"api\"],\"description\":\"Type of data source\"},\"api\":{\"type\":\"object\",\"description\":\"API configuration for fetching options\",\"properties\":{\"method\":{\"type\":\"string\",\"enum\":[\"get\"],\"description\":\"HTTP method for API call\"},\"url\":{\"type\":\"string\",\"description\":\"API endpoint URL. If field type relational : example: api/<<dataSource>>/(list|save-update|delete|select)/<<api-code>>, If field type choose example: api/choose-data/<<picklist-code>>\"},\"map\":{\"type\":\"object\",\"description\":\"Field mapping for API response\",\"properties\":{\"label\":{\"type\":\"string\",\"description\":\"Field name in API response to map to label\"},\"value\":{\"type\":\"string\",\"enum\":[\"uid\"],\"description\":\"Field name in API response to map to value\"}},\"required\":[\"label\",\"value\"],\"additionalProperties\":false}},\"required\":[\"method\",\"url\",\"map\"],\"additionalProperties\":false}},\"required\":[\"type\",\"api\"],\"additionalProperties\":false}},\"required\":[\"name\",\"type\",\"title\",\"showTitle\",\"id\",\"label\",\"properties\",\"dataSource\"],\"additionalProperties\":false},\"textarea_field_properties\":{\"type\":\"object\",\"description\":\"properties of the textarea field\",\"properties\":{\"name\":{\"type\":\"string\",\"description\":\"Field data key\"},\"type\":{\"type\":\"string\",\"enum\":[\"textarea\"],\"description\":\"based on the field, choose type wisely\"},\"title\":{\"type\":\"string\",\"description\":\"Label displayed next to the field\"},\"id\":{\"type\":\"string\",\"description\":\"Unique field identifier\"},\"showTitle\":{\"type\":\"boolean\",\"description\":\"Show or hide field title\"},\"properties\":{\"type\":\"object\",\"description\":\"based on type select the suitable properties\",\"properties\":{\"placeholder\":{\"type\":\"string\",\"description\":\"placeholder message of this field\"},\"required\":{\"type\":\"boolean\",\"description\":\"When the field is required only then make it true\"},\"minLength\":{\"type\":\"number\",\"description\":\"You should think, based on field type, set the minimum length\"},\"maxLength\":{\"type\":\"number\",\"description\":\"You should think, based on field type, set the maximum length\"},\"displayStyle\":{\"type\":\"string\",\"enum\":[\"htmleditor\",\"plain\",\"rich\"],\"description\":\"Display style for textarea (htmleditor, plain, or rich text)\"},\"rows\":{\"type\":\"string\",\"description\":\"Number of visible text lines for textarea\"},\"cols\":{\"type\":\"string\",\"description\":\"Visible width of textarea in characters\"}},\"required\":[\"placeholder\",\"required\",\"minLength\",\"maxLength\",\"cols\",\"displayStyle\",\"rows\"],\"additionalProperties\":false}},\"required\":[\"name\",\"type\",\"title\",\"showTitle\",\"id\",\"properties\"],\"additionalProperties\":false},\"button_field_properties\":{\"type\":\"object\",\"description\":\"If the type is button\",\"properties\":{\"name\":{\"type\":\"string\",\"description\":\"Field data key\"},\"type\":{\"type\":\"string\",\"enum\":[\"button\"],\"description\":\"based on the field, choose type wisely\"},\"title\":{\"type\":\"string\",\"description\":\"Label displayed next to the field\"},\"id\":{\"type\":\"string\",\"description\":\"Unique field identifier\"},\"showTitle\":{\"type\":\"boolean\",\"description\":\"Show or hide field title\"},\"properties\":{\"type\":\"object\",\"description\":\"based on type select the suitable properties\",\"properties\":{\"web\":{\"type\":\"boolean\",\"description\":\"based on mobile view\"},\"mobile\":{\"type\":\"boolean\",\"description\":\"based on mobile view\"}},\"required\":[\"web\",\"mobile\"],\"additionalProperties\":false},\"triggers\":{\"type\":\"array\",\"description\":\"Event triggers for button/formValid fields\",\"items\":{\"type\":\"object\",\"description\":\"Trigger event configuration\",\"properties\":{\"type\":{\"type\":\"string\",\"description\":\"Trigger event type (click, ...)\"},\"actions\":{\"type\":\"array\",\"description\":\"Actions executed when trigger fires\",\"items\":{\"$ref\":\"#/$defs/action_definition\"}}},\"required\":[\"type\",\"actions\"],\"additionalProperties\":false}}},\"required\":[\"name\",\"type\",\"title\",\"id\",\"showTitle\",\"properties\",\"triggers\"],\"additionalProperties\":false},\"file_field_properties\":{\"type\":\"object\",\"description\":\"If the field type is attachmenet\",\"properties\":{\"name\":{\"type\":\"string\",\"description\":\"Field data key\"},\"type\":{\"type\":\"string\",\"enum\":[\"file\"],\"description\":\"based on the field, choose type wisely\"},\"title\":{\"type\":\"string\",\"description\":\"Label displayed next to the field\"},\"id\":{\"type\":\"string\",\"description\":\"Unique field identifier\"},\"showTitle\":{\"type\":\"boolean\",\"description\":\"Show or hide field title\"},\"properties\":{\"type\":\"object\",\"description\":\"based on type select the suitable properties\",\"properties\":{\"required\":{\"type\":\"boolean\",\"description\":\"When the field is required only then make it true\"},\"multiple\":{\"type\":\"boolean\",\"description\":\"Allow multiple file selection\"},\"allowedTypes\":{\"type\":\"array\",\"description\":\"Array of allowed file extensions\",\"items\":{\"type\":\"string\",\"description\":\"File extension (e.g., 'jpg', 'png', 'pdf')\"}},\"maxSize\":{\"type\":\"number\",\"description\":\"Maximum file size in KB\"},\"minSize\":{\"type\":\"number\",\"description\":\"Minimum file size in KB\"},\"minLength\":{\"type\":\"number\",\"description\":\"Minimum number of files required when multiple is true\"},\"selectionMode\":{\"type\":\"string\",\"enum\":[\"single\",\"multiple\"],\"description\":\"File selection mode\"},\"delConfirm\":{\"type\":\"boolean\",\"description\":\"Show confirmation dialog before deleting files\"},\"delConfirmMsg\":{\"type\":\"string\",\"description\":\"Custom message for delete confirmation dialog\"},\"messages\":{\"type\":\"object\",\"description\":\"validation messages for the field\",\"properties\":{\"required\":{\"type\":\"string\",\"description\":\"Required field warning message\"},\"minlength\":{\"type\":\"string\",\"description\":\"Minimum files count warning message\"},\"maxSize\":{\"type\":\"string\",\"description\":\"Maximum file size warning message\"},\"minSize\":{\"type\":\"string\",\"description\":\"Minimum file size warning message\"},\"allowedTypes\":{\"type\":\"string\",\"description\":\"Invalid file type warning message\"}},\"required\":[\"required\",\"minlength\",\"maxSize\",\"minSize\",\"allowedTypes\"],\"additionalProperties\":false}},\"required\":[\"required\",\"multiple\",\"allowedTypes\",\"maxSize\",\"minSize\",\"minLength\",\"selectionMode\",\"delConfirm\",\"delConfirmMsg\",\"messages\"],\"additionalProperties\":false}},\"required\":[\"name\",\"type\",\"title\",\"showTitle\",\"id\",\"properties\"],\"additionalProperties\":false},\"image_field_properties\":{\"type\":\"object\",\"description\":\"If the field type is image\",\"properties\":{\"name\":{\"type\":\"string\",\"description\":\"Field data key\"},\"type\":{\"type\":\"string\",\"enum\":[\"image\"],\"description\":\"based on the field, choose type wisely\"},\"label\":{\"type\":\"string\",\"description\":\"Label displayed for the field\"},\"id\":{\"type\":\"string\",\"description\":\"Unique field identifier\"},\"showTitle\":{\"type\":\"boolean\",\"description\":\"Show or hide field title\"},\"properties\":{\"type\":\"object\",\"description\":\"based on type select the suitable properties\",\"properties\":{\"multiple\":{\"type\":\"boolean\",\"description\":\"Allow multiple file selection\"},\"placeholder\":{\"type\":\"string\",\"description\":\"placeholder message for file upload area\"},\"required\":{\"type\":\"boolean\",\"description\":\"When the field is required only then make it true\"},\"allowedTypes\":{\"type\":\"array\",\"description\":\"Array of allowed file extensions\",\"items\":{\"type\":\"string\",\"enum\":[\"jpg\",\"jpeg\",\"png\",\"gif\",\"bmp\",\"webp\",\"svg\"]}},\"maxSize\":{\"type\":\"number\",\"description\":\"Maximum file size in KB\"},\"minSize\":{\"type\":\"number\",\"description\":\"Minimum file size in KB\"},\"minFiles\":{\"type\":\"number\",\"description\":\"Minimum number of files required\"},\"maxFiles\":{\"type\":\"number\",\"description\":\"Maximum number of files allowed\"},\"dimensions\":{\"type\":\"string\",\"description\":\"Image dimensions constraint (width_height format)\"},\"mode\":{\"type\":\"string\",\"description\":\"Field mode expression (view/edit based on conditions)\"},\"softDeleteConfirmationMsg\":{\"type\":\"boolean\",\"description\":\"Enable soft delete confirmation dialog\"},\"softDelConfirmMsg\":{\"type\":\"string\",\"description\":\"Soft delete confirmation message text\"},\"hardDeleteConfirmationMsg\":{\"type\":\"boolean\",\"description\":\"Enable hard delete confirmation dialog\"},\"hardDelConfirmMsg\":{\"type\":\"string\",\"description\":\"Hard delete confirmation message text\"},\"delConfirm\":{\"type\":\"boolean\",\"description\":\"Enable general delete confirmation\"},\"delConfirmMsg\":{\"type\":\"string\",\"description\":\"General delete confirmation message text\"},\"messages\":{\"type\":\"object\",\"description\":\"validation messages for the field\",\"properties\":{\"required\":{\"type\":\"string\",\"description\":\"Required warning message\"},\"fileType\":{\"type\":\"string\",\"description\":\"Invalid file type message\"},\"fileSize\":{\"type\":\"string\",\"description\":\"File size limit exceeded message\"},\"maxFiles\":{\"type\":\"string\",\"description\":\"Maximum files limit exceeded message\"}},\"required\":[\"required\",\"fileType\",\"fileSize\",\"maxFiles\"],\"additionalProperties\":false}},\"required\":[\"multiple\",\"placeholder\",\"required\",\"maxSize\",\"minSize\",\"minFiles\",\"maxFiles\",\"mode\",\"softDeleteConfirmationMsg\",\"softDelConfirmMsg\",\"hardDeleteConfirmationMsg\",\"hardDelConfirmMsg\",\"delConfirm\",\"dimensions\",\"delConfirmMsg\",\"allowedTypes\",\"messages\"],\"additionalProperties\":false}},\"required\":[\"name\",\"type\",\"label\",\"showTitle\",\"id\",\"properties\"],\"additionalProperties\":false},\"boolean_field_properties\":{\"type\":\"object\",\"description\":\"If the field type is boolean\",\"properties\":{\"name\":{\"type\":\"string\",\"description\":\"Field data key\"},\"type\":{\"type\":\"string\",\"enum\":[\"boolean\"],\"description\":\"based on the field, choose type wisely\"},\"title\":{\"type\":\"string\",\"description\":\"Label displayed for the field\"},\"id\":{\"type\":\"string\",\"description\":\"Unique field identifier\"},\"showTitle\":{\"type\":\"boolean\",\"description\":\"Show or hide field title\"},\"properties\":{\"type\":\"object\",\"description\":\"based on type select the suitable properties\",\"properties\":{\"displayStyle\":{\"type\":\"string\",\"enum\":[\"boolean\"],\"description\":\"Display style of the field\"},\"required\":{\"type\":\"boolean\",\"description\":\"When the field is required only then make it true\"}},\"required\":[\"displayStyle\",\"required\"],\"additionalProperties\":false}},\"required\":[\"name\",\"type\",\"title\",\"showTitle\",\"id\",\"properties\"],\"additionalProperties\":false}}}";
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String, Object> map;
//        List<String> list = Arrays.asList("sample", "input", "data");
//		try {
//			map = objectMapper.readValue(json, Map.class);
//			System.out.println(convertMapToMarkdown(map));
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//		System.out.println(convertWordToNumber("eight thousand six hundred twenty three"));
//		Map<String, Object> map = Map.of("name", "tejaswini", "surname", "shetty", "studies", "b-tech", "age", "23");
//		Map<String, Object> metadata = Map.of("name", Map.of("is_ignore", true), "age", Map.of("is_ignore", true));
//		System.out.println(getRequiredKeyValues(map, metadata));
//        String jsonOutput = convertExcelToJson(FILE_PATH);
//        System.out.println(jsonOutput);
    }	
}
