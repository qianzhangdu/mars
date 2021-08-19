/**
 *
 *
 * @author qianzhang
 *
 * @Date 2020/4/1
 */
package com.qianzhang.mars.spring;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.qianzhang.mars.annotation.LiteflowComponent;
import com.qianzhang.mars.aop.ICmpAroundAspect;
import com.qianzhang.mars.core.NodeComponent;
import com.qianzhang.mars.util.LOGOPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * 组件扫描类，只要是NodeComponent的实现类，都可以被这个扫描器扫到
 * @author qianzhang
 */
public class ComponentScanner implements BeanPostProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(ComponentScanner.class);

	public static Map<String, NodeComponent> nodeComponentMap = new HashMap<>();

	public static ICmpAroundAspect cmpAroundAspect;

	static {
		// 打印liteflow的LOGO
		LOGOPrinter.print();
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class clazz = bean.getClass();
		// 组件的扫描发现，扫到之后缓存到类属性map中
		if (NodeComponent.class.isAssignableFrom(clazz)) {
			LOG.info("component[{}] has been found", beanName);
			NodeComponent nodeComponent = (NodeComponent) bean;
			nodeComponent.setNodeId(beanName);

			//判断NodeComponent是否是标识了@LiteflowComponent的标注
			//如果标注了，那么要从中取到name字段
			LiteflowComponent liteflowComponent = bean.getClass().getAnnotation(LiteflowComponent.class);
			if (ObjectUtil.isNotNull(liteflowComponent)){
				String name = liteflowComponent.name();
				if (StrUtil.isNotBlank(name)){
					nodeComponent.setName(name);
				}
			}

			nodeComponent.setSelf(nodeComponent);
			nodeComponentMap.put(beanName, nodeComponent);
		}

		// 组件Aop的实现类加载
		if (ICmpAroundAspect.class.isAssignableFrom(clazz)) {
			LOG.info("component aspect implement[{}] has been found", beanName);
			cmpAroundAspect = (ICmpAroundAspect) bean;
		}

		return bean;
	}

	public static void cleanCache(){
		nodeComponentMap.clear();
	}
}
