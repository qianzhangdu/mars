package com.qianzhang.mars.entity.flow;

import com.qianzhang.mars.enums.ExecuteTypeEnum;

/**
 * 可执行器接口
 * 目前实现这个接口的有2个，node和chain
 * @author qianzhang
 */
public interface Executable {

    void execute(Integer slotIndex) throws Exception;

    ExecuteTypeEnum getExecuteType();

    String getExecuteName();
}
