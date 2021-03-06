package scheduler.view.web.client.views.resources.courses;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.smartgwt.client.widgets.grid.EditorValueMapFunction;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

public class PossibleDayPatternsFunction implements EditorValueMapFunction {
	@SuppressWarnings("rawtypes")
	// Thats how the interface defines them, without generic types
	public Map getEditorValueMap(Map values, ListGridField field, ListGrid grid) {
		if (values == null)
			return new HashMap<String, String>();

		String type = (String) values.get("type");
		assert (type != null);

		String scuString = (String) values.get("scu");
		assert (scuString != null);

		return getValues(type, scuString);
	}

	static Map<String, String> getValues(String type, String scuString) {

		boolean canTether = "LAB".equals(type) || "ACT".equals(type)
				|| "DIS".equals(type);

		Map<String, String> result = new LinkedHashMap<String, String>();

		if (canTether) {
			result.put("MWF", "MWF");
			result.put("MW", "MW");
			result.put("TR", "TR");
			result.put("M", "M");
			result.put("T", "T");
			result.put("W", "W");
			result.put("R", "R");
			result.put("F", "F");
		} else {
			Integer scu = null;
			try {
				scu = Integer.parseInt(scuString);
			} catch (NumberFormatException e) {
				return new HashMap<String, String>();
			}

			switch (scu) {
			case 0:
				break;
			case 1:
				result.put("M", "M");
				result.put("T", "T");
				result.put("W", "W");
				result.put("R", "R");
				result.put("F", "F");
				break;
			case 2:
				result.put("MW", "MW");
				result.put("WF", "WF");
				result.put("MF", "MF");
				result.put("TR", "TR");
				break;
			case 3:
				result.put("MWF", "MWF");
				result.put("MW", "MW");
				result.put("TR", "TR");
				break;
			case 4:
				result.put("TR", "TR");
				result.put("MW", "MW");
				result.put("MF", "MF");
				result.put("MTWR", "MTWR");
				result.put("MTRF", "MTRF");
				result.put("MWRF", "MWRF");
				result.put("TWRF", "TWRF");
				break;
			case 5:
				result.put("MTWRF", "MTWRF");
				break;
			default:
				break;
			}
		}

		return result;
	}
}