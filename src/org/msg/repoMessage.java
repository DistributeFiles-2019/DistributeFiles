package org.msg;
import java.util.Map;

import org.util.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class repoMessage extends Object implements Serializable{
	public MessageID messageId = MessageID.NULL;
	public ArrayList<Repository> repolist = null;
	public repoMessage() {
		
	}
	public enum MessageID{
		NULL,RESPONSE
  	}
}