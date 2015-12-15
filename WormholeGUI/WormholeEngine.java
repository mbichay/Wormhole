import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class WormholeEngine{

	Wormhole view;
	private String username, servletURL, mysqlURL, outputFilepath;
	private static final String CHAR_SET = "UTF-8"; // Charset
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String USER = "root";		//	for MYSQL DB
	private static final String PASS = "team11";	//	for MYSQL DB
	private HttpURLConnection httpConnUpload, httpConnDownload, httpConnQuery, httpConnDelete;
	private URL uploadURL, downloadURL, queryURL, deleteURL;
	private static final String UPLOAD = "/WormholeUpload";
	private static final String DOWNLOAD = "/WormholeDownload";
	private static String NEXT_LINE = "\r\n";
	private static String OPT_SEP = "//"; // Option seperator.
	private static int BYTE_CHUNK = 4096; // arbitrary byte chunk size.



	public WormholeEngine(Wormhole wormholeView, String config){
		view = wormholeView; // Hook into the view.
		validateConfig(config); // Validation step
		init(); // Initialize all buttons.
	}

	private void validateConfig(String config){

		// Config File Parse
		try{
			try(BufferedReader bufferedReader = new BufferedReader(new FileReader(config))){
				String line;
				String opt;
				while ((line = bufferedReader.readLine()) !=null){
					opt = line.substring(0,line.indexOf('='));
					line = line.substring(line.indexOf('=')+1, line.length());
					if (opt.toLowerCase().equals("servleturl")){
						servletURL = line;
					} else if (opt.toLowerCase().equals("mysqlurl")){
						mysqlURL = line;
					} else if (opt.toLowerCase().equals("outputfilepath")){
						outputFilepath = line;
					}
				}
			} catch (Exception e){
				throw e;
			}
		} catch (Exception e){
			view.lockdown(true, "Config file not found.");
			return;
		} // End Config File Parse

		// Begin URL validation
		try {
			uploadURL = new URL(servletURL + UPLOAD);
			downloadURL = new URL(servletURL + DOWNLOAD);
			httpConnUpload = (HttpURLConnection)uploadURL.openConnection();
			httpConnDownload = (HttpURLConnection)downloadURL.openConnection();
			//System.out.println("uploadURL: " + uploadURL + "\ndownloadURL: " + downloadURL);
			if (httpConnUpload.getResponseCode() == 404 ||
				httpConnDownload.getResponseCode() == 404){
				throw new Exception();
			}
			httpConnUpload.disconnect();
			httpConnDownload.disconnect();
		} catch (Exception e){
			view.lockdown(true, "Servlet URL invalid.");
			System.out.println(e.toString());
			return;
		} // End URL validation

		// Begin mySQL URL validation
		
		try	{
			Class.forName(JDBC_DRIVER);
			Connection con = DriverManager.getConnection(mysqlURL,USER,PASS);
		}	catch(Exception e)	{
			System.out.println("Cannot connect to DB: " + e.toString());
		}
		
		// End mySQL URL validation

		// Begin output path validation
		if (!outputFilepath.equals("")){
			if (outputFilepath.charAt(outputFilepath.length()-1) != '/'){
				outputFilepath += "/";
			}
			File outDir = new File(outputFilepath);
			if (!outDir.exists()){
				outDir.mkdirs();
			}
		} // End output path validation
	}




	private void init(){
		//Hook listeners to the view.
		view.setActionListener('l', new doLoginListener());
		view.setActionListener('r', new doRegisterListener());
		view.setActionListener('u', new doUploadListener());
		view.setActionListener('d', new doDownloadListener());
		view.setActionListener('D', new doDeleteListener());
	}

	// Query for files in the user's account.
	private void doQuery(){

		//null queryURL check, only need to build this URL once.
		if (queryURL == null){
			try{
				queryURL = new URL(downloadURL.toString() + "?username=" + username);
			} catch (Exception e){ System.out.println(e); return; } // Handled in validation step.
		}

		try { // Begin HTTP Get-Request
			httpConnQuery = (HttpURLConnection)queryURL.openConnection();
			httpConnQuery.setRequestMethod("GET");
			httpConnQuery.setRequestProperty("Accept-Charset", CHAR_SET);
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnQuery.getInputStream()));

			//Fires GET-REQUEST
			httpConnQuery.getResponseCode();

			//Waits for response from the server, receives a string deliniated by "//"s.
			//After the response has been fully consumed, splits the string into an array of
			//strings and passes it over to then view's queryItem comboBox.
			String files[] = {""};
			String queryResponse;
			while ((queryResponse = reader.readLine()) != null) {
				files[0]+=queryResponse;
			}
			reader.close();
			httpConnQuery.disconnect();

			if (files[0].equals("")){
				view.setQueryItems(files);
				return;
			} else {
				view.setQueryItems(files[0].split(OPT_SEP));
			}
		} catch (IOException e){
			view.setFeedback(e.toString());
			return;
		} // End HTTP Get-Request
	}

	class doLoginListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			username = view.getUserInput('u');
			String password = view.getUserInput('p');
			Boolean loginOK = false;
			//VANESSA START HERE
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			
			//	connect to database and use SELECT query to validate login
			//System.out.println("mysqlURL: " + mysqlURL);
			try	{
				Class.forName(JDBC_DRIVER);
				con = DriverManager.getConnection(mysqlURL,USER,PASS);
				String loginSQL = "SELECT * FROM systemusers WHERE userName = ? AND password = ?";
				ps = con.prepareStatement(loginSQL);
				ps.setString(1, username);
				ps.setString(2, password);
				rs = ps.executeQuery();
				
				//	if user/pass combination found in the database, login succesful
				System.out.println("rs.next():" + rs.next());
				if(!rs.next())	{
					loginOK = true;
				}	
			}	catch(Exception e1)	{
				e1.toString();
			}
			//VANESSA IF LOGIN IS SUCESSFUL, END HERE
			if (loginOK){
				view.setUploadDownloadLayout(); // Display Upload/Download layout
				view.setFeedback("Login successful.");
				doQuery(); // Refresh query items.
			} else {
				view.setFeedback("Login Failed. Re-enter username and password.");
			}
		}
	}

	class doRegisterListener implements ActionListener{

		public void actionPerformed(ActionEvent e){
			username = view.getUserInput('u');
			String password = view.getUserInput('p');
			Boolean loginOK = false;
			//VANESSA START HERE
			Connection con = null;
			PreparedStatement ps = null;
			int i;
			
			try	{
				Class.forName(JDBC_DRIVER);
				con = DriverManager.getConnection(mysqlURL,USER,PASS);
				String registerSQL = "INSERT INTO systemusers(userName,password) VALUES (?,?)";
				ps = con.prepareStatement(registerSQL);
				ps.setString(1, username);
				ps.setString(2, password);
				i = ps.executeUpdate();
				
				if(i > 0)	{
					loginOK = true;
				}	
				
			}	catch(Exception e1)	{
				e1.toString();
			}
			//VANESSA IF REGISTRATION IS SUCESSFUL, END HERE
			if (loginOK){
				view.setUploadDownloadLayout(); // Display Upload/Download layout
				view.setFeedback("Login successful.");
				doQuery(); // Refresh query items.
			} else {
				view.setFeedback("Login Failed. Re-enter username and password.");
			}
		}
	}


	class doUploadListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try{ // Begin HTTP-Post request.

				// Encodes username into the boundary of the multipart http request.
				String boundary = "username=" + username + OPT_SEP + "===" +
								System.currentTimeMillis() + "===";

				httpConnUpload = (HttpURLConnection)uploadURL.openConnection();
				httpConnUpload.setDoOutput(true);
				httpConnUpload.setDoInput(true);
				httpConnUpload.setRequestProperty("Content-Type",
	                "multipart/form-data; boundary=" + boundary);

				//Pulls filepath from the user input.
				File file = new File(view.getUserInput('f'));
				OutputStream servletOutputStream = httpConnUpload.getOutputStream();
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(servletOutputStream, CHAR_SET), true);

				//Begin encoding user meta-data
				writer.append("--" + boundary).append(NEXT_LINE);
				writer.append("Content-Disposition: form-data; name=\"" + "uploadFile"
		                + "\"; filename=\"" + file.getName() + "\"").append(NEXT_LINE);
				writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append(NEXT_LINE);
				writer.append("Content-Transfer-Encoding: binary").append(NEXT_LINE);
		        writer.append(NEXT_LINE);
		        writer.flush();

		        //Begin file upload portion, pulls file byte-by-byte into the buffer
				FileInputStream fileInputStream = new FileInputStream(file);
				byte[] buffer = new byte[BYTE_CHUNK];
				int bytesRead = -1;
				long totalBytes = 0;
				while ((bytesRead = fileInputStream.read(buffer)) != -1){
					servletOutputStream.write(buffer, 0, bytesRead);
					totalBytes += bytesRead;
					view.setFeedback("Bytes Uploaded: " + totalBytes);
				}
				servletOutputStream.flush();
				//End file upload portion.

				//Safety flush.
				writer.append(NEXT_LINE).flush();
				writer.append(NEXT_LINE).flush();
				writer.append("--" + boundary + "--").append(NEXT_LINE).flush();
				writer.close();
				//Safety flush.

				//Consume response from the server.
				if (httpConnUpload.getResponseCode() == HttpURLConnection.HTTP_OK){
					String response;
					BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnUpload.getInputStream()));
					while ((response = reader.readLine()) != null){
						view.setFeedback(response); // Write response to feedback window.
					}
				}

				servletOutputStream.close();
				fileInputStream.close();

			} catch (Exception err) {
				view.setFeedback("File not found.");
				return;
			}
			httpConnUpload.disconnect();
			doQuery(); // Refresh query items.
		} // End HTTP-Post Request
	}

	class doDownloadListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try { // Begin HTTP-Post Rquest
				String filename = view.getUserInput('q');
				if (filename.equals("")){ // Check for empty query item box.
					view.setFeedback("Please select a file to download.");
					return;
				}

				//Encode username and filename into boundary.
				String boundary = "username=" + username + OPT_SEP +  "filename=" +
								filename + OPT_SEP +  "===" + System.currentTimeMillis() + "===";

				//Initiate multipart request.
				httpConnDownload = (HttpURLConnection)downloadURL.openConnection();
				httpConnDownload.setDoOutput(true);
				httpConnDownload.setDoInput(true);
				httpConnDownload.setRequestProperty("Content-Type",
	                			"multipart/form-data; boundary=" + boundary);

				OutputStream servletOutputStream = httpConnDownload.getOutputStream();
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(servletOutputStream, CHAR_SET), true);
				writer.flush();
				writer.close();

				//Fire request
				if (httpConnDownload.getResponseCode() == HttpURLConnection.HTTP_OK) {

					//Open fileoutput stream from the input stream of the http request.
					BufferedInputStream bufferedInputStream = new BufferedInputStream(httpConnDownload.getInputStream());
					File file = new File(filename);
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
										new FileOutputStream(outputFilepath + file.getName()));

					//Write bytes into output stream until file is fully downloaded to output.
					int byteStream = -1;
					byte[] byteArry = new byte[BYTE_CHUNK];
					while ((byteStream = bufferedInputStream.read(byteArry)) > 0) {
						bufferedOutputStream.write(byteArry, 0, byteStream);
					}
					bufferedInputStream.close();
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
					httpConnDownload.disconnect();
					view.setFeedback("File download complete.");
				}



			} catch (Exception err) {
				System.out.println(e);
			} // End HTTP-Post Request
		}
	}

	class doDeleteListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try{ // Begin HTTP-Get Request
				String filename = view.getUserInput('q');
				if (filename.equals("")){ // Empty query item box check
					view.setFeedback("Please select a file to delete.");
					return;
				}

				// URL needs to be regenerated during each deletion, username and file encoded in URL
				deleteURL = new URL(uploadURL.toString() + "?username=" + username +
					"&filename=" + filename);

				httpConnDelete = (HttpURLConnection)deleteURL.openConnection();
				httpConnDelete.setRequestMethod("GET");
				httpConnDelete.setRequestProperty("Accept-Charset", CHAR_SET);
				httpConnDelete.getResponseCode(); // Fire request

				//Consume response in string buffer.
				BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnDelete.getInputStream()));
				String response;
				String deleteResponse = "";
				while ((response = reader.readLine()) != null){
					deleteResponse += response;
				}
				reader.close();
				view.setFeedback(deleteResponse); // Output response to feedback window.
				httpConnDelete.disconnect();


			} catch (Exception err){
				System.out.println(e);
			} // End HTTP-Get Request
			doQuery(); // Refresh query items.
		}
	}	
}