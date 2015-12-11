import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;



public class WormholeDownloader{

	private String username;
	private String urlStr;
	private HttpURLConnection httpConn;
	private URL url;
	OutputStream oStream;
    PrintWriter writer;



	public WormholeDownloader(String username, String urlStr){
		this.username = username;
		this.urlStr =urlStr;
	}

	public void query() {
		
		try{
			url = new URL(urlStr + "?username=" + username);
			httpConn = (HttpURLConnection)url.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn.setRequestProperty("Accept-Charset", "UTF-8");
			httpConn.getResponseCode();

			BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));

			String filesTemp = "";
			String response;
			while ((response = reader.readLine()) != null) {
				filesTemp+=response;
			}

			if (filesTemp.equals("")){
				System.out.println("No files to display.");
			} else {
				String files[] = filesTemp.split("//");
				for (String fileName : files){
					System.out.println(fileName);
				}
			}


		} catch (IOException e){
			System.out.println(e);
		}


	}

	public Boolean download(String filename){
		String boundary = filename+ "===" + System.currentTimeMillis() + "===";

		try {
			url = new URL(urlStr);
			httpConn = (HttpURLConnection)url.openConnection();
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setRequestProperty("Content-Type",
	                "multipart/form-data; boundary=" + boundary);

			oStream = httpConn.getOutputStream();
			writer = new PrintWriter(new OutputStreamWriter(oStream, Wormhole.CHAR_SET), true);
			writer.flush();
			writer.close();
			

			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK){
				//BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
				//List items = 
				//String response;

				BufferedInputStream bis = new BufferedInputStream(httpConn.getInputStream());
				File test = new File(filename);
				BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(test.getName()));

				int i = -1;
				byte[] arry = new byte[4096];
				while ((i = bis.read(arry)) > 0) {
					System.out.println(i);
					bos.write(arry, 0, i);
				}
				bos.flush();
				bos.close();
			}



		} catch (IOException e) {
			System.out.println(e);
		}



		System.out.println("Feature not yet implemented.");
		return false;
	}

}