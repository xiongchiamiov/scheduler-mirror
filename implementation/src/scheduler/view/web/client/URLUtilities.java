package scheduler.view.web.client;

import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.user.client.Window;

public class URLUtilities {

	static Map<String, String> parseURLArguments(String url) {
		url = url.substring(url.indexOf('?') + 1);

		Map<String, String> result = new TreeMap<String, String>();
		
		String[] paramsAndArgsStrings = url.split("[&|\\?]");
		for (String paramAndArgString : paramsAndArgsStrings) {
			String[] paramAndArg = paramAndArgString.split("=");
			assert(paramAndArg.length == 2);
			result.put(paramAndArg[0], paramAndArg[1]);
		}
		
		return result;
	}
	
	static String getBaseURL() {
		String url = Window.Location.getHref();
		Map<String, String> arguments = parseURLArguments(url);
		url = url.substring(0, url.indexOf('?'));
		if (arguments.containsKey("gwt.codesvr"))
			url += "?gwt.codesvr=" + arguments.get("gwt.codesvr");
		return url;
	}
	
	static String appendArgumentToURL(String url, String parameter, String argument) {
		if (url.contains("?"))
			url += "&";
		else
			url += "?";
		url += parameter + "=" + argument;
		return url;
	}
	
}
