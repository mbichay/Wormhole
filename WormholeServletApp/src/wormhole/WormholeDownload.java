package wormhole;

import java.io.File;
import java.io.IOException;
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
}
