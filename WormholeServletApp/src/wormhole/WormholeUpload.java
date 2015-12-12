package wormhole;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.io.File;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;


public class WormholeUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//Temporary directory for storing intermediary buffer.
	private static final String THRESHHOLD_TEMP_DIR = System.getProperty("java.io.tmpdir");

    //File deletion
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	//Validate input parameters required for this function. Pulled from URL.
    	String username = request.getParameter("username");
    	String filename = request.getParameter("filename");
    	if (username == null || filename == null){
    		return;
    	}
    	
    	//Get the writer for writing to the response.
    	PrintWriter writer = response.getWriter();
    	
    	//Get the file specified by parameters.
    	File file = new File(getServletContext().getRealPath("") + File.separator + "wormhole" + File.separator +
    				username + File.separator + filename);
    	
    	// File delete.
    	if (file.delete()){
    		writer.write("File deleted successfully.");
    	} else {
    		writer.write("File deletion failed.");
    	}
    	writer.flush();
    	writer.close();	
    }
    
    
    // File upload.
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Check to see if the server post request to this servlet is a multipart upload
		if (!ServletFileUpload.isMultipartContent(request)){
			return;
		}
		
		//Create file factory and servletFileuploader.
		DiskFileItemFactory fileFactory = new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD,
										new File(THRESHHOLD_TEMP_DIR));
		ServletFileUpload fileUpload = new ServletFileUpload(fileFactory);
		
		// Get response writer for responding to request.
		PrintWriter printWriter = response.getWriter();
		
		// Get the path to the storage directory.
		String servletPath = getServletContext().getRealPath("") + File.separator + "wormhole" + File.separator +
				WormholeUtils.parseOption(request.getContentType(), "username=");
		
		//Create directory if doesn't exist.
		File baseStorageDir = new File(servletPath);
		if (!baseStorageDir.exists()){
			baseStorageDir.mkdirs();
		}
		
		try {
			//Begin request parse.
			List items = fileUpload.parseRequest(request);
			Iterator itemIterator = items.iterator();
			
			//Consume bytes.
			while(itemIterator.hasNext()){
				FileItem uploadItem = (FileItem) itemIterator.next();
				if (uploadItem.isFormField()){
					continue;
				}
				//Write bytes out to disk.
				String fileName = new File(uploadItem.getName()).getName();
                String filePath = servletPath + File.separator + fileName;
                File storeFile = new File(filePath);
                uploadItem.write(storeFile);
                System.out.println("File Uploaded: " + filePath);
                printWriter.flush();
                printWriter.close();
			}
			//Send successful response to user.
			printWriter.print("File upload successful.");
		} catch (Exception e) {
			//Send failure response.
			printWriter.print("File upload failure.");
		}	
	}
}
