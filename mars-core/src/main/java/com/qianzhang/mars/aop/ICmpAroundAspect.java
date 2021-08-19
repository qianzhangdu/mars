/**
 *
 *
 * @author qianzhang
 *
 * @Date 2020/10/22
 */
package com.qianzhang.mars.aop;

import com.qianzhang.mars.entity.data.Slot;

/**
 * 全局组件拦截器接口
 * 实现这个接口并注入到spring上下文即可
 * @author qianzhang
 */
public interface ICmpAroundAspect {

    void beforeProcess(String nodeId, Slot slot);

    void afterProcess(String nodeId, Slot slot);
}
