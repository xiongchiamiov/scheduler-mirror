package edu.calpoly.csc.scheduler.view.web.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.gwt.user.server.rpc.AbstractRemoteServiceServlet;

@SuppressWarnings("serial")
public class UploadService extends AbstractRemoteServiceServlet {
	
	@Override
	public void processPost(HttpServletRequest request, HttpServletResponse response){
		
    	ServletFileUpload upload = new ServletFileUpload();

        try{
            FileItemIterator iter = upload.getItemIterator(request);

            while(iter.hasNext()) {
            	
            	FileItemStream item = iter.next();
            	
            	String contents = null;
        		InputStream in = null;
        		OutputStream out = null;
        		
        		try{
        			
        			// get input stream
        			in = item.openStream();
        			
        			// write stream to a byte array
        			byte[] byteArr = ByteStreams.toByteArray(in);
        			
        			// create a new string from the byte array
        			contents = new String(byteArr, Charset.forName("US-ASCII"));

        			// Set content type
                    response.setContentType("text/html");

                    // Set content size
                    response.setContentLength(contents.length());

                    // Open the file and output streams
                    out = response.getOutputStream();

                    // Copy the contents to the output stream
                    out.write(byteArr);
 	
        		}catch(Exception e){
        			// Log.print(e);
        		}finally{
        			Closeables.closeQuietly(in);
        			Closeables.closeQuietly(out);
        		}
            }
        }
        catch(Exception e){
            e.printStackTrace();
        } 
	}
}
