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
import java.text.SimpleDateFormat;
import java.util.Date;


public class WormholeUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String STORAGE_DIR = "wormhole";
	private static final String THRESHHOLD_TEMP_DIR = System.getProperty("java.io.tmpdir");


    public WormholeUpload() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Check to see if the server post request to this servlet is a multipart upload
		if (!ServletFileUpload.isMultipartContent(request)){
			return;
		}
		

		DiskFileItemFactory fileFactory = new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, new File(THRESHHOLD_TEMP_DIR));
		ServletFileUpload fileUpload = new ServletFileUpload(fileFactory);
		PrintWriter printWriter = response.getWriter();
		String servletPath = getServletContext().getRealPath("") + File.separator + STORAGE_DIR + File.separator + getUsername(request.getContentType());
		
		File baseStorageDir = new File(servletPath);
		if (!baseStorageDir.exists()){
			baseStorageDir.mkdirs();
		}
		
		try {
			List items = fileUpload.parseRequest(request);
			Iterator itemIterator = items.iterator();
		
			while(itemIterator.hasNext()){
				FileItem uploadItem = (FileItem) itemIterator.next();
				if (uploadItem.isFormField()){
					continue;
				}
				
				String fileName = new File(uploadItem.getName()).getName();
                String filePath = servletPath + File.separator + fileName;
                File storeFile = new File(filePath);
                System.out.println(filePath);
                uploadItem.write(storeFile);
                printWriter.print("File upload sucessful...\n");
                printWriter.print("Name: " + uploadItem.getName() + "\n");
                printWriter.print("Size: " + uploadItem.getSize() + " Bytes\n");
                printWriter.print("Date: " + new SimpleDateFormat("dd-MM-YYYY").format(new Date()) + "\n");
			}
		} catch (Exception e) {
			printWriter.print("File upload failure.");
		}	
	}
	
	private String getUsername(String boundary){
		String username = boundary.substring(boundary.indexOf("boundary=") + "boundary=".length());
		return username.substring(0, username.indexOf('='));
	}
	

}
