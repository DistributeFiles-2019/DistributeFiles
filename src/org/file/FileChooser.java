package org.file;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
public class FileChooser {
	public static  File ChooseFile() {
		JFileChooser fd = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("id files(*.pub)", "pub");
		fd.setFileFilter(filter);
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.setMultiSelectionEnabled(true);
		fd.showOpenDialog(null);
		File f = fd.getSelectedFile();
		return f;
	}
	
	public static File ChooseDirectory() {
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fd.setMultiSelectionEnabled(false);
		fd.showOpenDialog(null);
		File f = fd.getSelectedFile();
		return f;
	}
	
	public static void main(String[] args) {
		File f = FileChooser.ChooseDirectory();
		System.out.println(f);
	}
}
