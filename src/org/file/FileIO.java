package org.file;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;

public class FileIO {
	public static String FileRead(String name) {
		File f = new File(name);
		if (f != null) {
			FileIO.FileRead(f);
		}
		return new String("");
	}

	public static String FileRead(File f) {
		String s = "";
		InputStream is = null;
		Reader reader = null;
		BufferedReader bufferedReader = null;
		try {
			is = new FileInputStream(f);
			reader = new InputStreamReader(is);
			bufferedReader = new BufferedReader(reader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				s += line;
			}
			return s;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != bufferedReader)
					bufferedReader.close();
				if (null != reader)
					reader.close();
				if (null != is)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new String("");
	}

	public static void FileWrite(File f, String content) {
		FileWriter fw = null;
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			fw = new FileWriter(f);
			fw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void FileWrite(String name, String content) {
		File f = new File(name);
		if (f != null) {
			FileIO.FileWrite(f, content);
		}
	}

	public static void FileAppend(String name, String content) {
		File f = new File(name);
		if (f != null) {
			FileIO.FileAppend(f, content);
		}
	}

	public static void FileAppend(File f, String content) {
		FileWriter fw = null;
		try {
			// 如果文件存在，则追加内容；如果文件不存在，则创建文件
			fw = new FileWriter(f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(fw);
		pw.println(content);
		pw.flush();
		try {
			fw.flush();
			pw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
