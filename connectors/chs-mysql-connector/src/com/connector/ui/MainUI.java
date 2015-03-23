package com.connector.ui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.connector.db.MySQLConnector;
import com.connector.network.Connections;
import com.connector.publisher.Publisher;
import com.connector.subscriber.SubscriberExchange;

public class MainUI implements ActionListener {

	JFrame appFrame, loginFrame;
	JLabel jlbTopicName, jlbValue, jlbQuery;
	JTextField jtfName, jtfValue, jtfQuery, userText;
	JPasswordField passwordText;
	JTextArea jtaSubscription;
	JButton jbnPublish, jbnClear, jbnExit, jbnRun;
    private JPanel controlPanel;

	@SuppressWarnings("rawtypes")
	private JComboBox jcbTopicDropdown;
	String topic, value;
	int phone;
	int recordNumber; // used to naviagate using >> and  buttons
	Container cPane;
	private String[] description = null;
	private String[] subscribe = null;
	public static void main(String args[]) {

		new MainUI();
	}
	public MainUI() {
		topic = "";
		value = "";
		LoginUI(); 
//			createGUI();
		
	}
	
	public void LoginUI() {
		
		loginFrame = new JFrame("IIITD CHS Application");
		loginFrame.setSize(300, 150);
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		placeComponents(loginFrame);
		loginFrame.setVisible(true);
	}
	
