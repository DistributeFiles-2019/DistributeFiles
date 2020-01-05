package org.msg;
import java.util.Map;
import java.io.Serializable;
import java.util.HashMap;


public class Message extends Object implements Serializable{
	public MessageID messageId = MessageID.NULL;
	public int id = -1;
	public int sc = -1;
	public String info = "";
	public Message() {
		
	}
	public enum MessageID{
		NULL,LOGIN,LOGOUT,LOGINRESPONSE,LOGOUTRESPONSE,NEWREPORESPONSE,UPLOADPUBRESPONSE,REPO,NEWREPO,PUSH,PULL,UPDATE,UPLOADPUB,CONNECTREMOTERESPONSE,MANUALUPDATERESPONSE
  	}
}
