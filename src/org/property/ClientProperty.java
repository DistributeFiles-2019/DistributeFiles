package org.property;

import java.util.Properties;
import org.property.*;
public class ClientProperty {
	public String sqlIp;
	public int port;
	public String userName;
	public String password;
	public String url;
	
	public void GetProperty() {
		Properties p = PropertyLoader.ReadProperty("/property/client.properties");
		sqlIp = (String)p.getProperty("sqlIp", "127.0.0.1");
		port = Integer.parseInt(p.getProperty("port", "3306"));
		userName = p.getProperty("userName","root");
		password = p.getProperty("password","xxd123456");
		
		url = String.format("jdbc:mysql://%s:%d/distributefiles?serverTimezone=GMT", sqlIp,port);
		
	}
}
