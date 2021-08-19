package com.qianzhang.mars.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * logo打印器
 * @author qianzhang
 */
public class LOGOPrinter {

	private static final Logger LOG = LoggerFactory.getLogger(LOGOPrinter.class);

	public static void print() {
		StringBuilder str = new StringBuilder("\n");
		str.append("=========================================轻量级，微流程框架====================================================\n");
		LOG.info(str.toString());
	}
}
