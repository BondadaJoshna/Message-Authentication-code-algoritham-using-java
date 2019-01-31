import java.io.*;
import java.net.*;
import javax.crypto.Mac;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Base64;
import java.util.UUID;
public class Client extends JFrame
{
	static Socket sk=null;
	static DataOutputStream out=null;
	static DataInputStream in=null;

	JPanel jp;
	JLabel jl1,jl2;
	JTextField jt1,jt2;
	JRadioButton jr1,jr2;
	ButtonGroup bg;
	JTextArea jta;
	JTextField jt3;
	JButton jb;

	public Client() throws Exception
	{
		jp=new JPanel();
		add(jp);
		jl1=new JLabel("IP Address : ");
		jt1=new JTextField(15);
		jl2=new JLabel("Port No : ");
		jt2=new JTextField(5);
		jr1=new JRadioButton("connect");
		jr2=new JRadioButton("disconnect");
		bg=new ButtonGroup();
		jta=new JTextArea(20,20);
		jt3=new JTextField(10);
		jb=new JButton("send");

		jp.setLayout(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();

		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridx=0;
		gbc.gridy=0;
		jp.add(jl1,gbc);

		gbc.gridx=1;
		gbc.gridy=0;
		jp.add(jt1,gbc);		

		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridx=0;
		gbc.gridy=1;
		jp.add(jl2,gbc);

		gbc.gridx=1;
		gbc.gridy=1;
		jp.add(jt2,gbc);

		gbc.fill=GridBagConstraints.HORIZONTAL;
		bg.add(jr1);
		bg.add(jr2);
		gbc.gridx=0;
		gbc.gridy=2;

		jp.add(jr1,gbc);
		jr1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(ae.getSource()==jr1)
				{
					try{
						getConnected();
					}catch(Exception e)
					{
						System.out.println(e.getMessage());
					}
				}
			}
		}
		);
		gbc.gridx=1;
		gbc.gridy=2;
		jr2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)	
			{
				if(ae.getSource()==jr2)
				{
					try{
						getDisConnected();
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
		gbc.gridy=3;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridwidth=3;
		jp.add(jta,gbc);	

		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridx=0;
		gbc.gridy=4;
		jp.add(jt3,gbc);
		
		gbc.gridx=3;
		gbc.gridy=4;
		jb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)	
			{
				if(ae.getSource()==jb)
				{
					try{
						sending();
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
	public void getConnected() throws Exception
	{
		sk=new Socket(jt1.getText(),Integer.parseInt(jt2.getText()));
		jta.setText("Connecting to Server\n");
		in=new DataInputStream(sk.getInputStream());
		out=new DataOutputStream(sk.getOutputStream());
		jta.append("\nI/O streams established\n");
	}
	public void sending()throws Exception
	{
		String text="";
		text=jt3.getText();

		jta.append("\n\nstart generating key");				
		KeyGenerator k=KeyGenerator.getInstance("HmacMD5");
		SecretKey key=k.generateKey();
		jta.append("\nfinish generating key");
		jta.append("\nkey is "+key);

		jta.append("\nstarted generating mac value");
		Mac mac=Mac.getInstance("HmacMD5");
		mac.init(key);

		out.writeUTF(text);
		out.flush();
		jta.append("\nsent the text to receiver");

		jta.append("\noriginal key is "+key);
		String encodedKey=Base64.getEncoder().encodeToString(key.getEncoded());
		out.writeUTF(encodedKey);
		out.flush();
		jta.append("\nkey has been sent in encoded format");
	
		jta.append("\nmessage to be sent is "+text);		
		byte[] plaintext=text.getBytes();
		String clientMac=new String(mac.doFinal(plaintext),"UTF8");		
		jta.append("\nsending the mac value "+clientMac);
		out.writeUTF(clientMac);
		out.flush();

	}

	public void getDisConnected() throws Exception
	{
		sk.close();
		jta.setText("\nclient disconnected\n");
		in.close();
		out.close();
	}
	public static void main(String args[])throws Exception
	{
		Client er=new Client();		
		er.setTitle("Client");
		er.setSize(500,500);
		er.setVisible(true);
	}
}
