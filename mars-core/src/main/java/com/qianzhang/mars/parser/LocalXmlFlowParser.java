/**
 *
 *
 * @author qianzhang
 *
 * @Date 2020/4/1
 */
package com.qianzhang.mars.parser;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * 基于本地的xml方式解析器
 * @author qianzhang
 */
public class LocalXmlFlowParser extends XmlFlowParser{

	public void parseMain(List<String> pathList) throws Exception {
		Resource[] resources = matchRuleResources(pathList);
		List<String> contentList = ListUtil.toList();
		for (Resource resource : resources) {
			String content = IoUtil.read(resource.getInputStream(), CharsetUtil.CHARSET_UTF_8);
			if (StrUtil.isNotBlank(content)){
				contentList.add(content);
			}
		}
		parse(contentList);
	}
}
