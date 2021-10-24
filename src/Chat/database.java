package Chat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import Chat.ServerProperties;

public class database {
	static String DB_host = "127.0.0.1";
	static String DB_name = "chat_java";
	static String DB_user = "root";
	static String DB_passwd = "";
	static String DB_URL = "jdbc:mysql://" + DB_host +"/" + DB_name + "?useUnicode=true&characterEncoding=utf-8";
	private Connection connect = null;
	private Statement state = null;
	private ResultSet result = null;
	database(){
		loadSetting();
		try {
			connect = DriverManager.getConnection(DB_URL,DB_user,DB_passwd);
			//System.out.println("MySQL connect status OK!!");
			state = connect.createStatement();
		} catch (SQLException e){
			System.out.println("MySQL connect status fail!!");
		}
	}
	boolean checkisLogin(String account){
		String sql = "SELECT * FROM `accounts` WHERE account = '" + account + "'";
		ResultSet r = getSQL_Data(sql) ;
		try {
			r.next();
			int state =(int)r.getObject("state");
			if(state == 0){
				return true;
			} else {
				return false;
			}
		} catch (SQLException e){
			JOptionPane.showMessageDialog(null, "帳號或密碼輸入錯誤");
		}
		return false;
	}
	boolean checkLogin(String account,String passwd){
		String sql = "SELECT * FROM `accounts` WHERE account = '" + account + "'";
		ResultSet r = getSQL_Data(sql) ;
		try {
			r.next();
			String sql_passwd = (String) r.getObject("passwd");
			if(sql_passwd.equals(passwd)) {
				String id = String.valueOf(r.getInt("id"));
				String name = (String)r.getObject("name");
				String sex = (String)r.getObject("sex");
				String admin = String.valueOf(r.getInt("admin"));
				connectPan.conn.setName(name);
				connectPan.conn.setAdmin(Integer.parseInt(admin));
				connectPan.conn.setSex(sex);
				connectPan.conn.setAccount(account);
				Connect_toServer.sendCommand("setName",name);
				Connect_toServer.sendCommand("setSex",sex);
				Connect_toServer.sendCommand("setAdmin",admin);
				Connect_toServer.sendCommand("setId",id);
				return true;
			} else {
				JOptionPane.showMessageDialog(null, "帳號或密碼輸入錯誤");
			}
		} catch (SQLException e){
			JOptionPane.showMessageDialog(null, "帳號或密碼輸入錯誤");
		}
		return false;
	}
	int update(String sql){
		try {
			int resc = state.executeUpdate(sql);
			return resc;
		} catch (SQLException e) {
			System.out.println("錯誤");
		}
		return 0; 
	}
	int insert(String sql,String data[]){
		int res=0;
		try {
			PreparedStatement ps = connect.prepareStatement(sql);
			for(int i=0;i<data.length;i++){
				ps.setString((i+1), data[i]);
			} 
			res = ps.executeUpdate();
		} catch(SQLException e){
			e.printStackTrace();
		}
		return res;
	}
	
	ResultSet getSQL_Data(String sql){
		try {
			result = state.executeQuery(sql);
			return result;
		} catch (SQLException e) {
			
		}
		return null;
	}
	int getSQLCount(String sql){
		ResultSet res = getSQL_Data(sql);
		int count=0;
		try {
			while(res.next()){
				count++;
			}
		} catch (SQLException e) {
			
		}
		return count;
	}
	
	public static void loadSetting() {
		DB_host = ServerProperties.getProperty("DB_host", DB_host);
		DB_name = ServerProperties.getProperty("DB_name", DB_name);
		DB_user = ServerProperties.getProperty("DB_user", DB_user);
		DB_passwd = ServerProperties.getProperty("DB_passwd", DB_passwd);
	}
	
}