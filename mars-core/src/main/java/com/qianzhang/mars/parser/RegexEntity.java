/**
 *
 *
 * @author qianzhang
 *
 * @Date 2020/4/1
 */
package com.qianzhang.mars.parser;

/**
 * 正则实体，主要用于条件节点
 * @author qianzhang
 */
public class RegexEntity {

	private String item;

	private String[] realItemArray;

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String[] getRealItemArray() {
		return realItemArray;
	}

	public void setRealItemArray(String[] realItemArray) {
		this.realItemArray = realItemArray;
	}
}
