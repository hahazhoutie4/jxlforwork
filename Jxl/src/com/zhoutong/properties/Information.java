package com.zhoutong.properties;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
public class Information {
	/**
	 * @author hahazhoutie4
	 * @website cnblogs.com/hahazhoutie4-blogs/
	 */
	public Information() {
	}
	public Map<String, String> getInformation(File file) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			Workbook workbook = Workbook.getWorkbook(file);
			Sheet sheet = workbook.getSheet(0);
			int rows = sheet.getRows();
			for (int i = 0; i < rows; i++) {
				String floor_information = sheet.getCell(0, i).getContents();
				String concrete_information = sheet.getCell(1, i).getContents();
				map.put(floor_information, concrete_information);
			}
		} catch (BiffException | IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	public SortedSet<Integer> getConcrete(Map<String,String> map){
			SortedSet<Integer> set=new TreeSet<Integer>();
			Set<String> key=map.keySet();
			for(String q:key){
				if(!q.equals("������")&&!String.valueOf(q.charAt(1)).equals("-")){
						set.add(Integer.valueOf(map.get(q).split("C")[1]));
				}
			}
			return set;			//��ȡ��������¥��Ļ�����ǿ�ȵȼ�����,�磺C30-C35-C40-C45,��ȡ����ֵΪ30-35-40-45���˴�Ϊ�Ѿ�����Ļ�����ǿ�ȵȼ�
	}	
}
