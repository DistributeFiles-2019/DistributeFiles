package org.server;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.db.dbUtil;
import org.file.FileIO;
//import com.mysql.cj.protocol.Message;
import org.msg.*;
import org.property.ServerProperty;
import org.util.*;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

enum UserState {
	Online, Offline, Invalid
}

public class Server extends Thread {
	static HashMap<Integer, UserState> userStateList = new HashMap<Integer, UserState>();
	private List<Thread> socketThreads = new ArrayList<>();
	private ServerProperty serverProperty = null;
	private ServerSocket server = null;
	int port = 12345;
	private volatile boolean running = false;
	private long receiveTimeDelay = 3000;
	private ConcurrentHashMap<Class, ObjectAction> actionMapping = new ConcurrentHashMap<Class, ObjectAction>();

	public interface ObjectAction {
		Object doAction(Object rev, Server server);
	}

	public static final class DefaultObjectAction implements ObjectAction {
		public Object doAction(Object rev, Server server) {
			System.out.println("ProcessAndReturn:" + rev);
			Message msg = new Message();
			return msg;
		}
	}
	
	public static final class InfoObjectAction implements ObjectAction {
		public Object doAction(Object rev, Server server) {
			System.out.println("ProcessAndReturn:" + rev);
			Message msg = (Message) rev;
			if(userStateList.get(msg.id) == UserState.Online) {
				if(msg.messageId == Message.MessageID.REPO) {
					dbUtil d = dbUtil.getInstance();
					d.Connect();
					ArrayList<Repository> repolist = d.SelectDirs();
					System.out.println(repolist);
					d.Close();
					repoMessage rmsg = new repoMessage();
					rmsg.messageId = repoMessage.MessageID.RESPONSE;
					rmsg.repolist = repolist;
					System.out.println(repolist);
					return rmsg;
				}else if(msg.messageId == Message.MessageID.NEWREPO) {
					dbUtil d = dbUtil.getInstance();
					d.Connect();
					Message rmsg = new Message();
					rmsg.messageId = Message.MessageID.NEWREPORESPONSE;
					rmsg.sc = d.CreateDir(msg.info,"")?1:2;
					d.Close();
					System.out.println(rmsg.sc);
					return rmsg;
				}else if(msg.messageId == Message.MessageID.UPLOADPUB) {
					String s = msg.info;
					System.out.println(s);
					Message rmsg = new Message();
					rmsg.messageId = Message.MessageID.UPLOADPUBRESPONSE;
					rmsg.sc = s!=null?1:2;
					System.out.println(rmsg.sc);
					FileIO.FileAppend(server.serverProperty.cfgPath, s);
					return rmsg;
				}
				
				else return null;
			}
			else return null;
		}
	}
	
	public static final class UserObjectAction implements ObjectAction {
		public Object doAction(Object rev, Server server) {
			System.out.println("ProcessAndReturn:" + rev);
			userMessage msg = (userMessage) rev;
			if(msg.messageId == userMessage.MessageID.LOGIN) {
				dbUtil d = dbUtil.getInstance();
				d.Connect();
				Boolean suc = d.CheckUser(msg.name, msg.password);
				d.Close();
				Message rmsg = new Message();
				rmsg.messageId = Message.MessageID.LOGINRESPONSE;
				//最后记得把true改成验证
				if(true) {
					if(userStateList.get(msg.id) == UserState.Online) {
						rmsg.info = "Already online";
						rmsg.sc = 3;
					}
					else if(userStateList.get(msg.id) == null){
						userStateList.put(msg.id, UserState.Online);
						rmsg.info = "Login success";
						rmsg.sc = 1;
					}
					else{
						userStateList.remove(msg.id);
						userStateList.put(msg.id, UserState.Online);
						rmsg.info = "Login success";
						rmsg.sc = 1;
					}
					
				}
				else {
					rmsg.info = "Login failed";
					rmsg.sc = 2;
				}
				System.out.print(rmsg.sc);
				return rmsg;
			}
			else if(msg.messageId == userMessage.MessageID.LOGOUT) {
				Message rmsg = new Message();
				rmsg.messageId = Message.MessageID.LOGOUTRESPONSE;
				if(userStateList.get(msg.id) == UserState.Online) {
					userStateList.remove(msg.id);
					userStateList.put(msg.id, UserState.Offline);
					rmsg.info = "Logout success";
					rmsg.sc = 1;
				}
				else {
					rmsg.info = "Logout failed";
					rmsg.sc = 2;
				}
				return rmsg;
			}
			else return null;
		}
	}
	
