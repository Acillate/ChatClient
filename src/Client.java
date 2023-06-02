import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.TextArea;

public class Client extends JFrame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel imgLabel = new JLabel("");
	private Socket client; // Create a socket for the client to connect to the server
	private BufferedReader in; // Create a buffered reader to take in messages from the server
	private PrintWriter out; // Create a print writer to send messages to the server
	private JPanel contentPane; // Create a JPanel to create a GUI for the client
	private JTextField textField; // Create a JTextField to type messages
	private String msgToSend; // Create a string to send messages to the server
	//private String HLGAMEMSG;
	private TextArea textArea; // Create a TextArea to display messages from the server
	private List<String> msgList = new ArrayList<>(); // Create a arraylist to hold our messages from the server
	private ImageIcon card;
	private int suit;
	private int value;
	private String[] msgSuit;
	private String[] msgValue;

    String inMessage;
	public Client()  {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Exit the JFrame on closing the frame
		setBounds(100, 100, 573, 688);//Set bounds for the JFrame
		contentPane = new JPanel(); // contentPane is a new JPanel
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		imgLabel.setBounds(431, 158, 209, 253);
		contentPane.add(imgLabel);

		textField = new JTextField();//textField is a new JTextField object	
		textField.setBounds(10, 618, 322, 20);//Set the bounds for the textField
		contentPane.add(textField);//Add the textField to our contentPane
		textField.setColumns(10);

		JButton btnNewButton = new JButton("Send"); //Create a new button to send messages
		btnNewButton.addActionListener(new ActionListener() {//Attach an action listener to the button
			public void actionPerformed(ActionEvent e) {//Add a action performed so it knows when we click the button
				try {

					msgToSend = textField.getText(); //Store what we typed in the textField into the string
					out.println(msgToSend); //Send whats stored in the string to the server
					textField.setText("");//Reset the textField to blank
					//hlGame();
					
				} catch (Exception e2) {
					e2.printStackTrace();
				}

			}
		});
		btnNewButton.setBounds(342, 615, 89, 23);//Set the bounds for the button
		contentPane.add(btnNewButton);//Add button to the contentPane

		textArea = new TextArea();//Set textArea as a new TextArea object
		textArea.setBounds(10, 10, 414, 586);//Set the bounds for our textArea
		textArea.setEditable(false);//Make it so client cant edit the textArea
		contentPane.add(textArea);//Add textArea to our contentPane
		
		
	}
	@Override
	public void run() {
		try {
			String ip = JOptionPane.showInputDialog("Please enter the IP of the server "
					+ "you are trying to connect to",  JOptionPane.QUESTION_MESSAGE);//Ask user to input ip address
			while(ip.isBlank() || ip.isEmpty()) {
				ip = JOptionPane.showInputDialog("Please enter the IP of the server "
						+ "you are trying to connect to",  JOptionPane.QUESTION_MESSAGE);//Ask user to input ip address
			}
			client = new Socket(ip, 8088); // Set client to a new socket object and we give it our ip and port to connect to
			out = new PrintWriter(client.getOutputStream(), true); //lets the user send messages to the server
			in = new BufferedReader(new InputStreamReader(client.getInputStream())); //lets user take in messages from the server
			Thread t = new Thread();// Create a new Thread 
			t.start(); // Start the thread
			 // Create a new string 
			while ((inMessage = in.readLine()) != null) {// while the messages coming in is not null do this loop
				if(inMessage.startsWith("suit")) {
					msgSuit = inMessage.split(" ");
					System.out.println("Suit " + msgSuit.length);
					if(msgSuit.length == 2) {
						suit = Integer.parseInt(msgSuit[1]);
						System.out.println("Suit: " + msgSuit[1]);
					}
				}
				if(inMessage.startsWith("value")) {
					msgValue = inMessage.split(" ");
					System.out.println("Value " + msgValue.length);
					System.out.println(toString());
					if(msgValue.length == 2) {
						value = Integer.parseInt(msgValue[1]);
						System.out.println("Value: " + msgValue[1]);
					}
				}
				msgList.add(inMessage); // add the message coming in to our arraylist
				textArea.setText(null);//Clear the textArea
				for(int i = 0; i < msgList.size(); i++) {//Loop though the arraylist
					textArea.append(msgList.get(i)+"\n");//Display on the textArea the current index of the arraylist	
				}	
				card = new ImageIcon(getClass().getResource("/images/"+suit + "." + value + ".png"));
				imgLabel.setIcon(card);
			}
		}
		catch (IOException e) {
			shutDown();
		}
	}
	public void shutDown() {
		try {
			in.close();//Close our buffered reader
			out.close();//Close out Print writer
			if (!client.isClosed()) {//If client is not closed do this
				client.close();//Close the client
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	} 


	public void refreshChat() throws IOException {
		textArea.setText(null);//Clear the textArea
		for(int i = 0; i < msgList.size(); i++) {//Loop though the arraylist
			textArea.append(msgList.get(i)+"\n");//Display on the textArea the current index of the arraylist
		}
	}
	
	/*
	public void getFile() throws IOException {
		 InputStream inputStream = client.getInputStream(); 
		 BufferedInputStream bis = new BufferedInputStream(inputStream);
		 BufferedImage bufferedImage = ImageIO.read(bis);
		 JLabel jlp = new JLabel(new ImageIcon(bufferedImage));
		 contentPane.add(jlp, BorderLayout.CENTER);
	}
	*/
	


	public static void main(String[] args) {
		Client client = new Client(); // Create a new client object
		client.setVisible(true);//Set GUI visable to true
		client.run();//Run our client
	}
	@Override
	public String toString() {
		return "Client [msgSuit=" + Arrays.toString(msgSuit) + ", msgValue=" + Arrays.toString(msgValue) + "]";
	}
}




