import java.awt.*;
import javax.swing.*;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
class Wormhole extends JFrame{

	// const magic values
	final static int WIDTH = 410;
	final static int LRHEIGHT = 170;
	final static int UDHEIGHT = 225;

	// const GUI Assets
	private JPanel pnlUsernamePassword, pnlLoginRegister, pnlQueryBox, pnlFilepath, pnlUploadDownload, pnlFeedback;
	private GridLayout loginRegisterLayout, uploadDownloadLayout;
	private JButton loginButton, registerButton, uploadButton, downloadButton, deleteButton;
	private JComboBox<String> queryBox;
	private JTextField usernameTextField, passwordTextField, filepathTextField, feedbackTextField;

	public static void main(String[] args) {
		// Custom Wormhole config file.
		Wormhole wormhole = new Wormhole();
		String configFilepath = "config.ini";
		if (args.length > 0){
			configFilepath = args[0];
		}
		// Start engine
		WormholeEngine engine = new WormholeEngine(wormhole, configFilepath);
	}

	public Wormhole(){
		super("Wormhole");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		// Create login register asset.
		createLoginRegisterLayout();

		// Create upload download asset.
		createUploadDownloadLayout();

		// Start default layout as login/register asset.
		setLoginRegisterLayout();
	}

	// Assemble login register layout panels and assets.
	private void createLoginRegisterLayout(){
		loginRegisterLayout = new GridLayout(3,0);

		pnlUsernamePassword = new JPanel();
		pnlUsernamePassword.setLayout(new GridLayout(0,2));
		usernameTextField = new JTextField("");
		usernameTextField.setBorder(BorderFactory.createTitledBorder("Username:"));
		usernameTextField.setHorizontalAlignment(JTextField.CENTER);
		usernameTextField.setEditable(true);
		passwordTextField = new JTextField("");
		passwordTextField.setBorder(BorderFactory.createTitledBorder("Password:"));
		passwordTextField.setHorizontalAlignment(JTextField.CENTER);
		passwordTextField.setEditable(true);
		pnlUsernamePassword.add(usernameTextField);
		pnlUsernamePassword.add(passwordTextField);

		pnlLoginRegister = new JPanel();
		pnlLoginRegister.setLayout(new GridLayout(0,2));
		loginButton = new JButton("Login");
		registerButton = new JButton("Register");
		loginButton.setEnabled(true);
		registerButton.setEnabled(true);
		pnlLoginRegister.add(loginButton);
		pnlLoginRegister.add(registerButton);

		pnlFeedback = new JPanel();
		pnlFeedback.setLayout(new GridLayout(0,1));
		pnlFeedback.setBorder(BorderFactory.createTitledBorder("Feedback:"));
		feedbackTextField = new JTextField("");
		feedbackTextField.setHorizontalAlignment(JTextField.CENTER);
		feedbackTextField.setEditable(false);
		pnlFeedback.add(feedbackTextField);
	}

	// Bring login / register layout to view.
	public void setLoginRegisterLayout(){
		getContentPane().removeAll();
		setSize(WIDTH,LRHEIGHT);
		setLayout(loginRegisterLayout);
		add(pnlUsernamePassword);
		add(pnlLoginRegister);
		add(pnlFeedback);
		setFeedback("");
		setVisible(true);
	}


	// Assemble upload and download layout panels and assets.
	private void createUploadDownloadLayout(){
		uploadDownloadLayout = new GridLayout(4,0);

		pnlQueryBox = new JPanel();
		pnlQueryBox.setBorder(BorderFactory.createTitledBorder("Files:"));
		pnlQueryBox.setLayout(new GridLayout(0,1));
		queryBox = new JComboBox<String>();
		pnlQueryBox.add(queryBox);

		pnlFilepath = new JPanel();
		pnlFilepath.setLayout(new GridLayout(0,1));
		pnlFilepath.setBorder(BorderFactory.createTitledBorder("Upload Filepath:"));
		filepathTextField = new JTextField();
		filepathTextField.setEditable(true);
		pnlFilepath.add(filepathTextField);

		pnlUploadDownload = new JPanel();
		pnlUploadDownload.setLayout(new GridLayout(0,3));
		uploadButton = new JButton("Upload");
		uploadButton.setEnabled(true);
		downloadButton = new JButton("Download");
		downloadButton.setEnabled(true);
		deleteButton = new JButton("Delete");
		deleteButton.setEnabled(true);
		pnlUploadDownload.add(uploadButton);
		pnlUploadDownload.add(downloadButton);
		pnlUploadDownload.add(deleteButton);
	}

	// Bring upload / download layout to view.
	public void setUploadDownloadLayout(){
		getContentPane().removeAll();
		setSize(WIDTH, UDHEIGHT);
		setLayout(uploadDownloadLayout);
		add(pnlQueryBox);
		add(pnlFilepath);
		add(pnlUploadDownload);
		add(pnlFeedback);
		setFeedback("");
		setVisible(true);
	}

	// Similar to a console window, this provides 1-line feedback to the user.
	public void setFeedback(String feedback){
		feedbackTextField.setText(feedback);
	}


	// Action listener hooks
	// Call by signifying which actionable through the char "opt"
	public void setActionListener(char opt, ActionListener action){
		switch(opt){
			case 'l': loginButton.addActionListener(action); break;
			case 'r': registerButton.addActionListener(action); break;
			case 'u': uploadButton.addActionListener(action); break;
			case 'd': downloadButton.addActionListener(action); break;
			case 'q': queryBox.addActionListener(action); break;
			case 'D': deleteButton.addActionListener(action); break;
			default: return;
		}
	}

	// Locks down all buttons, text fields, etc
	// Displays lockdown message in the feedback Console.
	public void lockdown(Boolean lockdown, String feedback){
		if (lockdown){
			usernameTextField.setEditable(false);
			passwordTextField.setEditable(false);
			filepathTextField.setEditable(false);
			loginButton.setEnabled(false);
			registerButton.setEnabled(false);
			uploadButton.setEnabled(false);
			downloadButton.setEnabled(false);
			deleteButton.setEnabled(false);
			queryBox.setEnabled(false);
			setFeedback(feedback);
		} else {
			usernameTextField.setEditable(true);
			passwordTextField.setEditable(true);
			filepathTextField.setEditable(true);
			loginButton.setEnabled(true);
			registerButton.setEnabled(true);
			uploadButton.setEnabled(true);
			downloadButton.setEnabled(true);
			deleteButton.setEnabled(true);
			queryBox.setEnabled(true);
			setFeedback(feedback);
		}
	}

	// Method used for pulling specific String input from the user.
	public String getUserInput(char opt){
		String input;
		switch(opt){
			case 'u': {
				input = usernameTextField.getText();
				input = input.toLowerCase();
				usernameTextField.setText("");
				break;
			}

			case 'p': {
				input = passwordTextField.getText();
				passwordTextField.setText("");
				break;
			}

			case 'f':{
				 input = filepathTextField.getText();
				 filepathTextField.setText("")
;				 break;
			}

			case 'q': input = queryBox.getSelectedItem().toString(); break;

			default: return "";
		}
		return input.replaceAll("\\s","");
	}

	// Updates the query combo box for displaying files.
	public void setQueryItems(String[] items){
		queryBox.removeAllItems();
		if (items!=null)
			for (String item : items)
				queryBox.addItem(item);
	}
}