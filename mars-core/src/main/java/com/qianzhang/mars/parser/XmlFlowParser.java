package com.qianzhang.mars.parser;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.qianzhang.mars.common.LocalDefaultFlowConstant;
import com.qianzhang.mars.entity.flow.Chain;
import com.qianzhang.mars.entity.flow.Condition;
import com.qianzhang.mars.entity.flow.Executable;
import com.qianzhang.mars.entity.flow.Node;
import com.qianzhang.mars.enums.NodeTypeEnum;
import com.qianzhang.mars.exception.ExecutableItemNotFoundException;
import com.qianzhang.mars.exception.NodeTypeNotSupportException;
import com.qianzhang.mars.exception.ParseException;
import com.qianzhang.mars.spring.ComponentScanner;
import com.qianzhang.mars.core.NodeComponent;
import com.qianzhang.mars.flow.FlowBus;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * xml形式的解析器
 *
 * @author qianzhang
 */
public abstract class XmlFlowParser extends FlowParser {

    private final Logger LOG = LoggerFactory.getLogger(XmlFlowParser.class);

    public void parse(String content) throws Exception {
        parse(ListUtil.toList(content));
    }

    public void parse(List<String> contentList) throws Exception {
        if (CollectionUtil.isEmpty(contentList)) {
            return;
        }
        List<Document> documentList = ListUtil.toList();
        for (String content : contentList) {
            Document document = DocumentHelper.parseText(content);
            documentList.add(document);
        }
        parseDocument(documentList);
    }

    //xml形式的主要解析过程
    public void parseDocument(List<Document> documentList) throws Exception {
        try {
            //先进行Spring上下文中的节点的判断
            for (Entry<String, NodeComponent> componentEntry : ComponentScanner.nodeComponentMap.entrySet()) {
                if (!FlowBus.containNode(componentEntry.getKey())) {
                    FlowBus.addCommonNode(componentEntry.getKey(), new Node(componentEntry.getValue()));
                }
            }

            for (Document document : documentList) {
                Element rootElement = document.getRootElement();
                Element nodesElement = rootElement.element("nodes");
                // 当存在<nodes>节点定义时，解析node节点
                if (ObjectUtil.isNotNull(nodesElement)){
                    List<Element> nodeList = nodesElement.elements("node");
                    String id, name, clazz, type, script;
                    for (Element e : nodeList) {
                        id = e.attributeValue("id");
                        name = e.attributeValue("name");
                        clazz = e.attributeValue("class");
                        type = e.attributeValue("type");

                        //初始化type
                        if (StrUtil.isBlank(type)){
                            type = NodeTypeEnum.COMMON.getCode();
                        }
                        NodeTypeEnum nodeTypeEnum = NodeTypeEnum.getEnumByCode(type);
                        if (ObjectUtil.isNull(nodeTypeEnum)){
                            throw new NodeTypeNotSupportException(StrUtil.format("type [{}] is not support", type));
                        }

                        //这里区分是普通java节点还是脚本节点
                        //如果是脚本节点，又区分是普通脚本节点，还是条件脚本节点
                        if (nodeTypeEnum.equals(NodeTypeEnum.COMMON) && StrUtil.isNotBlank(clazz)){
                            if (!FlowBus.containNode(id)){
                                FlowBus.addCommonNode(id, name, clazz);
                            }
                        }
                    }
                }

                // 解析chain节点
                List<Element> chainList = rootElement.elements("chain");
                for (Element e : chainList) {
                    String chainName = e.attributeValue("name");
                    if (!FlowBus.containChain(chainName)) {
                        parseOneChain(e, documentList);
                    }
                }
            }
        } catch (Exception e) {
            String errorMsg = "FlowParser parser exception";
            LOG.error(errorMsg, e);
            throw new ParseException(errorMsg);
        }
    }

    /**
     * 解析一个chain的过程
     */
    private void parseOneChain(Element e, List<Document> documentList) throws Exception {
        String condArrayStr;
        String[] condArray;
        String group;
        String errorResume;
        Condition condition;
        Element condE;
        List<Executable> chainNodeList;
        List<Condition> conditionList;

        String chainName = e.attributeValue("name");
        conditionList = new ArrayList<>();
        for (Iterator<Element> it = e.elementIterator(); it.hasNext(); ) {
            condE = it.next();
            condArrayStr = condE.attributeValue("value");
            errorResume = condE.attributeValue("errorResume");
            group = condE.attributeValue("group");
            if (StrUtil.isBlank(condArrayStr)) {
                continue;
            }
            if (StrUtil.isBlank(group)) {
                group = LocalDefaultFlowConstant.DEFAULT;
            }
            if (StrUtil.isBlank(errorResume)) {
                errorResume = Boolean.TRUE.toString();
            }
            condition = new Condition();
            chainNodeList = new ArrayList<>();
            condArray = condArrayStr.split(",");
            RegexEntity regexEntity;
            String itemExpression;
            String item;
            //这里解析的规则，优先按照node去解析，再按照chain去解析
            for (int i = 0; i < condArray.length; i++) {
                itemExpression = condArray[i].trim();
                regexEntity = parseNodeStr(itemExpression);
                item = regexEntity.getItem();
                if (FlowBus.containNode(item)) {
                    Node node = FlowBus.getNode(item);
                    chainNodeList.add(node);
                    //这里判断是不是条件节点，条件节点会含有realItem，也就是括号里的node
                    if (regexEntity.getRealItemArray() != null) {
                        for (String key : regexEntity.getRealItemArray()) {
                            if (FlowBus.containNode(key)) {
                                Node condNode = FlowBus.getNode(key);
                                node.setCondNode(condNode.getId(), condNode);
                            } else if (hasChain(documentList, key)) {
                                Chain chain = FlowBus.getChain(key);
                                node.setCondNode(chain.getChainName(), chain);
                            }
                        }
                    }
                } else if (hasChain(documentList, item)) {
                    Chain chain = FlowBus.getChain(item);
                    chainNodeList.add(chain);
                } else {
                    String errorMsg = StrUtil.format("executable node[{}] is not found!", regexEntity.getItem());
                    throw new ExecutableItemNotFoundException(errorMsg);
                }
            }
            condition.setErrorResume(errorResume.equals(Boolean.TRUE.toString()));
            condition.setGroup(group);
            condition.setConditionType(condE.getName());
            condition.setNodeList(chainNodeList);
            super.buildBaseFlowConditions(conditionList, condition);
        }
        FlowBus.addChain(chainName, new Chain(chainName, conditionList));
    }

    //判断在这个FlowBus元数据里是否含有这个chain
    //因为chain和node都是可执行器，在一个规则文件上，有可能是node，有可能是chain
    @SuppressWarnings("unchecked")
    private boolean hasChain(List<Document> documentList, String chainName) throws Exception {
        for (Document document : documentList) {
            List<Element> chainList = document.getRootElement().elements("chain");
            for (Element ce : chainList) {
                String ceName = ce.attributeValue("name");
                if (ceName.equals(chainName)) {
                    if (!FlowBus.containChain(chainName)) {
                        parseOneChain(ce, documentList);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
