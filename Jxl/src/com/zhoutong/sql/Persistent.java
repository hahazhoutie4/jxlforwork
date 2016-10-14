package com.zhoutong.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import jxl.Cell;

import com.zhoutong.constant.Const;
import com.zhoutong.jxl.ForWork;

public class Persistent {
	/**
	 * @author hahazhoutie4
	 * @return
	 * @website cnblogs.com/hahazhoutie4-blogs/
	 */
	private Connection connection;
	private Statement statement;

	private void getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3305/ajax", "root", "jintian123");
			connection.setAutoCommit(true);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.out.println("fail to connect mysql");
		}
	}

	private void getStatement() {
		if (null == connection) {
			getConnection();
		}
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createTable(ForWork forwork) {
		if (null == statement) {
			getStatement();
		}
		String schema = forwork.getSchema();
		String sql = "create database if not exists " + schema + ";";
		try {
			System.out.print(sql);
			statement.execute(sql);
			String sheet_name = forwork.getSheet_name();
			String sql2 = "create table if not exists " + schema + "."
					+ sheet_name + "( id INT PRIMARY KEY ,";
			List<String> data_names = forwork.getMainInformation().getNames();
			int i = 1;
			for (String data_name : data_names) {
				System.out.println("---" + data_name);
				if (i != 2) {
					if (i == 1 || i == 3) {
						sql2 = sql2 + data_name.toString() + " varchar(45)"
								+ " NOT NULL";
					} else {
						sql2 = sql2 + data_name.toString() + " DOUBLE NOT NULL";
					}
					if (i < data_names.size()) {
						sql2 = sql2 + ",";
					}
				}
				i++;
			}
			if (sheet_name.equals("ǽ") || sheet_name.contains("��")
					|| sheet_name.contains("��") || sheet_name.contains("��")) {
				sql2 = sql2 + ",�������ȼ�  varchar(45)";
			}
			sql2 = sql2 + ");";
			System.out.println(sql2);
			statement.execute(sql2);
			// ����������
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertData(ForWork forwork, boolean rewrite) {
		// Cell[3].getContents().equals("С��")||Cell[3].getContents().equals("�ϼ�")Ϊ�ϼ�������
		String sql = "";
		if (null == statement) {
			getStatement();
		}
		String schema = forwork.getSchema();
		String sheet_name = forwork.getSheet_name();
		if (rewrite) {
			try {
				System.out.println("delete  from " + schema + "." + sheet_name
						+ ";");
				statement.execute("delete  from " + schema + "." + sheet_name
						+ ";");
				System.out.println("ɾ�������������");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Map<Integer, Cell[]> map = forwork.getMap();
		int size = map.size();
		int id = 0;
		for (int j = 3; j < size; j++) {
			Cell[] c = map.get(j); // ĳһ�е�����c
			int length = c.length;
			int caculate = 0;
			if (!c[3].getContents().equals("С��")
					&& !c[3].getContents().equals("�ϼ�")) {
				sql = sql + "insert into " + schema + "." + sheet_name
						+ " values(" + id + ",";
				for (Cell ci : c) {
					if (caculate > 0 && caculate != 2) {
						String content = ci.getContents();
						if (caculate != 1 && caculate != 3) {
							Float f = Float.valueOf(content);
							sql = sql + f;
						} else {
							sql = sql + "\"" + content + "\"";
						}
						if (caculate < length - 1) {
							sql = sql + ",";
						}
					}
					caculate++;
				}
				id++;
				if (sheet_name.equals("ǽ") || sheet_name.contains("��")
						|| sheet_name.contains("��") || sheet_name.contains("��")) {
					sql = sql + ",null";
				}
				sql = sql + ");";
				try {
					statement.addBatch(sql);
					statement.executeBatch();
				} catch (SQLException e) {
					e.printStackTrace();
				} // �˴�ִ�в�����伴��
				System.out.println(sql);
				System.out.println();
				sql = "";
			}
		}
	}

	public boolean isDataExist(ForWork forwork) {
		if (null == statement) {
			getStatement();
		}
		try {
			ResultSet res = statement.executeQuery("select * from "
					+ forwork.getSchema() + "." + forwork.getSheet_name());
			return res.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// ���������ǿ���У�ͨ������������key-valueֵ
	public void insert_other_information(ForWork forwork,
			String conrete_information, String floor_information) {
		String db_name = forwork.getSchema(); // ���ݿ�����
		String table_name = forwork.getSheet_name(); // ��ȡ�������
		String sql = "update " + db_name + "." + table_name + " set �������ȼ� "
				+ "=" + "'" + conrete_information + "'" + " where ¥��=" + "'"
				+ floor_information + "';";
		try {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("sql���" + sql);
	}

	// ���������ǿ��������ͨ��properties�ļ�
	public void insert_other_information(ForWork forwork, Properties properties) {
		String db_name = forwork.getSchema(); // ���ݿ�����
		String table_name = forwork.getSheet_name(); // ��ȡ�������
		Set<Object> set = properties.keySet();
		for (Object o : set) {
			String floor_information = (String) o;
			String concrete_information = (String) properties
					.get(floor_information);
			String sql = "update " + db_name + "." + table_name + " set �������ȼ� "
					+ "=" + "'" + concrete_information + "'" + " where ¥��="
					+ "'" + floor_information + "';";
			System.out.println(sql);
			try {
				statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
		//���������ǿ��������ͨ��xls�ļ���������excel97��
	public void insert_other_information(ForWork forwork,
			Map<String, String> information) {
		String db_name = forwork.getSchema(); // ���ݿ�����
		String table_name = forwork.getSheet_name(); // ��ȡ�������
		Set<String> set = information.keySet();
		for (String o : set) {
			String concrete_information = (String) information.get(o);
			String sql = "update " + db_name + "." + table_name + " set �������ȼ� "
					+ "=" + "'" + concrete_information + "'" + " where ¥��="
					+ "'" + o + "';";
			System.out.println(sql);
			try {
				statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	//��ȡǽ������
	public void getC_1(ForWork forwork,String[] question_1,String QTQ_JLQ,String underGround_){
		Map<String,String> map=new HashMap<String,String>();			//�����洢��ȡ������
		if(null==statement){
			this.getStatement();
		}
		String sql_001,sql_002,sql_003="";
		if(QTQ_JLQ.equals(Const.Q_6)){
			 sql_001=" ģ�����_m2_"+"=0";	//ģ�����=0,����ǽ
		}else{
			 sql_001=" ģ�����_m2_"+">0";	//ģ�����>0,����ǽ
		}
		if(underGround_.equals(Const.Q_8)){
			 sql_002=" ¥��"+" not like "+"'"+"_-%"+"'"+" and ¥��"+"<>"+"'"+"������"+"'";		//���¹�����
		}else{
			 sql_002=" ¥��"+" like "+"'"+"_-%"+"'"+" or ¥��"+" ="+"'"+"������"+"'";		//���Ϲ����� 
		}
		String schema=forwork.getSchema();		//�����ļ���
		String table_name=forwork.getSheet_name();		//���ر���
		String sql_moban_001="select sum(ģ�����_m2_) from "+schema+"."+table_name+";";				//��ȡ����ǽģ�������Ϣ
		String sql_moban_002="select sum(ģ�����_m2_) from "+schema+"."+table_name+" where "+" ¥��"+" not like "+"'"+"_-%"+"'"+" and ¥��"+"<>"+"'"+"������"+"'";		//���ϵ�ģ�����
		String sql_moban_003="select sum(ģ�����_m2_) from "+schema+"."+table_name+" where "+" ¥��"+" not like "+"'"+"_-%"+"'"+" and ¥��"+"<>"+"'"+"������"+"'";		//���µ�ģ�����
		String sql_concrete_001="select sum(���_m3_) from"+schema+"."+table_name+" where ";
		try {
					ResultSet res=statement.executeQuery(sql_moban_001);
					while(res.next()){
						float s=res.getFloat(0);
						map.put("ģ�����", String.valueOf(s));				//��ȡǽ������ģ�����
					}
					ResultSet res_001=statement.executeQuery(sql_moban_002);		
					while(res_001.next()){
						float f=res_001.getFloat(0);
						map.put("����ģ�����",String.valueOf(f));			//��ȡǽ���������ģ�����
					}
					ResultSet res_002=statement.executeQuery(sql_moban_003);
					while(res_002.next()){
						float f1=res_002.getFloat(0);
						map.put("����ģ�����",String.valueOf(f1));			//��ȡǽ���������ģ�����
					}
					ResultSet res_003=statement.executeQuery(sql_concrete_001);
					
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	//��ȡ��������
		public void getC_2(ForWork forwork){
			
			
			
			
		}
		
	//��ȡ��������	
		public void getC_3(ForWork forwork){
			
			
			
			
		}
		
	//��ȡ�幤����
		public void getC_4(ForWork forwork){
			
			
			
			
		}
}