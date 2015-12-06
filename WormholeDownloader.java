import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WormholeDownloader{

	private String username;
	private String urlStr;
	private HttpURLConnection httpConn;
	private URL url;



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
		System.out.println("Feature not yet implemented.");
		return false;
	}

}