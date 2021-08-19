package com.qianzhang.mars.parser;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * Yaml格式转换
 *
 * @author guodongqing
 * @since 2.5.0
 */
public class LocalYmlFlowParser extends YmlFlowParser {

    @Override
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
