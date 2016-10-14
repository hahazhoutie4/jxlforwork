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
			if (sheet_name.equals("墙") || sheet_name.contains("柱")
					|| sheet_name.contains("梁") || sheet_name.contains("板")) {
				sql2 = sql2 + ",混凝土等级  varchar(45)";
			}
			sql2 = sql2 + ");";
			System.out.println(sql2);
			statement.execute(sql2);
			// 建立表格完成
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertData(ForWork forwork, boolean rewrite) {
		// Cell[3].getContents().equals("小计")||Cell[3].getContents().equals("合计")为合计数列行
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
				System.out.println("删除表格所有数据");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Map<Integer, Cell[]> map = forwork.getMap();
		int size = map.size();
		int id = 0;
		for (int j = 3; j < size; j++) {
			Cell[] c = map.get(j); // 某一行的数据c
			int length = c.length;
			int caculate = 0;
			if (!c[3].getContents().equals("小计")
					&& !c[3].getContents().equals("合计")) {
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
				if (sheet_name.equals("墙") || sheet_name.contains("柱")
						|| sheet_name.contains("梁") || sheet_name.contains("板")) {
					sql = sql + ",null";
				}
				sql = sql + ");";
				try {
					statement.addBatch(sql);
					statement.executeBatch();
				} catch (SQLException e) {
					e.printStackTrace();
				} // 此处执行插入语句即可
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

	// 加入混凝土强度列，通过给定的两个key-value值
	public void insert_other_information(ForWork forwork,
			String conrete_information, String floor_information) {
		String db_name = forwork.getSchema(); // 数据库名称
		String table_name = forwork.getSheet_name(); // 获取表格名称
		String sql = "update " + db_name + "." + table_name + " set 混凝土等级 "
				+ "=" + "'" + conrete_information + "'" + " where 楼层=" + "'"
				+ floor_information + "';";
		try {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("sql语句" + sql);
	}

	// 加入混凝土强度列数，通过properties文件
	public void insert_other_information(ForWork forwork, Properties properties) {
		String db_name = forwork.getSchema(); // 数据库名称
		String table_name = forwork.getSheet_name(); // 获取表格名称
		Set<Object> set = properties.keySet();
		for (Object o : set) {
			String floor_information = (String) o;
			String concrete_information = (String) properties
					.get(floor_information);
			String sql = "update " + db_name + "." + table_name + " set 混凝土等级 "
					+ "=" + "'" + concrete_information + "'" + " where 楼层="
					+ "'" + floor_information + "';";
			System.out.println(sql);
			try {
				statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
		//加入混凝土强度列数，通过xls文件（必须是excel97）
	public void insert_other_information(ForWork forwork,
			Map<String, String> information) {
		String db_name = forwork.getSchema(); // 数据库名称
		String table_name = forwork.getSheet_name(); // 获取表格名称
		Set<String> set = information.keySet();
		for (String o : set) {
			String concrete_information = (String) information.get(o);
			String sql = "update " + db_name + "." + table_name + " set 混凝土等级 "
					+ "=" + "'" + concrete_information + "'" + " where 楼层="
					+ "'" + o + "';";
			System.out.println(sql);
			try {
				statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	//提取墙工程量
	public void getC_1(ForWork forwork,String[] question_1,String QTQ_JLQ,String underGround_){
		Map<String,String> map=new HashMap<String,String>();			//用来存储获取的数据
		if(null==statement){
			this.getStatement();
		}
		String sql_001,sql_002,sql_003="";
		if(QTQ_JLQ.equals(Const.Q_6)){
			 sql_001=" 模板面积_m2_"+"=0";	//模板面积=0,砌体墙
		}else{
			 sql_001=" 模板面积_m2_"+">0";	//模板面积>0,剪力墙
		}
		if(underGround_.equals(Const.Q_8)){
			 sql_002=" 楼层"+" not like "+"'"+"_-%"+"'"+" and 楼层"+"<>"+"'"+"基础层"+"'";		//地下工程量
		}else{
			 sql_002=" 楼层"+" like "+"'"+"_-%"+"'"+" or 楼层"+" ="+"'"+"基础层"+"'";		//地上工程量 
		}
		String schema=forwork.getSchema();		//返回文件名
		String table_name=forwork.getSheet_name();		//返回表名
		String sql_moban_001="select sum(模板面积_m2_) from "+schema+"."+table_name+";";				//获取剪力墙模板面积信息
		String sql_moban_002="select sum(模板面积_m2_) from "+schema+"."+table_name+" where "+" 楼层"+" not like "+"'"+"_-%"+"'"+" and 楼层"+"<>"+"'"+"基础层"+"'";		//地上的模板面积
		String sql_moban_003="select sum(模板面积_m2_) from "+schema+"."+table_name+" where "+" 楼层"+" not like "+"'"+"_-%"+"'"+" and 楼层"+"<>"+"'"+"基础层"+"'";		//地下的模板面积
		String sql_concrete_001="select sum(体积_m3_) from"+schema+"."+table_name+" where ";
		try {
					ResultSet res=statement.executeQuery(sql_moban_001);
					while(res.next()){
						float s=res.getFloat(0);
						map.put("模板面积", String.valueOf(s));				//获取墙面所有模板面积
					}
					ResultSet res_001=statement.executeQuery(sql_moban_002);		
					while(res_001.next()){
						float f=res_001.getFloat(0);
						map.put("地上模板面积",String.valueOf(f));			//获取墙面地上所有模板面积
					}
					ResultSet res_002=statement.executeQuery(sql_moban_003);
					while(res_002.next()){
						float f1=res_002.getFloat(0);
						map.put("地下模板面积",String.valueOf(f1));			//获取墙面地下所有模板面积
					}
					ResultSet res_003=statement.executeQuery(sql_concrete_001);
					
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	//提取梁工程量
		public void getC_2(ForWork forwork){
			
			
			
			
		}
		
	//提取柱工程量	
		public void getC_3(ForWork forwork){
			
			
			
			
		}
		
	//提取板工程量
		public void getC_4(ForWork forwork){
			
			
			
			
		}
}