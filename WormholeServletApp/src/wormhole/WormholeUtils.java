package wormhole;

import java.io.File;

import javax.servlet.http.HttpServlet;

public class WormholeUtils {

	public static String parseOption(String str, String opt){
		str = str.substring(str.indexOf(opt)+opt.length(), str.length());
		return str.substring(0 , str.indexOf("//"));
	}
	
	
}
