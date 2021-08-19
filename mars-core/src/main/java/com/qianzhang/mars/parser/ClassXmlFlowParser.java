package com.qianzhang.mars.parser;

import java.util.List;

/**
 * 基于自定义的xml方式解析器
 * @author qianzhang
 */
public abstract class ClassXmlFlowParser extends XmlFlowParser {
	@Override
	public void parseMain(List<String> pathList) throws Exception {
		String content = parseCustom();
		parse(content);
	}

	public abstract String parseCustom();
}
