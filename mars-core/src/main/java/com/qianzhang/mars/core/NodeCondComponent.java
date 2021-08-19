/**
 *
 *
 * @author qianzhang
 *
 * @Date 2020/4/1
 */
package com.qianzhang.mars.core;

/**
 * 条件路由节点抽象类
 * @author qianzhang
 */
public abstract class NodeCondComponent extends NodeComponent {

	@Override
	public void process() throws Exception {
		String nodeId = this.processCond();
		this.getSlot().setCondResult(this.getClass().getName(), nodeId);
	}

	//用以返回路由节点的beanId
	public abstract String processCond() throws Exception;

}
