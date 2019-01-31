import java.net.*;
import java.io.*;
import java.util.*;
import javax.crypto.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.lang.*;
import javax.crypto.spec.*;
class Server extends JFrame
{
	static ServerSocket ss=null;
	static Socket sk=null;
	static DataOutputStream out=null;
	static DataInputStream in=null;

	JPanel jp;
	JLabel jl;
	JTextField jt1;
	JRadioButton jr1,jr2;
	ButtonGroup bg;
	JTextArea jta;
	JButton jb;

	public Server() throws Exception,NumberFormatException
	{
		jp=new JPanel();
		add(jp);		
		jl=new JLabel("Port no : ");
		jt1=new JTextField(5);
		jr1=new JRadioButton("start");
		jr2=new JRadioButton("stop");
		bg=new ButtonGroup();
		jta=new JTextArea(20,20);
		jb=new JButton("receive");

		jp.setLayout(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();

		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridx=0;
		gbc.gridy=0;
		jp.add(jl,gbc);

		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridx=1;
		gbc.gridy=0;
		jp.add(jt1,gbc);		

		gbc.fill=GridBagConstraints.HORIZONTAL;
		bg.add(jr1);
		bg.add(jr2);
		gbc.gridx=0;
		gbc.gridy=1;
		jr1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(ae.getSource()==jr1)
				{
					try{
						startServer();
					}catch(Exception e)
					{
						System.out.println(e.getMessage());
					}
				}
			}
		}
		);
		jp.add(jr1,gbc);

		gbc.gridx=1;
		gbc.gridy=1;
		jr2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)	
			{
				if(ae.getSource()==jr2)
				{
					try{
						stopServer();
					}catch(Exception e)
					{
						System.out.println(e.getMessage());
					}
				}
			}
		}
		);
		jp.add(jr2,gbc);

		gbc.gridx=0;
		gbc.gridy=2;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridwidth=3;
		jp.add(jta,gbc);	

		gbc.gridx=0;
		gbc.gridy=3;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridwidth=3;
		jb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)	
			{
				if(ae.getSource()==jb)
				{
					try{
						receiving();
					}catch(Exception e)
					{
						System.out.println(e.getMessage());
					}
				}
			}
		}
		);
		jp.add(jb,gbc); 

		addWindowListener(new java.awt.event.WindowAdapter(){
		public void windowClosing(java.awt.event.WindowEvent we)
		{
			System.exit(0);
		}
		});
	}
	public void startServer() throws Exception
	{
		String s1=jt1.getText();
		ss=new ServerSocket(Integer.parseInt(s1));
		jta.setText("Server is listening at port  "+jt1.getText());
		sk=ss.accept();
		jta.append("\n\nA Client connected\n"); 	
		in=new DataInputStream(sk.getInputStream());
		out=new DataOutputStream(sk.getOutputStream());			
		jta.append("\nI/O streams established\n");
	}
	public void receiving()throws Exception
	{
		String text="";
		text=in.readUTF();
		String clientMac="";
		String serverMac="";
	
		if(text.length()<10)
		{
			jta.append("\n\nSecure message is : "+text);
			String encodedKey=in.readUTF();
			jta.append("\nread the encodedkey ");
			byte[] decodedKey=Base64.getDecoder().decode(encodedKey);
			SecretKey originalKey=new SecretKeySpec(decodedKey,0,decodedKey.length,"HmacMD5");
			jta.append("\noriginal key is "+originalKey);

			jta.append("\nreading the mac value");
			clientMac=in.readUTF();
			jta.append("\nreceived mac value is "+clientMac);
	
			jta.append("\ngenerating the mac value for received message");
			Mac mac=Mac.getInstance("HmacMD5");
			byte[] plaintext=text.getBytes("UTF8");
			mac.init(originalKey);
			serverMac=new String(mac.doFinal(plaintext),"UTF8");		
			jta.append("\ngenerated mac value is "+serverMac);
		}
		else
		{
			text=text+"is";
			String encodedKey=in.readUTF();
			jta.append("\n\nread the encodedkey ");
			byte[] decodedKey=Base64.getDecoder().decode(encodedKey);
			SecretKey originalKey=new SecretKeySpec(decodedKey,0,decodedKey.length,"HmacMD5");
			jta.append("\noriginal key is "+originalKey);

			jta.append("\nreading the mac value");
			clientMac=in.readUTF();
			jta.append("\nreceived mac value is "+clientMac);
	
			jta.append("\ngenerating the mac value for received message");
			Mac mac=Mac.getInstance("HmacMD5");
			byte[] plaintext=text.getBytes("UTF8");
			mac.init(originalKey);
			serverMac=new String(mac.doFinal(plaintext),"UTF8");		
			jta.append("\ngenerated mac value is "+serverMac);
		}

		if(serverMac.equalsIgnoreCase(clientMac))							
		{
			jta.append("\n\nMessage is from an authenticated user..");
		}
		else
		{
			jta.append("\n\nMessage is not from an authenticated user..");
		}
	}

	public void stopServer() throws Exception
	{
		sk.close();
		jta.setText("\nserver stopped\n");
		ss.close();
		in.close();
		out.close();
	}
	public static void main(String[] args) throws Exception
	{		
		Server es=new Server();
		es.setTitle("Server");
		es.setSize(500,500);
		es.setVisible(true); 
	}
}
