package Chat;

import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;


public class Registerfrm extends JFrame {
	private JLabel account_lab = new JLabel("帳號:");
	private JLabel passwd_lab = new JLabel("密碼:");
	private JLabel passwd2_lab = new JLabel("確認密碼:");
	private JLabel name_lab = new JLabel("暱稱:");
	private JLabel sex_lab = new JLabel("性別:");
	private JTextField account_text = new JTextField("");
	private JPasswordField passwd = new JPasswordField("");
	private JPasswordField passwd2 = new JPasswordField("");
	private JTextField name_text = new JTextField("");
	private JButton send_btn = new JButton("註冊");
	private JButton reset_btn = new JButton("重新填寫");
	private ButtonGroup sex = new ButtonGroup();
	private JRadioButton m = new JRadioButton("男",true);
	private JRadioButton f = new JRadioButton("女",false);
	private JPanel sexpan = new JPanel();
	String sex_str="男";
	Registerfrm(){
		setTitle("註冊帳號");
		setSize(300, 200);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(this);
		setResizable(false);
		setVisible(true);
		setLayout(new GridLayout(6,6,2,2));
		sexpan.setLayout(new GridLayout(1,2));
		sexpan.add(m);
		sexpan.add(f);
		sex.add(m);
		sex.add(f);
		add(account_lab);
		add(account_text);
		add(passwd_lab);
		add(passwd);
		add(passwd2_lab);
		add(passwd2);
		add(name_lab);
		add(name_text);
		add(sex_lab);
		add(sexpan);
		add(send_btn);
		add(reset_btn);
		m.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(e.getItemSelectable() == m){
					sex_str="男";
				} 
			}
		});
		f.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(e.getItemSelectable() == f){
					sex_str="女";
				}
				
			}
		});
		send_btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(checkAlltext() && checkPasswd() && checkAccount()){
					String account_s = account_text.getText();
					String passwd_s = String.valueOf(passwd.getPassword());
					String name_s = name_text.getText();
					String data[] = new String[4];
					data[0] = account_s;
					data[1] = passwd_s;
					data[2] = name_s;
					data[3] = sex_str;
					String sql = "INSERT INTO `accounts` (account,passwd,name,sex) VALUES (?,?,?,?)";
					
					int res = Client.db.insert(sql,data);
					if(res > 0){
						Registerfrm.this.dispose();
						JOptionPane.showMessageDialog(null, "註冊成功。");
					}
				}
	
			}
		});
		
		reset_btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				account_text.setText("");
				passwd.setText("");
				passwd2.setText("");
				name_text.setText("");
			}
		});
	}
	boolean checkAccount(){
		String account = account_text.getText();
		String sql = "SELECT * FROM `accounts` WHERE account = '" + account + "'";
		int count = Client.db.getSQLCount(sql);
		if(count > 0){
			JOptionPane.showMessageDialog(null, "此帳號已被註冊。");
			return false;
		}
		return true;
	}
	boolean checkAlltext(){
		if(account_text.getText().equals("") || passwd.getText().equals("") || 
				passwd2.getText().equals("") || name_text.getText().equals("")){
			JOptionPane.showMessageDialog(null, "請把空處填滿");
			return false;
		}
		return true;
	}
	boolean checkPasswd(){
		if(!String.valueOf(passwd.getPassword()).equals(String.valueOf(passwd2.getPassword()))){
			JOptionPane.showMessageDialog(null, "兩次密碼不符合");
			return false;
		}
		return true;
	}
}
