package Chat;

import Chat.Registerfrm;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.net.*;

public class Client {
	static main_frm frm = new main_frm();
	static database db = new database();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client client = new Client();
	}

}

class main_frm extends JFrame{
	static connectPan connectpan = new connectPan();
	static messagePan messagepan = new messagePan();
	static loginPan loginpan = new loginPan();
	static gamePan gamepan = new gamePan();
	main_frm(){
		setTitle("客戶端");
		setSize(630, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(this);
		setResizable(false);
		setVisible(true);
		setLayout(null);
		add(connectpan);
		add(messagepan);
		add(loginpan);
		add(gamepan);
	}
}
class gamePan extends JPanel implements Runnable {
	private JLabel time_lab = new JLabel("");
	private int time_min=0,time_sec=0;
	private JButton startgame_btn = new JButton("尋找對戰");
	private JButton cancelgame_btn = new JButton("取消對戰");
	private Thread t;
	connect_game cgame;
	gamePan(){
		setBorder(BorderFactory.createTitledBorder("遊戲"));
		setLayout(null);
		setVisible(false);
		setBounds(5, 450, 600, 100);
		startgame_btn.setBounds(50, 25, 100, 50);
		cancelgame_btn.setBounds(50, 25, 100, 50);
		time_lab.setBounds(200, 25, 100, 50);
		startgame_btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				time_min=0;
				time_sec=0;
				t = new Thread(Client.frm.gamepan);
				t.start();
				cancelgame_btn.setVisible(true);
				startgame_btn.setVisible(false);
				cgame = new connect_game("127.0.0.1",7778);
			}	
		});
		cancelgame_btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				cgame.close();
				cgame = null;
				close();
			}	
		});
		add(startgame_btn);
		add(cancelgame_btn);
		add(time_lab);
	}
	public void run() {
		while(t != null){
			setTimeLab();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			time_sec++;
			if(time_sec >= 60){
				time_min++;
				time_sec=0;
			}
		}
		
	}
	void close(){
		cancelgame_btn.setVisible(false);
		startgame_btn.setVisible(true);
		setThreadnull();
	}
	void setThreadnull(){
		t = null;
		time_lab.setText("");
	}
	void setStartbtn(boolean set){
		startgame_btn.setEnabled(set);
	}
	public void setTimeLab(){
		String min0 = "";
		String sec0 = "";
		if(time_min < 10)
			min0="0";
		else 
			min0="";
		if(time_sec < 10)
			sec0="0";
		else 
			sec0="";
		time_lab.setText(min0 + "" + time_min + ":" + sec0 + "" + time_sec);
	}
}
class testfrm extends JFrame{
	boolean finish = false;
	testfrm(){
		setSize(450, 550);
		setTitle("測試");
		//setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setLayout(null);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(final WindowEvent event){
				close();
			}
		});
	}
	void close(){
		if(!finish){
			Client.frm.gamepan.cgame.sendCommand("closeroomerr",String.valueOf(Client.frm.gamepan.cgame.getRoomID()));
		}
		Client.frm.gamepan.close();
		Client.frm.gamepan.setStartbtn(true);
		this.dispose();

	}
	void intrrupclose(){
		JOptionPane.showMessageDialog(null, "對手已離開對戰");
		close();
	}
}
class connect_game implements Runnable{
	private DataOutputStream out;
	private DataInputStream in;
	private String ip="";
	private int port = 0;
	private int roomID = 0;
	testfrm gameroom;
	Thread thread = new Thread(this);
	Socket gamess;
	connect_game(String ip,int port){
		this.ip = ip;
		this.port = port;
		try {
			gamess = new Socket(ip,port); 
			in = new DataInputStream(gamess.getInputStream());
			out = new DataOutputStream(gamess.getOutputStream());
			thread.start();
			sendCommand("setName",Client.frm.connectpan.conn.getName());
			sendCommand("SearchGame"," ");
		} catch (Exception e){
			Client.frm.gamepan.close();
			JOptionPane.showMessageDialog(null, "遊戲伺服器維修中...");
		}
	}
	void sendCommand(String command,String data){
		String str = "command "+ command +" " + data;
		try {
			out.writeUTF(str);
		} catch (IOException e) {
			
		}
	}
	void command(String command[]){
		switch(command[0]){
			case "opengame":
				new testfrm();
				Client.frm.gamepan.close();
				Client.frm.gamepan.setStartbtn(false);
				break;
			case "setroomid":
				this.roomID = Integer.parseInt(command[1]);
				break;
			case "closeroomerr":
				gameroom.intrrupclose();
				gameroom = null;
				break;
			default:
				break;
		}
	}
	public void run() {
		try {
			while(true){
				String in_data = in.readUTF();
				String prefix[] = in_data.split(" ");
				switch(prefix[0]){
					case "gameserver":
						in_data = DelPrefix(in_data,prefix[0]);
						String command_str[] = in_data.split(" ");	
						command(command_str);
						break;
					case "message":
						in_data = DelPrefix(in_data,prefix[0]);
						messagePan.append_textarea(in_data);
						break;
					default:
							
						break;
				}
				
			}
		} catch (Exception e){
			close();
		}
	}
	String DelPrefix(String str,String prefix){
		return str.substring(prefix.length()+1,str.length());
	}
	void close(){
		try {
			Client.frm.gamepan.close();
			sendCommand("cancel","");
			out.close();
			in.close();
			gamess.close();	
		} catch (IOException e1) {
			
		}
	}
	int getRoomID(){
		return this.roomID;
	}
	void setRoomID(int roomid){
		this.roomID = roomid;
	}
	
	
	
}
class messagePan extends JPanel{
	static JTextArea textarea = new JTextArea();
	static DefaultListModel list_model = new DefaultListModel();
	static JList online_list = new JList(list_model);
	JScrollPane scrollPane = new JScrollPane(textarea);
	private JTextField send_text = new JTextField();
	private JButton send_btn = new JButton("傳送");
	messagePan(){
		setBorder(BorderFactory.createTitledBorder("訊息"));
		setLayout(null);
		setVisible(false);
		setBounds(5, 100, 600, 350);
		online_list.setBorder(BorderFactory.createTitledBorder("線上名單"));
		online_list.setBackground(Color.CYAN);
		online_list.setBounds(5, 20, 100, 250);
		scrollPane.setBounds(130, 20, 450, 250);
		send_text.setBounds(10, 290, 380, 30);
		send_btn.setBounds(400, 290, 75, 30);
		textarea.setEditable(false);
		send_btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(!send_text.getText().equals("")){
					connectPan.conn.sendMessage(send_text.getText());
					messagePan.append_textarea("我:" + send_text.getText());
					send_text.setText("");
				}
			}
		});
		send_text.addKeyListener(new KeyAdapter(){
			public void keyPressed( KeyEvent e ){
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					if(!send_text.getText().equals("")){
						connectPan.conn.sendMessage(send_text.getText());
						messagePan.append_textarea("我:" + send_text.getText());
						send_text.setText("");
					}
				}
			}
		});
		add(scrollPane);
		add(send_text);
		add(send_btn);
		add(online_list);
	}
	static void addOnlineList_item(String str){
		list_model.addElement(str);
	}
	static void removeOnlineList_item(String str){
		list_model.removeElement(str);
	}
	static void removeAllOnlineList_item(){
		list_model.removeAllElements();
	}
	static void append_textarea(String str){
		textarea.append(str+"\r\n");
		textarea.setCaretPosition(textarea.getDocument().getLength()); 
	}
}

