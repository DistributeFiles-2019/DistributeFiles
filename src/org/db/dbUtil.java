package org.db;

import java.io.File;
import java.sql.*;

import org.file.FileChooser;
import org.util.*;

import java.util.ArrayList;
public class dbUtil {
	
	private Connection conn = null;
	
	
	public void Connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url="jdbc:mysql://localhost:3306/distributefiles?serverTimezone=GMT";
			String user = "root";
			String password = "xxd123456";
			conn = DriverManager.getConnection(url,user,password);
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	public void Close() {
		try {
			conn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			
		}
		finally {
			conn = null;
		}
	}
	
	public boolean CheckConnect() {
		if (conn != null) {
			try {
				return !conn.isClosed();
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return false;
	}
	
	public boolean CreateUser(String name,String password) {
		try {
			String sql = String.format("insert into user(name,password) values(\"%s\",\"%s\")", name,CryptoUtil.encode(password));
			Statement s = conn.createStatement();
			s.executeUpdate(sql);
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public boolean DeleteUser(String name,String password) {
		try {
			String sql = String.format("delete from user where name = \"%s\" and password = \"%s\"", name,CryptoUtil.encode(password));
			Statement s = conn.createStatement();
			return s.executeUpdate(sql) > 0;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean DeleteAllUser() {
		try {
			String sql = String.format("delete from user");
			Statement s = conn.createStatement();
			return s.executeUpdate(sql) > 0;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public boolean CheckUser(String name,String password) {
		try {
			String sql = String.format("select * from user where name = \"%s\" and password = \"%s\"", name,CryptoUtil.encode(password));
			Statement s = conn.createStatement();
			ResultSet rset  = s.executeQuery(sql);
			System.out.println(rset.getRow());
			return rset.getRow() > 0;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean CreateDir(String currentDir, String remoteDir) {
		try {
			String sql = String.format("insert into repository(currentDir,remoteDir,isRemote) values(\"%s\",\"%s\",false)", currentDir, remoteDir);
			Statement s = conn.createStatement();
			s.executeUpdate(sql);
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean DeleteDir(String currentDir, String remoteDir) {
		try {
			String sql = String.format("delete from repository where currentDir = \"%s\" and remoteDir = \"%s\"", currentDir, remoteDir);
			Statement s = conn.createStatement();
			return s.executeUpdate(sql) > 0;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public ArrayList<Repository> SelectDirs() {
		try {
			ArrayList<Repository> arr = new ArrayList<Repository>();
			String sql = String.format("select isremote,remotedir,currentdir from repository");
//			Statement s = conn.createStatement();
			PreparedStatement s = conn.prepareStatement(sql);
			ResultSet rset  = s.executeQuery();
			//System.out.println(rset.getMetaData().getColumnCount());
			//System.out.println(rset.getRow());
			while (rset.next()) {
				Repository r = new Repository();
				r.getDir(rset);
				arr.add(r);
			}
			return arr;
		}
		catch (Exception e){
			e.printStackTrace();
			return new ArrayList<Repository>();
		}
	}
	
	public boolean DeleteAllRepo() {
		try {
			String sql = String.format("delete from repository");
			Statement s = conn.createStatement();
			return s.executeUpdate(sql) > 0;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	private static class SingletonHolder {
		private static final dbUtil INSTANCE = new dbUtil();
	}

	private dbUtil() {
	}

	public static final dbUtil getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public static void main(String[] args) {
			dbUtil d = dbUtil.getInstance();
			d.Connect();
			//File f = FileChooser.ChooseDirectory();
			//String s = f.getAbsolutePath().replace("\\", "\\\\");
			//d.CreateUser("cn", "cn");
			//d.CreateDir("test");
//			d.DeleteDir("c111","r222");
//			d.CreateDir("abc","ef");
			d.DeleteAllRepo();
			ArrayList<Repository> as = d.SelectDirs();
			System.out.println(as);
//			System.out.println(d.CheckUser("xxd", "xd"));
//			System.out.println(d.DeleteUser("xxd", "xd"));
			d.Close();
			System.out.println("finished");
	}
}
