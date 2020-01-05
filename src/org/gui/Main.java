package org.gui;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.client.Client;
import org.file.FileChooser;
import org.msg.*;
import org.util.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.gui.repoDialog;



public class Main extends JFrame {
	String log = "";
	int status = 0;
	Client client;
	JTextArea logText = new JTextArea();
	public repoDialog repodialog = null;
	public JPanel loginpanel = null;
	public JPanel logoutpanel = null;
	String path = System.getProperty("user.dir");
	public Main(Client c) {
		this.client = c;
		new Thread(this.client).start();
		this.setSize(500, 300);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		try {
			repodialog = new repoDialog(Main.this);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		loginpanel = new JPanel();
		loginpanel.setBounds(0, 100, 200, 100);
		loginpanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,5));
		
		JLabel nameLabel = new JLabel("用户名：");
		nameLabel.setSize(50, 20);
		loginpanel.add(nameLabel);
		
		JTextField nameText = new JTextField("", 10);
		nameText.setSize(100, 21);
		loginpanel.add(nameText);

		JLabel passwordLabel = new JLabel("密码：    ");
		passwordLabel.setSize(50, 20);
		loginpanel.add(passwordLabel);

		JPasswordField passwordField = new JPasswordField("", 10);
		passwordField.setSize(100, 21);
		loginpanel.add(passwordField);

		JButton loginButton = new JButton("登录");
		loginButton.setBounds(20, 80, 80, 20);
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userMessage msg = new userMessage();
				msg.messageId = userMessage.MessageID.LOGIN;
				msg.id = client.getID();
				msg.name = nameText.getText();
				msg.password = new String(passwordField.getPassword());
				try {
					client.sendObject(msg);
					writeLog("Try to login with id="+msg.id+",name="+msg.name+",password="+msg.password);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		loginpanel.add(loginButton);
		
		this.add(loginpanel);
		loginpanel.setVisible(true);
		
		logoutpanel = new JPanel();
		logoutpanel.setBounds(0, 100, 200, 100);
		JButton logoutButton = new JButton("注销");
		logoutButton.setBounds(20, 80, 80, 20);
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userMessage msg = new userMessage();
				msg.messageId = userMessage.MessageID.LOGOUT;
				msg.id = client.getID();
				try {
					client.sendObject(msg);
					writeLog("Try to logout with id="+msg.id);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		logoutpanel.add(logoutButton);
		logoutpanel.setVisible(false);
		
		this.add(logoutpanel);
		
		JButton uploadpubButton = new JButton("上传pub");
		uploadpubButton.setBounds(60, 10, 100, 20);
		uploadpubButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File f = FileChooser.ChooseFile();
				Message msg = new Message();
				msg.messageId = Message.MessageID.UPLOADPUB;
				msg.id = Main.this.client.getID();
				if(f != null) {
					BufferedReader br;
					try {
						br = new BufferedReader(new FileReader(f));
						String info="";
						String tmp="";
						while((tmp=br.readLine())!=null) {
							info += tmp+"\n";
						}
						msg.info = info;
						
					} catch (FileNotFoundException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						Main.this.client.sendObject(msg);
						System.out.println(msg.info);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
		});
		this.add(uploadpubButton);
		JButton repoButton = new JButton("仓库");
		repoButton.setBounds(60, 40, 100, 20);
		repoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repodialog.setVisible(true);
			}
		});
		this.add(repoButton);
		JButton clearButton = new JButton("清空");
		clearButton.setBounds(230, 220, 80, 20);
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				log = "";
				logText.setText(log);
			}
		});
		this.add(clearButton);
		JButton logButton = new JButton("日志");
		logButton.setBounds(330, 220, 80, 20);
		logButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					File file=new File(path + "/log.txt");
			        if(!file.exists())
			            file.createNewFile();
					Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler file://" + path + "/log.txt");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		this.add(logButton);
		
		this.logText.setBounds(200, 10, 250, 200);
		this.add(logText);
		
		systemTray();
		this.addWindowListener(new WindowAdapter() {
			 @Override
			 public void windowIconified(WindowEvent e) {
				 Main.this.setVisible(false);
			 }
		});
		
		this.setVisible(true);
	}
	
	public void changeStatus() {
		if(status == 0) {
			status = 1;
			loginpanel.setVisible(false);
			logoutpanel.setVisible(true);
		}else if(status == 1) {
			status = 0;
			loginpanel.setVisible(true);
			logoutpanel.setVisible(false);
		}
	}

	private void systemTray() {
		if (SystemTray.isSupported()) {
			
			PopupMenu popupMenu = new PopupMenu();
			MenuItem itemExit = new MenuItem("Exit");
			itemExit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			popupMenu.add(itemExit);
			ImageIcon icon = new ImageIcon("icon.png");
			TrayIcon trayIcon = new TrayIcon(icon.getImage(), "test", popupMenu);
			trayIcon.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Main.this.setVisible(true);
				}
			});
   
			try {
				SystemTray.getSystemTray().add(trayIcon);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void writeLog(String s) throws IOException {
		log += s;
		log += '\n';
		this.logText.setText(log);
		File file=new File(path + "/log.txt");
        if(!file.exists())
            file.createNewFile();
		OutputStream os = new FileOutputStream(path + "/log.txt", true);
		PrintWriter pw=new PrintWriter(os);
		pw.println(s);
		pw.close();
		os.close();
	}
 
	public static void main(String[] args) {
	}
}