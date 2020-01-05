package org.util;
import java.io.Serializable;
import java.sql.*;
public class Repository implements Serializable{
	public String remoteDir;
	public String currentDir;
	public boolean isRemote;
	
	
	
	public void getDir(ResultSet rs) throws SQLException {
		this.currentDir = rs.getString("currentDir");
		this.remoteDir = rs.getString("remoteDir");
		this.isRemote = rs.getBoolean("isRemote");
	}
	
	
	@Override
	public String toString() {
		String output = String.format("currentDir:%s,remoteDir:%s,isRemote:%B",  this.currentDir, this.remoteDir, this.isRemote);		
		return output;		
	}
}
