package org.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

import org.KeepAlive;
import org.gui.Main;
import org.util.*;
import org.msg.*;
import org.property.ClientProperty;
import org.property.ServerProperty;
import org.server.Server.InfoObjectAction;
public class Client extends Thread {
	private String serverIp = "127.0.0.1";
	private ClientProperty clientProperty = null;
	private int port = 12345;
	private Socket socket = null;
	private boolean running = false;
	private ConcurrentHashMap<Class, ObjectAction> actionMapping = new ConcurrentHashMap<Class, ObjectAction>();
	private Scanner sc;
	private int id = (new Random()).nextInt(1000000);
	private String user;
	private String ID = "";
	public Main main = null;

	public static interface ObjectAction {
		void doAction(Object obj, Client client, Main main);
	}

	public static final class DefaultObjectAction implements ObjectAction {
		public void doAction(Object obj, Client client, Main main) {
			System.out.println("Process:\t" + obj.toString());
		}
	}
	
	public static final class InfoObjectAction implements ObjectAction {
		public void doAction(Object obj, Client client, Main main) {
			System.out.println("Process:\t" + obj.toString());
			Message msg = (Message) obj;
			System.out.println(msg.info);
			if(msg.messageId == Message.MessageID.LOGINRESPONSE) {
				try {
					main.writeLog(msg.info);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(msg.sc == 1) {
					main.changeStatus();
					JOptionPane.showMessageDialog(main, "登录成功", "提示消息",JOptionPane.PLAIN_MESSAGE);
				}else if(msg.sc == 2) {
					//main.changeStatus();
					JOptionPane.showMessageDialog(main, "登录失败", "提示消息",JOptionPane.WARNING_MESSAGE);
				}else if(msg.sc == 3) {
					JOptionPane.showMessageDialog(main, "已经在线", "提示消息",JOptionPane.WARNING_MESSAGE);
				}else {
				}
			}else if(msg.messageId == Message.MessageID.LOGOUTRESPONSE) {
				try {
					main.writeLog(msg.info);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(msg.sc == 1) {
					JOptionPane.showMessageDialog(main, "注销成功", "提示消息",JOptionPane.PLAIN_MESSAGE);
					main.changeStatus();
				}else if(msg.sc == 2) {
					main.changeStatus();
					JOptionPane.showMessageDialog(main, "注销失败", "提示消息",JOptionPane.WARNING_MESSAGE);
				}else {
				}
			}else if(msg.messageId == Message.MessageID.NEWREPORESPONSE) {
				try {
					main.writeLog(msg.info);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(msg.sc == 1) {
					JOptionPane.showMessageDialog(main, "创建成功", "提示消息",JOptionPane.PLAIN_MESSAGE);
				}else if(msg.sc == 2) {
					JOptionPane.showMessageDialog(main, "创建失败", "提示消息",JOptionPane.WARNING_MESSAGE);
				}else {
				}
			}else if(msg.messageId == Message.MessageID.UPLOADPUBRESPONSE) {
				try {
					main.writeLog(msg.info);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(msg.sc == 1) {
					JOptionPane.showMessageDialog(main, "上传成功", "提示消息",JOptionPane.PLAIN_MESSAGE);
				}else if(msg.sc == 2) {
					JOptionPane.showMessageDialog(main, "上传失败", "提示消息",JOptionPane.WARNING_MESSAGE);
				}else {
				}
			}else if(msg.messageId == Message.MessageID.CONNECTREMOTERESPONSE) {
				try {
					main.writeLog(msg.info);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(msg.sc == 1) {
					JOptionPane.showMessageDialog(main, "连接成功", "提示消息",JOptionPane.PLAIN_MESSAGE);
				}else {
				}
			}else if(msg.messageId == Message.MessageID.MANUALUPDATERESPONSE) {
				try {
					main.writeLog(msg.info);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(msg.sc == 1) {
					JOptionPane.showMessageDialog(main, "同步成功", "提示消息",JOptionPane.PLAIN_MESSAGE);
				}else {
				}
			}
		}
	}
	
	public static final class RepoObjectAction implements ObjectAction {
		public void doAction(Object obj, Client client, Main main) {
			repoMessage msg = (repoMessage) obj;
			System.out.println("Process:\t" + obj.toString());
			if(msg.messageId == repoMessage.MessageID.RESPONSE) {
				System.out.println(msg.repolist);
				main.repodialog.refreshRepo(msg.repolist);
			}
		}
	}

	public void addActionMap(Class<?> cls, ObjectAction action) {
		actionMapping.put(cls, action);
	}

	public void sendObject(Object obj) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(obj);
		System.out.println("send:\t" + obj);
		oos.flush();
	}
	
	public int getID() {
		return id;
	}

	public static void main(String[] args) throws IOException {
		Client client = new Client();
		client.start();
	}

	public Client() {
		sc = new Scanner(System.in);
		main = new Main(this);
		this.clientProperty = new ClientProperty();
		this.clientProperty.GetProperty();
		connect();
		this.addActionMap(Message.class, new InfoObjectAction());
		this.addActionMap(repoMessage.class, new RepoObjectAction());
		//run();
	}

	private void connect() {
		try {
			// 需要服务器的IP地址和端口号，才能获得正确的Socket对象
			socket = new Socket(serverIp, port);
			try {
				InputStream s = socket.getInputStream();
				byte[] buf = new byte[1024];
				int len = s.read(buf);
				socket = new Socket(serverIp, Integer.parseInt(new String(buf, 0, len)));
				System.out.println(
						String.format("Connect to %s:%d", serverIp, Integer.parseInt(new String(buf, 0, len))));
				this.running = true;
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("IO Error while reading message.");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Unknown exception found. Failed to connect to server.");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to connect to server.");
		}
	}
	
	private void quit() {
		
	}

	class KeepAliveWatchDog implements Runnable {
		long checkDelay = 10;
		long keepAliveDelay = 2000;
		long lastSendTime = System.currentTimeMillis();

		public void run() {
			while (running) {
				if (System.currentTimeMillis() - lastSendTime > keepAliveDelay) {
					try {
						Client.this.sendObject(new KeepAlive());
					} catch (IOException e) {
						e.printStackTrace();
						Client.this.stop();
					}
					lastSendTime = System.currentTimeMillis();
				} else {
					try {
						Thread.sleep(checkDelay);
					} catch (InterruptedException e) {
						e.printStackTrace();
						Client.this.stop();
					}
				}
			}
		}

	}

	class ReceiveWatchDog implements Runnable {
		public void run() {
			while (running) {
				try {
					InputStream in = socket.getInputStream();
					if (in.available() > 0) {
						ObjectInputStream ois = new ObjectInputStream(in);
						Object obj = ois.readObject();
						System.out.println("Receive:\t" + obj);
						ObjectAction oa = actionMapping.get(obj.getClass());
						oa = oa == null ? new DefaultObjectAction() : oa;
						oa.doAction(obj, Client.this, Client.this.main);
					} else {
						Thread.sleep(10);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Client.this.stop();
				}
			}
		}
	}

	@Override
	public void run() {

		new Thread(new KeepAliveWatchDog()).start();  
		new Thread(new ReceiveWatchDog()).start();
		
		Scanner scanner = new Scanner(System.in);
		String in = "";

		do {
			String output = "";
			System.out.println(">:");
			in = scanner.nextLine();
			Message msg = new Message();
//			if (in.equals("login")) {
//				System.out.println("UserName:");
//				String usr = sc.nextLine();
//				System.out.println("Password:");
//				String pwd = sc.nextLine();
//				String pwd_enc = CryptoUtil.encode(pwd);
//				msg.messageId = Message.MessageID.LOGIN;
//				msg.Put("username", usr);
//				msg.Put("password", pwd_enc);
//			}
//			else if (in.equals("logout")) {
//				msg.messageId = Message.MessageID.LOGOUT;
//			}
//			else if (in.equals("repo")) {
//				msg.messageId = Message.MessageID.REPO;
//			}
//			else if (in.equals("push")) {
//				System.out.println("Local repo path:");
//				String local = sc.nextLine();
//				System.out.println("Remote repo path:");
//				String remote = sc.nextLine();
//				msg.messageId = Message.MessageID.PUSH;
//				msg.Put("local", local);
//				msg.Put("remote", remote);
//			}
//			else if (in.equals("pull")) {
//				System.out.println("Local repo path:");
//				String local = sc.nextLine();
//				System.out.println("Remote repo path:");
//				String remote = sc.nextLine();
//				msg.messageId = Message.MessageID.PULL;
//				msg.Put("local", local);
//				msg.Put("remote", remote);
//			}
//			else if (in.equals("update")) {
//				System.out.println("Local repo path:");
//				String local = sc.nextLine();
//				System.out.println("Remote repo path:");
//				String remote = sc.nextLine();
//				msg.messageId = Message.MessageID.UPDATE;
//				msg.Put("local", local);
//				msg.Put("remote", remote);
//			}
//			else {
//				output = in;
//				if (in.equals("")) {
//
//				}
//			}
			try {
				this.sendObject(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (!in.equals("bye"));
		/*if (socket == null || !socket.isConnected()) {
			System.out.println("Socket is null or has not been connected");
			return;
		}
		new SendMessageThread().start();
		super.run();
		try {
			InputStream s = socket.getInputStream();
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = s.read(buf)) != -1) {
				System.out.println(new String(buf, 0, len));
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO Error while reading message.");
		}*/
	}

	class SendMessageThread extends Thread {
		String content;

		public void setText(String s) {
			this.content = s;
		}

		public String getText() {
			return this.content;
		}

		@Override
		public void run() {
			super.run();
			// 写操作
			Scanner scanner = null;
			OutputStream os = null;
			try {
				scanner = new Scanner(System.in);
				os = socket.getOutputStream();
				String in = "";

				do {
					String output = "";
					System.out.println(">:");
					in = scanner.nextLine();
					if (in.equals("login")) {
						System.out.println("UserName:");
						String usr = sc.nextLine();
						System.out.println("Password:");
						String pwd = sc.nextLine();
						String pwd_enc = CryptoUtil.encode(pwd);
						output = String.format("login %s %s", usr, pwd_enc);
					} else {
						output = in;
						if (in.equals("")) {

						}
					}
					os.write(("" + output).getBytes());
					os.flush();
				} while (!in.equals("bye"));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Send Meg Failed");
			}

		}
	}
}
