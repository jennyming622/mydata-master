package com.riease.common.bean;

/**
 * 用來代表寬及高的值，如檔案尺寸的寬高。
 * @author wesleyzhuang
 *
 */
public class Dimension {

	private int width;
	private int height;
	
	public Dimension() {
	}
	
	public static Dimension newInstance(int width, int height) {
		Dimension d = new Dimension();
		d.setWidth(width);
		d.setHeight(height);
		return d;
	}
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
}
