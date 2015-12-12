package wormhole;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class WormholeDownload extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	//File download
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Check if multipart request. If not, fail out.
		if (!ServletFileUpload.isMultipartContent(request)){
			return;
		}
		
		//Attain filepath from URL username and filename.
		String filepath = getServletContext().getRealPath("") + File.separator + "wormhole" + File.separator +
						WormholeUtils.parseOption(request.getContentType(), "username=") + File.separator + 
						WormholeUtils.parseOption(request.getContentType(), "filename=");
		File file = new File(filepath);
		
		//Open up file stream from the filepath.
		FileInputStream iStream = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		
		//Open up response output stream and write to the user in bytes.
		OutputStream oStream = response.getOutputStream();
		while ((bytesRead = iStream.read(buffer)) != -1){
			oStream.write(buffer, 0, bytesRead);
		}
		iStream.close();
		oStream.flush();
		oStream.close();
	}
	
	//Files available in user account query.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Check if valid username in URL
		String username = request.getParameter("username");
		if (username == null){
			return;
		}
		
		//If the folder doesn't exist, make it.
		//Design decision to avoid having a setup-process in the user-registration step.
		File folder = new File(getServletContext().getRealPath("") + File.separator + "wormhole" + File.separator + username);
		if (!folder.exists()){
			folder.mkdirs();
		}
		
		//Pull the file name from the folder
		File[] files = folder.listFiles();
		
		//Get the response printer and print files to user deliniated by the "//" option seperator.
		PrintWriter printWriter = response.getWriter();
		try {
			for (int i = 0; i < files.length; ++i){
				if (files[i].isFile()){
					printWriter.print(files[i].getName() + "//");
				}
			}
			printWriter.flush();
			printWriter.close();
			
		} catch (Exception e) {
			System.out.println(e); //Unexpected error.
		}
	}
}

