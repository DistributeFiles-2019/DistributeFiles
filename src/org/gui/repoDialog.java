package org.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.file.FileChooser;
import org.msg.Message;
import org.msg.updateMessage;
import org.util.Repository;

public class repoDialog extends JDialog {
	String[] columnNames = { "本地仓库", "远程仓库", "是否远程" };
	String[][] tableValues = {};
	DefaultTableModel model = new DefaultTableModel(tableValues,columnNames);
	JTable table = new JTable(model);
	JScrollPane scrollPane = new JScrollPane(table){
		   @Override
		   public Dimension getPreferredSize() {
		     return new Dimension(560, 300);
		   }
		 };
		
	public repoDialog(Main frame) throws IOException {
		super(frame);
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JButton connectButton = new JButton("连接远程");
		connectButton.setBounds(60, 80, 80, 20);
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int row = table.getSelectedRow();
					frame.writeLog("Connect remote: "+table.getValueAt(row, 0)+" "+table.getValueAt(row, 1)+" "+table.getValueAt(row, 2));
					updateMessage msg = new updateMessage();
					msg.messageId = updateMessage.MessageID.CONNECTREMOTE;
					msg.currentDir = (""+table.getValueAt(row, 0)).replace("\\", "\\\\");
					msg.remoteDir = (""+table.getValueAt(row, 1)).replace("\\", "\\\\");
					msg.isRemote = ""+table.getValueAt(row, 2)=="远程";
					frame.client.sendObject(msg);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JButton updateButton = new JButton("手动同步");
		updateButton.setBounds(60, 80, 80, 20);
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int row = table.getSelectedRow();
					frame.writeLog("Manual update: "+table.getValueAt(row, 0)+" "+table.getValueAt(row, 1)+" "+table.getValueAt(row, 2));
					updateMessage msg = new updateMessage();
					msg.messageId = updateMessage.MessageID.MANUALUPDATE;
					msg.currentDir = (""+table.getValueAt(row, 0)).replace("\\", "\\\\");
					msg.remoteDir = (""+table.getValueAt(row, 1)).replace("\\", "\\\\");
					msg.isRemote = ""+table.getValueAt(row, 2)=="远程";
					frame.client.sendObject(msg);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JButton refreshButton = new JButton("刷新");
		refreshButton.setBounds(60, 80, 80, 20);
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendRefreshRequest(frame);
			}
		});
		JButton newrepoButton = new JButton("新建仓库");
		newrepoButton.setBounds(60, 80, 80, 20);
		newrepoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File f = FileChooser.ChooseDirectory();
				Message msg = new Message();
				msg.messageId = Message.MessageID.NEWREPO;
				msg.id = frame.client.getID();
				if(f != null) {
					msg.info = f.getAbsolutePath().replace("\\", "\\\\");
					System.out.println(msg.info);
					try {
						frame.writeLog("New repo: "+msg.info);
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					try {
						frame.client.sendObject(msg);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
		});
		this.setLayout(new FlowLayout(FlowLayout.LEFT,10,5));
		this.add(scrollPane);
		this.add(connectButton);
		this.add(updateButton);
		this.add(refreshButton);
		this.add(newrepoButton);
	}
	
	public void sendRefreshRequest(Main frame) {
		Message msg = new Message();
		msg.messageId = Message.MessageID.REPO;
		msg.id = frame.client.getID();
		try {
			frame.client.sendObject(msg);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void refreshRepo(ArrayList<Repository> repolist) {
		String[][] tableValues = new String[repolist.size()][3];
		for(int i=0;i<repolist.size();i++){
			tableValues[i][0]=repolist.get(i).currentDir;
			tableValues[i][1]=repolist.get(i).remoteDir;
			tableValues[i][2]=repolist.get(i).isRemote?"远程":"本地";
		}
		this.model = new DefaultTableModel(tableValues,this.columnNames);
		this.table.setModel(this.model);
	};
}
 