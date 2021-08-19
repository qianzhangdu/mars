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
 * 串行器
 * @author qianzhang
 */
public class ThenCondition extends Condition {

	public ThenCondition(List<Executable> nodeList) {
		super(nodeList);
	}

	public ThenCondition(Condition condition){
		super(condition.getNodeList());
		super.setConditionType(condition.getConditionType());
		super.setGroup(condition.getGroup());
		super.setErrorResume(condition.isErrorResume());
	}

}
