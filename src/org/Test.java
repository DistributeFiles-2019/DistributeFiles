package org;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Test {
	private static String USER="ubuntu";
	private static String HOST="192.144.225.39";
	private static int DEFAULT_SSH_PORT=22;
	private static String PASSWORD="s3W:2zeQD+7rC#";
	
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
	
	public Test( ) {
		try{
				//sendCmd("mkdir D:\\javatestgit");
				//sendCmd("git init D:\\javatestgit");
			String s = "D:\\h1";
				sendCmd("cd "+s+" && dir && git pull origin master && git pull origin master && git commit -m \\\"update\\\" && git push origin master\"");
//				sendCmd("cd D:\\javatestgit");
//				sendCmd("git clone ubuntu@192.144.225.39:/tmp/test.git");
//		         JSch jsch=new JSch();
//		         Session session = jsch.getSession(USER,HOST,DEFAULT_SSH_PORT);
//		         session.setPassword(PASSWORD);
//		         session.setConfig("StrictHostKeyChecking", "no");
//		         session.connect();
//		         ChannelShell channel=(ChannelShell)session.openChannel("shell");
//		         channel.connect();
//		         InputStream in = channel.getInputStream();
//		         OutputStream out = channel.getOutputStream();
//		         
//		         out.write("git init /tmp/xxxxxxxx.git \n\r".getBytes());
//		         out.flush();
//		         
////		         Scanner sc=new Scanner(in);
////		         while(sc.hasNextLine()) {
////					System.out.print(sc.nextLine()+'\n');
////		         }
//		         in.close();
//		         out.close();
//			        System.out.print("done");
         
	 }
	 catch(Exception e){
         System.out.println(e);
     }
	}
	
	public static void main(String[] args) {
		new Test();
		System.out.print("finish");
	}
}
