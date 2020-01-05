package org.property;

import java.util.Properties;
import org.property.*;


import org.file.FileIO;
public class ServerProperty {
	public String sqlIp;
	public int port;
	public String userName;
	public String password;
	public String url;
	public String cfgPath;
	public String USER;
	public String HOST;
	public int DEFAULT_SSH_PORT;
	public String PASSWORD;
	public void GetProperty() {
		Properties p = PropertyLoader.ReadProperty("/property/server.properties");
		sqlIp = p.getProperty("sqlIp", "127.0.0.1");
		port = Integer.parseInt(p.getProperty("port", "3306"));
		userName = p.getProperty("userName","root");
		password = p.getProperty("password","xxd123456");
		cfgPath = p.getProperty("cfgPath","/etc/ssh/sshd_config");
		USER=p.getProperty("USER");
		HOST=p.getProperty("HOST");
		DEFAULT_SSH_PORT=Integer.parseInt(p.getProperty("DEFAULT_SSH_PORT"));
		PASSWORD=p.getProperty("PASSWORD");
		
		
		url = String.format("jdbc:mysql://%s:%d/distributefiles?serverTimezone=GMT", sqlIp,port);
	}
	
	@Override
	public String toString() {
		String output = String.format("sqlIp:%s\nport:%d\nuserName:%s\npassword:%s\n", sqlIp,port,userName,password);
		return output;
	}
	
	public static void main(String[] args) {
		ServerProperty s = new ServerProperty();
		s.GetProperty();
		FileIO.FileAppend(s.cfgPath,"asdqwe");
		
		System.out.println(s);
	}
}
