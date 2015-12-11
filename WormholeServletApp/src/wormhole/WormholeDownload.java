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

public class WormholeDownload extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public WormholeDownload() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println(getUsername(request.getContentType()));
		
		String filepath = getServletContext().getRealPath("") + File.separator + "wormhole" + File.separator + "Matt2" + File.separator + getUsername(request.getContentType());
		File file = new File(filepath);
		
		FileInputStream iStream = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		long totalBytes = 0;
		long fileSize = file.length();
		
		OutputStream oStream = response.getOutputStream();
		
		while ((bytesRead = iStream.read(buffer)) != -1){
			oStream.write(buffer, 0, bytesRead);
			totalBytes += bytesRead;
			System.out.println("Bytes Uploaded: " + totalBytes);
		}
		iStream.close();
		oStream.flush();
		oStream.close();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		if (username == null){
			System.out.println("Please input username.");
			return;
		}
		
		File folder = new File(getServletContext().getRealPath("") + File.separator + "wormhole" + File.separator + username);
		if (!folder.exists()){
			folder.mkdirs();
		}
		
		File[] files = folder.listFiles();
		
		PrintWriter printWriter = response.getWriter();
		
		try {
			for (int i = 0; i < files.length; ++i){
				if (files[i].isFile()){
					printWriter.print(i+". " + files[i].getName() + "//");
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	
	private String getUsername(String boundary){
		String username = boundary.substring(boundary.indexOf("boundary=") + "boundary=".length());
		return username.substring(0, username.indexOf('='));
	}
}

