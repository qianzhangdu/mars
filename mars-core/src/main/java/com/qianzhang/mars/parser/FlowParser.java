package com.qianzhang.mars.parser;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import com.qianzhang.mars.entity.flow.Condition;
import com.qianzhang.mars.entity.flow.ThenCondition;
import com.qianzhang.mars.entity.flow.WhenCondition;
import com.qianzhang.mars.exception.ConfigErrorException;
import com.qianzhang.mars.enums.ConditionTypeEnum;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author guodongqing
 * @since 2.5.0
 */
public abstract class FlowParser {

    public abstract void parseMain(List<String> pathList) throws Exception;

    public abstract void parse(List<String> contentList) throws Exception ;

    private static final Pattern p = Pattern.compile("[^\\)\\(]+");

    //条件节点的正则解析
    public RegexEntity parseNodeStr(String str) {
        List<String> list = new ArrayList<String>();

        Matcher m = p.matcher(str);
        while(m.find()){
            list.add(m.group());
        }
        RegexEntity regexEntity = new RegexEntity();
        regexEntity.setItem(list.get(0).trim());
        if(list.size() > 1){
            String[] realNodeArray = list.get(1).split("\\|");
            for (int i = 0; i < realNodeArray.length; i++) {
                realNodeArray[i] = realNodeArray[i].trim();
            }
            regexEntity.setRealItemArray(realNodeArray);
        }
        return regexEntity;
    }

    protected void buildBaseFlowConditions(List<Condition> conditionList, Condition condition){
        if (condition.getConditionType().equals(ConditionTypeEnum.TYPE_THEN.getType())) {
            if (conditionList.size() > 1 &&
                    CollectionUtil.getLast(conditionList) instanceof ThenCondition) {
                CollectionUtil.getLast(conditionList).getNodeList().addAll(condition.getNodeList());
            } else {
                conditionList.add(new ThenCondition(condition));
            }
        } else if (condition.getConditionType().equals(ConditionTypeEnum.TYPE_WHEN.getType())) {
            if (conditionList.size() > 1 &&
                    CollectionUtil.getLast(conditionList) instanceof WhenCondition &&
                    CollectionUtil.getLast(conditionList).getGroup().equals(condition.getGroup())) {
                CollectionUtil.getLast(conditionList).getNodeList().addAll(condition.getNodeList());
            } else {
                conditionList.add(new WhenCondition(condition));
            }
        }
    }

    /**
     *  根据配置的ruleSource查找匹配的资源
     */
    protected Resource[] matchRuleResources(final List<String> pathList)  throws IOException {
        Assert.notEmpty(pathList, "rule source must not be null");

        List<Resource> allResource = new ArrayList<>();
        for (String path : pathList){
            String locationPattern = path;
            if (!locationPattern.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
                locationPattern = ResourceUtils.CLASSPATH_URL_PREFIX + locationPattern;
            }
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(locationPattern);
            if(ArrayUtil.isEmpty(resources)) {
                throw new ConfigErrorException("config error,please check rule source property");
            }
            allResource.addAll(ListUtil.toList(resources));
        }

        //如果有多个资源，检查资源都是同一个类型，如果出现不同类型的配置，则抛出错误提示
        Set<String> fileTypeSet = new HashSet<>();
        allResource.forEach(resource -> fileTypeSet.add(FileUtil.extName(resource.getFilename())));
        if (fileTypeSet.size() != 1){
            throw new ConfigErrorException("config error,please use the same type of configuration");
        }

        return allResource.toArray(new Resource[]{});
    }
}