class loginPan extends JPanel{
	private JLabel account_lab = new JLabel("帳號:");
	private JLabel passwd_lab = new JLabel("密碼:");
	private JTextField account = new JTextField();
	private JPasswordField passwd = new JPasswordField("");
	private JButton login_btn = new JButton("登入");
	private JButton register_btn = new JButton("沒有帳號?點我註冊");
	loginPan(){
		setBorder(BorderFactory.createTitledBorder("帳號登入"));
		setLayout(new GridLayout(3,3,2,2));
		setBounds(5, 100, 600, 120);
		login_btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String passwd_str = String.valueOf(passwd.getPassword());
				if(Client.db.checkLogin(account.getText(),passwd_str)){
					if(Client.db.checkisLogin(account.getText())){
						Connect_toServer.sendCommand("loginMessage","");
						Client.frm.messagepan.setVisible(true);
						messagePan.append_textarea("[系統訊息]你已經進入聊天室。");
						setVisible(false);
						main_frm.gamepan.setVisible(true);
					} else {
						JOptionPane.showMessageDialog(null, "此帳號已在線上。");
					}
					
				}
				
			}
		});
		
		register_btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new Registerfrm();
			}
		});
		
		setAllEnabled(false);
		add(account_lab);
		add(account);
		add(passwd_lab);
		add(passwd);
		add(login_btn);
		add(register_btn);
	}
	void setAllEnabled(boolean f){
		register_btn.setEnabled(f);
		account.setEnabled(f);
		passwd.setEnabled(f);
		login_btn.setEnabled(f);
	}
}

