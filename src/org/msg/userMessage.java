package org.msg;
import java.util.Map;

import org.util.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class userMessage extends Object implements Serializable{
	public MessageID messageId = MessageID.NULL;
	public int id = -1;
	public String name = "";
	public String password = "";
	public userMessage() {
		
	}
	public enum MessageID{
		NULL,LOGIN,LOGOUT
  	}
}