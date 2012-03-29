package scheduler.view.web.server;

import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class CSVDownload extends HttpServlet {
	
	private static HashMap<Integer, String> map = new HashMap<Integer, String>();
	private static int counter = 0;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response){
		
		OutputStream out = null;
		
		// Set content type
        response.setContentType("text/csv");
        
        // Set the file to be downloaded
        response.setHeader ("Content-Disposition", 
        		"attachment; filename=\"" + "schedule.csv" + "\"");
		
        // get the contents from the map
        String keyStr = request.getParameter("param");
        
        String contents = "";
        try{
        	
        	int key = Integer.parseInt(keyStr.trim());
        	contents = map.remove(key);
        	if(contents == null){ contents = ""; }
        	
        }catch(Exception e){}
        
		// write stream to a byte array
		byte[] byteArr = contents.getBytes();
		
		// Set content size
        response.setContentLength(contents.length());
        
		try{
            // Open the file and output streams
            out = response.getOutputStream();

            // Copy the contents to the output stream
            out.write(byteArr);

		}catch(Exception e){
			// Log.print(e);
		}finally{
			try{
				out.close();
			}catch(Exception e){}
		}
	}
	
	
	/**
	 * Save xml to the map and return confirmation code
	 * @param xml
	 * @return
	 */
	public static int save(String xml){
		
		// get counter
		int i = counter;
		
		// increment counter
		if(counter >= Integer.MAX_VALUE){
			counter = 0;
		}
		else{
			counter++;
		}
		
		// put settings xml into the map
		map.put(i, xml);
		
		return i;
	}
}
