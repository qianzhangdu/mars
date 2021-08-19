package com.qianzhang.mars.parser;

import cn.hutool.core.util.StrUtil;
import com.qianzhang.mars.exception.ParseException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;

/**
 * 基于zk方式的json形式的解析器
 * @author guodongqing
 * @since 2.5.0
 */
public class ZookeeperJsonFlowParser extends JsonFlowParser{

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperJsonFlowParser.class);

    private String nodePath = "/lite-flow/flow";

    public ZookeeperJsonFlowParser() {

    }

    public ZookeeperJsonFlowParser(String node) {
        nodePath = node;
    }

    @Override
    public void parseMain(List<String> pathList) throws Exception {
        //zk不允许有多个path
        String path = pathList.get(0);
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                path,
                new RetryNTimes(10, 5000)
        );
        client.start();

        if (client.checkExists().forPath(nodePath) == null) {
            client.create().creatingParentsIfNeeded().forPath(nodePath, "".getBytes());
        }

        String content = new String(client.getData().forPath(nodePath));


        if (StrUtil.isBlank(content)) {
            String error = MessageFormat.format("the node[{0}] value is empty", nodePath);
            throw new ParseException(error);
        }
        parse(content);


        final NodeCache cache = new NodeCache(client,nodePath);
        cache.start();

        cache.getListenable().addListener(() -> {
            String content1 = new String(cache.getCurrentData().getData());
            LOG.info("stating load flow config....");
            parse(content1);
        });
    }
}
