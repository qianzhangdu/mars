/**
 *
 *
 * @author qianzhang
 *
 * @Date 2020/4/1
 */
package com.qianzhang.mars.entity.flow;

import java.util.List;

/**
 * 并行器
 * @author qianzhang
 */
public class WhenCondition extends Condition{


	public WhenCondition(List<Executable> nodeList) {
		super(nodeList);
		super.setErrorResume(true);
	}

	public WhenCondition(List<Executable> nodeList, boolean errorResume) {
		super(nodeList);
		super.setErrorResume(errorResume);
	}

	public WhenCondition(Condition condition) {
		super(condition.getNodeList());
		super.setConditionType(condition.getConditionType());
		super.setGroup(condition.getGroup());
		super.setErrorResume(condition.isErrorResume());
	}

}
