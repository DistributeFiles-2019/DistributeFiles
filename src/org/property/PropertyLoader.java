package org.property;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

public class PropertyLoader {
	public static Properties ReadProperty(String path) {
		Properties pro = new Properties();
		InputStream inputStream = Object.class.getResourceAsStream(path);
		try {
			pro.load(inputStream);
			return pro;
		}
		catch (IOException e){
			e.printStackTrace();
			return null;
		}
	}

	public static void main() {
		
	}
}
