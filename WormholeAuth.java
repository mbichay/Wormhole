import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.URLConnection;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class WormholeAuth{

	public static String FORMAT = "UTF-8";
	Boolean valid = false;

	public WormholeAuth(String username, String password, String url){
		/*
		VANESSA, YOU CAN IGNORE THE URL PART IF YOU DON'T NEED IT.
		PLEASE CHANGE THE VALID BIT TO TRUE IF THEY'VE LOGGED IN AND TO
		FALSE IF THE PASSWORD WAS INCORRECT.

		THANKS!
		*/
		valid = true;
	}

	public Boolean isValid(){
		return valid;
	}
}