class connectPan extends JPanel{
	static JButton connect_btn = new JButton("連線");
	static JLabel ip_lab = new JLabel("IP:");
	static JLabel port_lab = new JLabel("port:");
	static JTextField ip_t = new JTextField("127.0.0.1");
	static JTextField port_t = new JTextField("7777");
	static Connect_toServer conn;
	connectPan(){
		setBorder(BorderFactory.createTitledBorder("連線設定"));
		setLayout(new FlowLayout());
		setBounds(5, 5, 600, 70);
		add(ip_lab);
		add(ip_t);
		add(port_lab);
		add(port_t);
		add(connect_btn);
		connect_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String ip = ip_t.getText();
				int port = 7777;
				try {
					port = Integer.parseInt(port_t.getText());
				} catch (Exception e){
					JOptionPane.showMessageDialog(null, port_t.getText() +"是一個不合法的port");
				}
				if(ip != null && port > 0){
					//setAllEnabled(false);
					
					conn = new Connect_toServer(ip,port);
				}
			}	
		});
	}
	static void setAllEnabled(boolean f){
		connect_btn.setEnabled(f);
		ip_t.setEnabled(f);
		port_t.setEnabled(f);	
	}
}
class Connect_toServer implements Runnable {
	private String ip="";
	private int port=0;
	String name = "";
	String sex="";
	String account = "";
	int admin = 0;
	static DataOutputStream out;
	static DataInputStream in;
	Thread thread = new Thread(this);
	Socket ss;
	Connect_toServer(String ip,int port){
		this.ip = ip;
		this.port = port;
		try {
			ss = new Socket(ip,port); 
			in = new DataInputStream(ss.getInputStream());
			out = new DataOutputStream(ss.getOutputStream());
			thread.start();
			
			
		} catch (Exception e){
			JOptionPane.showMessageDialog(null, "目前無法連線");
			Client.frm.connectpan.setAllEnabled(true);
			Client.frm.loginpan.setAllEnabled(false);
		}
	}
	void sendMessage(String str){
		
		try {
			out.writeUTF("message " + str);
		} catch (IOException e) {
			
		}
	}
	void sendErr(String str){
		try {
			out.writeUTF("error " + str);
		} catch (IOException e) {
			
		}
	}
	static void sendCommand(String command,String data){
		String str = "command "+ command +" " + data;
		try {
			out.writeUTF(str);
		} catch (IOException e) {
			
		}
	}
	void setName(String name){
		this.name = name;
	}
	void setSex(String sex){
		this.sex = sex;
	}
	void setAccount(String account){
		this.account = account;
	}
	void setAdmin(int admin){
		this.admin = admin;
	}
	String getName(){
		return this.name;
	}
	
	public void run() {
		try {
			while(true){
				String in_data = in.readUTF();
				String prefix[] = in_data.split(" ");
				switch(prefix[0]){
					case "server":
						in_data = DelPrefix(in_data,prefix[0]);
						String command_str[] = in_data.split(" ");
						server(command_str);
						break;
					case "message":
						in_data = DelPrefix(in_data,prefix[0]);
						messagePan.append_textarea(in_data);
						break;
					default:
						sendErr(in_data);		
						break;
				}
				
			}
		} catch (Exception e){
			try {
				out.close();
				in.close();
				ss.close();
				Client.frm.messagepan.setVisible(false);
				Client.frm.loginpan.setVisible(true);
				Client.frm.loginpan.setAllEnabled(false);
				Client.frm.connectpan.setAllEnabled(true);
				main_frm.gamepan.setVisible(false);
				JOptionPane.showMessageDialog(null, "與伺服器連線中斷。");
				messagePan.removeAllOnlineList_item();
				//client.db.setAccount_state(account, 0);
			} catch (IOException e1) {
				
			}
		}
	}
	String DelPrefix(String str,String prefix){
		return str.substring(prefix.length()+1,str.length());
	}
	void server(String command[]){
		try {
			switch(command[0]){
			case "addOnlineList":
				messagePan.addOnlineList_item(command[1]);
				break;
			case "removeOnlineList":
				messagePan.removeOnlineList_item(command[1]);
				break;
			case "is_connect":
				connectPan.setAllEnabled(false);
				Client.frm.loginpan.setAllEnabled(true);
				JOptionPane.showMessageDialog(null, "歡迎來到測試伺服器。\n請登入帳號");
				break;
			default:
				
				break;
		}
		} catch(ArrayIndexOutOfBoundsException e){
			
		}
	}
}
