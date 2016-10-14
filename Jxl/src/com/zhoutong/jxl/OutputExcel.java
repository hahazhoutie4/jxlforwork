package com.zhoutong.jxl;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;

public class OutputExcel {
	/**
	 *@author hahazhoutie4
	 *@website cnblogs.com/hahazhoutie4-blogs/
	 */
	private  static OutputExcel outputExcel;	//单例模式
	/**
	 * @return	singleton 单例模式
	 */
	public static OutputExcel getOutputExcel(){
		if(null==outputExcel){
			outputExcel=new OutputExcel();
		}
		return outputExcel;
	}				
	private OutputExcel(){
	}			
	public void CreateTable(ResultSet res,String directory){
		this.createnewfile(directory);
		
		
		
	}
	public boolean createnewfile(String directory){
		File file=new File(directory);
		try {
			boolean s=file.createNewFile();
			return s;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
