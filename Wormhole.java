import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.StringBuilder;
import java.util.Scanner;

public class Wormhole{

	private String username = "";
	private String password = "";
	private String url = "";
	public static String CONFIG_FILE = "config.ini";
	public static String CHAR_SET = "UTF-8";

	private WormholeUploader whUploader;
	private WormholeAuth auth;
	private WormholeDownloader whDownloader;
	Scanner input;

	public static void main(String args[]){
		System.out.println("Welcome to Wormhole");
		Wormhole wh = new Wormhole();

		if (!wh.parseConfig()){
			System.out.println("Please input configuration parameters correctly.");
			return;
		}

		if (wh.authenticate()){
			wh.init();
		} else {
			System.out.println("Username or Password invalid.");
			return;
		}

		wh.menuLoop();
	}

	public Boolean parseConfig(){
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(CONFIG_FILE))){
			
			String line = bufferedReader.readLine();
			String option;
			String input;

			while (line!=null){
				option = line.substring(0,line.indexOf('='));
				input = line.substring(line.indexOf('=')+1, line.length());
				if (option.equals("username")){
					this.username = input;
				} else if (option.equals("password")){
					this.password = input;
				} else if (option.equals("url")){
					this.url = input;
				}
				line = bufferedReader.readLine();
			}
		} catch (IOException e){
			System.out.println("Config.ini corrupted, please fix file and try again.");
			return inputValid();
		}
		return inputValid();
	}

	//Input validation for Username, Password, and URL.
	private Boolean inputValid(){
		if (username.equals("") || password.equals("") || url.equals("")){
			return false;
		} return true;
	}


	public void menuLoop(){
		input = new Scanner(System.in);
		Boolean cont = true;
		Integer selection;
		while (cont){
			System.out.println("Menu Options:");
			System.out.println("Query: 1");
			System.out.println("Upload: 2");
			System.out.println("Download: 3");
			System.out.println("Exit: 0\n");
			System.out.print("Please select option: ");

			try {
				selection = Integer.parseInt(input.nextLine());
			} catch (Exception e){
				System.out.println("Please choose a valid option");
				continue;
			}

			switch(selection){
				case 1: query(); break;
				case 2: upload(); break;
				case 3: download(); break;
				case 0: cont = false; break;
				default: System.out.println("Please choose a valid option"); break;
			}

		}
	}

	public Boolean authenticate(){
		/*
		VANESSA HERE IS WHERE YOU CALL THE CONSTRUCTOR FOR WORMHOLEAUTH ( YOU CAN CHANGE W/E YOU WANT IN THE CONSTRUCTOR)
		THEN RETURN THE BOOOLEAN VALUE ISVALID AS FALSE IF THEY ENTER THEIR PW OR USERNAME INCORRECTLY.
		*/
		auth = new WormholeAuth(this.username, this.password, this.url + "/wormhole-register");
		return auth.isValid();
	}

	public Boolean upload(){
		System.out.print("Input filepath: ");
		String filepath = input.nextLine();
		return whUploader.upload(filepath);	
	}

	public void query(){
		System.out.println("Your files: ");
		whDownloader.query();
		System.out.println("");
	}

	public Boolean download(){
		System.out.print("Input file name: ");
		String fileName = input.nextLine();
		return whDownloader.download(fileName);	
	}

	public void init(){
		this.whUploader = new WormholeUploader(this.username, this.url + "/wormhole-upload");
		this.whDownloader =new WormholeDownloader(this.username, this.url + "/wormhole-download");
	}


}