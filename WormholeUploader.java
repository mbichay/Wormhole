import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.io.FileInputStream;


public class WormholeUploader{
	
	private String username;
	private String urlStr;
	private HttpURLConnection httpConn;
	private URL url;
	OutputStream oStream;
    PrintWriter writer;
    String boundary;

    public static String NEXT_LINE = "\r\n";


	public WormholeUploader(String username, String urlStr){
		this.username = username;
		this.urlStr =urlStr;
	}

	public Boolean upload(String filepath){

		this.boundary = this.username+ "===" + System.currentTimeMillis() + "===";

		try{
			this.url = new URL(urlStr);
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setRequestProperty("Content-Type",
	                "multipart/form-data; boundary=" + boundary);

			File file = new File(filepath);
			oStream = httpConn.getOutputStream();
			writer = new PrintWriter(new OutputStreamWriter(oStream, Wormhole.CHAR_SET), true);

			writer.append("--" + boundary).append(NEXT_LINE);
			writer.append("Content-Disposition: form-data; name=\"" + "uploadFile"
	                + "\"; filename=\"" + file.getName() + "\"").append(NEXT_LINE);
			writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append(NEXT_LINE);
			writer.append("Content-Transfer-Encoding: binary").append(NEXT_LINE);
	        writer.append(NEXT_LINE);
	        writer.flush();

	        push_file(file);
	    } catch (IOException e){
	    	System.out.println(e);
	    	return false;
	    }

		return servlet_response();
	}

	private void push_file(File file){
		try {
			FileInputStream iStream = new FileInputStream(file);
			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			long totalBytes = 0;
			long fileSize = file.length();

			while ((bytesRead = iStream.read(buffer)) != -1){
				oStream.write(buffer, 0, bytesRead);
				totalBytes += bytesRead;
				System.out.println("Bytes Uploaded: " + totalBytes);
			}
			iStream.close();
			oStream.flush();
			writer.append(NEXT_LINE);
			writer.flush();
			writer.append(NEXT_LINE).flush();
			writer.append("--" + boundary + "--").append(NEXT_LINE);
			writer.close();
		} catch (IOException e){
			System.out.println(e);
		}
	}

	private Boolean servlet_response(){
		try {
			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK){
				BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
				String response;
				while ((response = reader.readLine()) != null) {
					System.out.println(response);
					if (response == "File upload failure."){
						return false;
					}
				}

			} else {
				System.out.println("Connection not made, check URL in configuration file.");
				return false;
			}
			System.out.println("");
			return true;
		} catch (IOException e){
			System.out.println(e);
			return false;
		}
	}



}