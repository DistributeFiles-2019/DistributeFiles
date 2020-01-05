package org.msg;
import java.util.Map;
import java.io.Serializable;
import java.util.HashMap;


public class updateMessage extends Object implements Serializable{
	public MessageID messageId = MessageID.NULL;
	public String currentDir = "";
	public String remoteDir = "";
	public Boolean isRemote = false;
	public updateMessage() {
		
	}
	public enum MessageID{
		NULL,CONNECTREMOTE,MANUALUPDATE
  	}
}