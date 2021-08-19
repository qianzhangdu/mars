/**
 *
 *
 * @author qianzhang
 *
 * @Date 2020/4/1
 */
package com.qianzhang.mars.core;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.qianzhang.mars.entity.data.CmpStep;
import com.qianzhang.mars.entity.data.CmpStepType;
import com.qianzhang.mars.entity.data.DataBus;
import com.qianzhang.mars.entity.data.Slot;
import com.qianzhang.mars.entity.flow.Executable;
import com.qianzhang.mars.entity.flow.Node;
import com.qianzhang.mars.entity.monitor.CompStatistics;
import com.qianzhang.mars.enums.NodeTypeEnum;
import com.qianzhang.mars.monitor.MonitorBus;
import com.qianzhang.mars.spring.ComponentScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.qianzhang.mars.flow.FlowBus;

/**
 * 普通组件抽象类
 * @author qianzhang
 */
public abstract class NodeComponent {

	private static final Logger LOG = LoggerFactory.getLogger(NodeComponent.class);

	private final TransmittableThreadLocal<Integer> slotIndexTL = new TransmittableThreadLocal<>();

	@Autowired(required = false)
	private MonitorBus monitorBus;

	private String nodeId;

	private String name;

	private NodeTypeEnum type;

	//这是自己的实例，取代this
	//为何要设置这个，用this不行么，因为如果有aop去切的话，this在spring的aop里是切不到的。self对象有可能是代理过的对象
	private NodeComponent self;

	//是否结束整个流程，这个只对串行流程有效，并行流程无效
	private final TransmittableThreadLocal<Boolean> isEndTL = new TransmittableThreadLocal<>();

	public void execute() throws Exception{
		Slot slot = this.getSlot();
		LOG.info("[{}]:[O]start component[{}] execution",slot.getRequestId(),this.getClass().getSimpleName());
		slot.addStep(new CmpStep(nodeId, name, CmpStepType.SINGLE));
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// process前置处理
		if (ObjectUtil.isNotNull(ComponentScanner.cmpAroundAspect)) {
			ComponentScanner.cmpAroundAspect.beforeProcess(this.getNodeId(), slot);
		}

		self.process();

		// process后置处理
		if (ObjectUtil.isNotNull(ComponentScanner.cmpAroundAspect)) {
			ComponentScanner.cmpAroundAspect.afterProcess(this.getNodeId(), slot);
		}

		stopWatch.stop();

		final long timeSpent = stopWatch.getTotalTimeMillis();
		// 性能统计
		if (ObjectUtil.isNotNull(monitorBus)) {
			CompStatistics statistics = new CompStatistics(this.getClass().getSimpleName(), timeSpent);
			monitorBus.addStatistics(statistics);
		}

		if (this instanceof NodeCondComponent) {
			String condNodeId = slot.getCondResult(this.getClass().getName());
			if (StrUtil.isNotBlank(condNodeId)) {
				Node thisNode = FlowBus.getNode(nodeId);
				Executable condExecutor = thisNode.getCondNode(condNodeId);
				if (ObjectUtil.isNotNull(condExecutor)) {
					condExecutor.execute(slotIndexTL.get());
				}
			}
		}

		LOG.debug("[{}]:componnet[{}] finished in {} milliseconds",slot.getRequestId(),this.getClass().getSimpleName(),timeSpent);
	}

	public abstract void process() throws Exception;

	/**
	 * 是否进入该节点
	 * @return boolean
	 */
	public boolean isAccess(){
		return true;
	}

	/**
	 * 出错是否继续执行(这个只适用于串行流程，并行节点不起作用)
	 * @return boolean
	 */
	public boolean isContinueOnError() {
		return false;
	}

	/**
	 * 是否结束整个流程(不往下继续执行)
	 * @return boolean
	 */
	public boolean isEnd() {
		Boolean isEnd = isEndTL.get();
		if(ObjectUtil.isNull(isEnd)){
			return false;
		} else {
			return isEndTL.get();
		}
	}

	//设置是否结束整个流程
	public void setIsEnd(boolean isEnd){
		this.isEndTL.set(isEnd);
	}

	public void removeIsEnd(){
		this.isEndTL.remove();
	}

	public NodeComponent setSlotIndex(Integer slotIndex) {
		this.slotIndexTL.set(slotIndex);
		return this;
	}

	public Integer getSlotIndex() {
		return this.slotIndexTL.get();
	}

	public void removeSlotIndex(){
		this.slotIndexTL.remove();
	}

	public <T extends Slot> T getSlot(){
		return DataBus.getSlot(this.slotIndexTL.get());
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public NodeComponent getSelf() {
		return self;
	}

	public void setSelf(NodeComponent self) {
		this.self = self;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NodeTypeEnum getType() {
		return type;
	}

	public void setType(NodeTypeEnum type) {
		this.type = type;
	}
}
