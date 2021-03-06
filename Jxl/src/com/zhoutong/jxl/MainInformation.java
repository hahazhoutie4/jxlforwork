package com.zhoutong.jxl;

import java.util.List;

public class MainInformation {
	/**
	 * @author hahazhoutie4
	 * @website cnblogs.com/hahazhoutie4-blogs/
	 */
	public MainInformation() {
	}
	private List<String> floor_information;// 楼层号
	private List<String> names; // 工程量名称
		/**
		 * @return	返回所有表头
		 */
	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}
	/**
	 * 
	 * @return 返回所有楼层信息
	 */
	public List<String> getFloor_information() {
		return floor_information;
	}

	public void setFloor_information(List<String> floor_information) {
		this.floor_information = floor_information;
	}
}