	private void placeComponents(JFrame frame) {
		frame.setLayout(null);

		JLabel userLabel = new JLabel("User");
		userLabel.setBounds(10, 10, 80, 25);
		frame.add(userLabel);

		userText = new JTextField(20);
		userText.setBounds(100, 10, 160, 25);
		frame.add(userText);

		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(10, 40, 80, 25);
		frame.add(passwordLabel);

		passwordText = new JPasswordField(20);
		passwordText.setBounds(100, 40, 160, 25);
		frame.add(passwordText);

		JButton loginButton = new JButton("login");
		loginButton.setBounds(10, 80, 80, 25);
		frame.add(loginButton);

		ActionListener loginButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton source = (JButton) e.getSource();
				String username = userText.getText();
				String password = passwordText.getText();
				
				Connections con = new Connections();
				try {
					description = con.getPublishTopics(username, password);
					subscribe = con.getSubscribedTopics(username, password);
					
					loginFrame.dispose();
					JOptionPane.showMessageDialog(source, source.getText()
							+ " Successful");
					createGUI();
					
					
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(source, source.getText()
							+ " Failed");
					e1.printStackTrace();
				}
				
			}
		};
		loginButton.addActionListener(loginButtonListener);
		
	}
	
	
	public void createGUI(){

		
   		/*Create a frame, get its contentpane and set layout*/
   		appFrame = new JFrame("AIIMS Connector");
   		cPane = appFrame.getContentPane();
   		cPane.setLayout(new GridBagLayout());
   		//Arrange components on contentPane and set Action Listeners to each JButton
   		controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
       
   		arrangeComponents();
   		appFrame.setSize(700,500);
   		appFrame.add(controlPanel);
   		appFrame.setResizable(true);
   		appFrame.setVisible(true);

   	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void arrangeComponents() {
		jlbTopicName = new JLabel("Topic");
		jlbValue = new JLabel("Value");
		jlbQuery = new JLabel("Query");
		
//		jtfName = new JTextField(20);
		jtfValue = new JTextField(10);
		jtfQuery = new JTextField(10);
		jcbTopicDropdown = new JComboBox();
		for (int i = 0; i < description.length; i++) {
			jcbTopicDropdown.addItem(description[i]);
		}
		jbnPublish = new JButton("Publish");
		jbnClear = new JButton("Clear");
		jbnExit = new JButton("Exit");
		jbnRun = new JButton("Run");
		
		jtaSubscription = new JTextArea("Logs \n", 10,30);
//		jtaSubscription.setCaretPosition(jtaSubscription.getDocument().getLength());
		jtaSubscription.setEditable(true);
		JScrollPane scrollPane = new JScrollPane(jtaSubscription);		/*add all initialized components to the container*/
		GridBagConstraints gridBagConstraintsx01 = new GridBagConstraints();
		gridBagConstraintsx01.gridx = 0;
		gridBagConstraintsx01.gridy = 0;
		gridBagConstraintsx01.insets = new Insets(5, 5, 5, 5);
		cPane.add(jlbTopicName, gridBagConstraintsx01);
		GridBagConstraints gridBagConstraintsx02 = new GridBagConstraints();
		gridBagConstraintsx02.gridx = 1;
		gridBagConstraintsx02.insets = new Insets(5, 5, 5, 5);
		gridBagConstraintsx02.gridy = 0;
		gridBagConstraintsx02.gridwidth = 1;
		gridBagConstraintsx02.fill = GridBagConstraints.BOTH;
		cPane.add(jcbTopicDropdown, gridBagConstraintsx02);
		GridBagConstraints gridBagConstraintsx03 = new GridBagConstraints();
		gridBagConstraintsx03.gridx = 0;
		gridBagConstraintsx03.insets = new Insets(5, 5, 5, 5);
		gridBagConstraintsx03.gridy = 1;
		cPane.add(jlbValue, gridBagConstraintsx03);
		GridBagConstraints gridBagConstraintsx04 = new GridBagConstraints();
		gridBagConstraintsx04.gridx = 1;
		gridBagConstraintsx04.insets = new Insets(5, 5, 5, 5);
		gridBagConstraintsx04.gridy = 1;
		gridBagConstraintsx04.gridwidth = 1;
		gridBagConstraintsx04.fill = GridBagConstraints.BOTH;
		cPane.add(jtfValue, gridBagConstraintsx04);
		GridBagConstraints gridBagConstraintsx05 = new GridBagConstraints();
		gridBagConstraintsx05.gridx = 0;
		gridBagConstraintsx05.insets = new Insets(5, 5, 5, 5);
		gridBagConstraintsx05.gridy = 4;
		cPane.add(jlbQuery, gridBagConstraintsx05);
		GridBagConstraints gridBagConstraintsx06 = new GridBagConstraints();
		gridBagConstraintsx06.gridx = 1;
		gridBagConstraintsx06.insets = new Insets(5, 5, 5, 5);
		gridBagConstraintsx06.gridy = 4;
		gridBagConstraintsx06.gridwidth = 1;
		gridBagConstraintsx06.fill = GridBagConstraints.BOTH;
		cPane.add(jtfQuery, gridBagConstraintsx06);
		GridBagConstraints gridBagConstraintsx09 = new GridBagConstraints();
		gridBagConstraintsx09.gridx = 1;
		gridBagConstraintsx09.gridy = 6;
		gridBagConstraintsx09.insets = new Insets(5, -200, 5, 20);
		cPane.add(jbnPublish, gridBagConstraintsx09);
		GridBagConstraints gridBagConstraintsx10 = new GridBagConstraints();
		gridBagConstraintsx10.gridx = 1;
		gridBagConstraintsx10.gridy = 6;
		gridBagConstraintsx10.insets = new Insets(5, 0, 5, 20);
		cPane.add(jbnRun, gridBagConstraintsx10);
		GridBagConstraints gridBagConstraintsx15 = new GridBagConstraints();
		gridBagConstraintsx15.gridx = 1;
		gridBagConstraintsx15.insets = new Insets(5, 150, 5, 5);
		gridBagConstraintsx15.gridy = 6;
		cPane.add(jbnClear, gridBagConstraintsx15);
		GridBagConstraints gridBagConstraintsx16 = new GridBagConstraints();
		gridBagConstraintsx16.gridx = 1;
		gridBagConstraintsx16.gridy = 10;
		gridBagConstraintsx16.insets = new Insets(5, 100, 5, 0);
		cPane.add(jbnExit, gridBagConstraintsx16);
		GridBagConstraints gridBagConstraintsx17 = new GridBagConstraints();
		gridBagConstraintsx17.gridx = 1;
		gridBagConstraintsx17.gridy = 8;
		cPane.add(scrollPane, gridBagConstraintsx17);
		jbnPublish.addActionListener(this);
		jbnRun.addActionListener(this);
		jbnClear.addActionListener(this);
		jbnExit.addActionListener(this);
		
		 SubscriberExchange R1 = new SubscriberExchange(subscribe, jtaSubscription);
		 R1.start();
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbnPublish) {
			publishValue();
			clear();
		} else if (e.getSource() == jbnRun) {
			runQuery(); 
		}
		else if (e.getSource() == jbnClear) {
			clear();
		} else if (e.getSource() == jbnExit) {
			System.exit(0);
		}
	}
	
	private void runQuery() {
		String query = jtfQuery.getText();
		//TODO : Shift this to startup after login page using set db connections  
		MySQLConnector mysql = new MySQLConnector();
		try {
			String res = mysql.getQueryResult(query);
			jtfValue.setText(res);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,
					"Error Executing Query !!");
			e.printStackTrace();
		}
		
	}
	public void publishValue() {
		topic = (String)jcbTopicDropdown.getSelectedItem();
		value = jtfValue.getText();
	
		
		if (value.equals("")) {
			JOptionPane.showMessageDialog(null,
					"Please enter a value.");
		} else {

			//TODO Publish Message
			Publisher p = new Publisher(topic, value);
			try {
				p.Publish();
				jtaSubscription.append(" [x] Sent 'Topic" + topic + ": Value" + value +"'\n");
				JOptionPane.showMessageDialog(null, "Message Published");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Message Publish Failed");
				e.printStackTrace();
			}
			
		}
	}
	
	public void printSubscription() {
		
	}
	
	public void clear() {
//		jtfName.setText("");
		jtfValue.setText("");
		jtfQuery.setText("");
		/*clear contents of arraylist*/
		recordNumber = -1;
		
	}
}