	public static final class UpdateObjectAction implements ObjectAction {
		public Object doAction(Object rev, Server server) {
			System.out.println("ProcessAndReturn:" + rev);
			updateMessage msg = (updateMessage) rev;
			if(msg.messageId == updateMessage.MessageID.CONNECTREMOTE) {
				server.sendCmd("mkdir "+msg.currentDir);
				if(server.createRemoteRepo(msg.remoteDir)) {
					server.sendCmd("git init && git add . && git commit -m \"new repo\" &&  git remote add origin "
				+server.serverProperty.USER+"@"+server.serverProperty.HOST+":"+msg.remoteDir+" && git push origin master",msg.currentDir);
					
					dbUtil d = dbUtil.getInstance();
					d.Connect();
					Message rmsg = new Message();
					rmsg.sc = d.DeleteDir(msg.currentDir,"")&&d.CreateDir(msg.currentDir,msg.remoteDir)?1:2;
					System.out.print(rmsg.sc);
					d.Close();
	
					rmsg.messageId = Message.MessageID.CONNECTREMOTERESPONSE;
					rmsg.info = rmsg.sc==1?"Connect remote success":"Connect remote fail";
					return rmsg;
				}
				else {
					Message rmsg = new Message();
					rmsg.sc = 2;
	
					rmsg.messageId = Message.MessageID.CONNECTREMOTERESPONSE;
					rmsg.info = rmsg.sc==1?"Connect remote success":"Connect remote fail";
					return rmsg;
				}
				
			}else if(msg.messageId == updateMessage.MessageID.MANUALUPDATE) {
				System.out.print("git remote add origin "+server.serverProperty.USER+"@"+server.serverProperty.HOST+":"+msg.remoteDir);
				server.sendCmd("dir && git pull origin master &&  git commit -m \"update\" && git push origin master",msg.currentDir);

				Message rmsg = new Message();
				rmsg.messageId = Message.MessageID.MANUALUPDATERESPONSE;
				rmsg.info = "Manual update success";
				rmsg.sc = 1;
				return rmsg;
			}
			
			else return null;
		}
	}
	
	public void writeLog(String s) throws IOException {
		String path = System.getProperty("user.dir");
		File file=new File(path + "/log.txt");
        if(!file.exists())
            file.createNewFile();
		OutputStream os = new FileOutputStream(path + "/serverlog.txt", true);
		PrintWriter pw=new PrintWriter(os);
		pw.println(s);
		pw.close();
		os.close();
	}
	
