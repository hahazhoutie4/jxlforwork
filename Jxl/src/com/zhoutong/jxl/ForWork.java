package com.zhoutong.jxl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ForWork {
	/**
	 * @author hahazhoutie4
	 * @website cnblogs.com/hahazhoutie4-blogs/
	 */
	private MainInformation mainInformation;
	private String[] names; // ��������Ϣ
	private Sheet[] sheets;
	private Workbook workbook;

	public ForWork() {
	};

	private Map<Integer, Cell[]> map;
	private String schema;
	private String sheet_name;

	// ����������
	private void createWorkbook(String name) throws BiffException, IOException {
		File file = new File(name);
		String file_name = file.getName();
		String schema1 = file_name.split("\\.")[0];
		schema = "h_" + schema1.split("\\#")[0] + schema1.split("\\#")[1];
		Workbook workbook = Workbook.getWorkbook(file);
		this.workbook = workbook;
	}

	// ��ȡ���б�
	private void getSheets() {
		if (this.workbook != null) {
			this.sheets = workbook.getSheets();
		} else {
			System.out.println("������Ϊ��!");
		}
	}

	// ��ȡ������
	private void getSheetNames() {
		if (this.sheets != null) {
			int length = sheets.length;
			System.out.println("�����" + length);
			names = new String[length];
			for (int i = 0; i < length; i++) {
				names[i] = sheets[i].getName();
			}
		} else {
			System.out.println("������Ϊ�գ�");
		}
	}

	// ��ȡ������,��ȡ¥����Ϣ--�˴��ظ���ȡ
	public void getSheetContent(String sheet_name) {
		this.setSheet_name(sheet_name);
		mainInformation = new MainInformation();
		map = null;
		map = new HashMap<Integer, Cell[]>();
		int length = sheets.length;
		Sheet sheet = null;
		for (int j = 0; j < length; j++) {
			if (sheets[j].getName().equals(sheet_name)) {
				sheet = sheets[j];
				break;
			}
		}
		int rows = sheet.getRows(); // ����
		// ȡ���ݣ��ӵڶ��У������п�ʼ;
		int i_column = 2;
		for (int i = i_column; i < rows; i++) {
			Cell[] cells = sheet.getRow(i);
			map.put(i, cells);
		}
		Cell[] cell_floor_information = sheet.getColumn(2);
		int information_length = cell_floor_information.length;
		List<String> list = new ArrayList<String>();
		for (int j = 4; j < information_length; j++) {
			String information = cell_floor_information[j].toString();
			if (!list.contains(information)) {
				list.add(information);
			}
		}
		mainInformation.setFloor_information(list); // ��ȡ����¥����Ϣ
		this.mainInformation();
	}

	// ��ȡ����mapֵ,Ȼ���ȡ�ؼ���Ϣ
	private void mainInformation() {
		Cell[] cell_information = map.get(2);
		int length = cell_information.length;
		List<String> set = new ArrayList<String>();
		for (int c = 1; c < length; c++) {
			String information = cell_information[c].getContents()
					.replace("(", "_").replace(")", "_");
			set.add(information);
		}
		mainInformation.setNames(set); // ��ȡ���˱�ͷ��Ϣ
	}

	public void Initialized(String url) throws BiffException, IOException {
		this.createWorkbook(url);
		this.getSheets();
		this.getSheetNames();
	}
	/**
	 * @return	�������б�����
	 */
	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}
		/**
		 * @return ����ÿһ��
		 */
	public Map<Integer, Cell[]> getMap() {
		return map;
	}

	public void setMap(Map<Integer, Cell[]> map) {
		this.map = map;
	}

	public void setMainInformation(MainInformation mainInformation) {
		this.mainInformation = mainInformation;
	}
		/**
		 * @return	����¥��ż��Ϻͱ�ͷ����
		 */
	public MainInformation getMainInformation() {
		return mainInformation;
	}
		/**
		 * @return	���ص�ǰ�ļ�����
		 */
	public String getSchema() {
		return schema;
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}
		/**
		 * @return	���ص�ǰ������
		 */
	public String getSheet_name() {
		return sheet_name;
	}

	public void setSheet_name(String sheet_name) {
		this.sheet_name = sheet_name;
	}
}