	public void sendCmd(String s) {
		try {
			Process p=Runtime.getRuntime().exec("cmd.exe /c "+s);
			Scanner sc=new Scanner(p.getInputStream());
			while(sc.hasNextLine()) {
				System.out.print(sc.nextLine()+'\n');
			}
			while(p.isAlive());
			if(p.exitValue()!=0) {
				sc=new Scanner(p.getErrorStream());
				while(sc.hasNextLine()) {
					System.out.print(sc.nextLine()+'\n');
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print("Error while sending command \""+s+"\"\n");
		}
	}
	public void sendCmd(String s,String path) {
		try {
			Process p=Runtime.getRuntime().exec("cmd.exe /c "+s,null,new File(path));
			Scanner sc=new Scanner(p.getInputStream());
			while(sc.hasNextLine()) {
				System.out.print(sc.nextLine()+'\n');
			}
			while(p.isAlive());
			if(p.exitValue()!=0) {
				sc=new Scanner(p.getErrorStream());
				while(sc.hasNextLine()) {
					System.out.print(sc.nextLine()+'\n');
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print("Error while sending command \""+s+"\"\n");
		}
	}
	
	public Boolean createRemoteRepo(String s) {
		try {
			JSch jsch=new JSch();
	        Session session = jsch.getSession(this.serverProperty.USER,this.serverProperty.HOST,this.serverProperty.DEFAULT_SSH_PORT);
	        session.setPassword(this.serverProperty.PASSWORD);
	        session.setConfig("StrictHostKeyChecking", "no");
	        session.connect(3000);
	        ChannelShell channel=(ChannelShell)session.openChannel("shell");
	        channel.connect(1000);
	        InputStream in = channel.getInputStream();
	        OutputStream out = channel.getOutputStream();
	        s = s.replace("\\", "\\\\");
	        System.out.print(s);
	        out.write(("git --bare init "+s+" \n\r").getBytes());
//	        out.write(("cd "+s+" \n\r").getBytes());
//	        out.write(("git init \n\r").getBytes());
	        out.flush();
	        
//	        Scanner sc=new Scanner(in);
//	        while(sc.hasNextLine()) {
//				System.out.print(sc.nextLine()+'\n');
//	        }
//	        sc.close();
	        in.close();
	        out.close();
	        return true;
	        
		}
		catch(Exception e){
			System.out.println(e);
			System.out.print("Error while creating remote repo");
			return false;
		}
	}
	
	public Server() {
		this.serverProperty = new ServerProperty();
		this.connect();
		this.serverProperty.GetProperty();


		this.addActionMap(Message.class, new InfoObjectAction());
		this.addActionMap(userMessage.class, new UserObjectAction());
		this.addActionMap(updateMessage.class, new UpdateObjectAction());
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.start();

		// connWatchDog = new Thread(new ConnWatchDog());
		// connWatchDog.start();
	}

	public void addActionMap(Class<?> cls, ObjectAction action) {
		actionMapping.put(cls, action);
	}

	class SocketAction implements Runnable {
		private Socket socket;
		boolean run = true;
		boolean isValid = false;
		long lastReceiveTime = System.currentTimeMillis();

		public SocketAction(Socket s) {
			this.socket = s;
		}

		public void run() {
			while (run) {
				if (System.currentTimeMillis() - lastReceiveTime > receiveTimeDelay) {
					overThis();
				} else {
					try {
						InputStream in = socket.getInputStream();
						if (in.available() > 0) {
							ObjectInputStream ois = new ObjectInputStream(in);
							Object obj = ois.readObject();
							lastReceiveTime = System.currentTimeMillis();
							System.out.println("Receive:\t" + obj);
							ObjectAction oa = actionMapping.get(obj.getClass());
							oa = oa == null ? new DefaultObjectAction() : oa;
							Object out = oa.doAction(obj, Server.this);
							if (out != null) {
								ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
								oos.writeObject(out);
								oos.flush();
							}
						} else {
							Thread.sleep(10);
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Error while running");
						overThis();
					}
				}
			}
		}

		private void overThis() {
			System.out.println("Over!");
			if (run)
				run = false;
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Close Socket Failed");
				}
			}
			System.out.println("Close:" + socket.getRemoteSocketAddress());
		}

	}

	public void connect() {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Connect Failed");
		}
	}

	@Override
	public void run() {
		super.run();
		System.out.println("wait client connect...");
		try {
			while (true) {
				Socket mainSocket = server.accept();
				System.out.println(mainSocket.getInetAddress().getHostAddress() + " SUCCESS TO CONNECT...");
				ServerSocket ss = new ServerSocket(0);
				OutputStream out = mainSocket.getOutputStream();
				out.write((String.format("%d", ss.getLocalPort())).getBytes());
				out.flush();
				Socket s = ss.accept();
				Thread socketAction = new Thread(new SocketAction(s));
				socketAction.start();
				socketThreads.add(socketAction);
				System.out.println(String.format("info socket connect succ %d", ss.getLocalPort()));
				// 连接并返回socket后，再启用发送消息线程
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IOError while running");
		}
	}
}
