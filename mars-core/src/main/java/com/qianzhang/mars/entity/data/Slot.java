/**
 *
 *
 * @author qianzhang
 *
 * @Date 2020/4/1
 */
package com.qianzhang.mars.entity.data;

/**
 * Slot的接口
 * @author qianzhang
 */
public interface Slot {
	<T> T getInput(String nodeId);

	<T> T getOutput(String nodeId);

	<T> void setInput(String nodeId,T t);

	<T> void setOutput(String nodeId,T t);

	<T> T getRequestData();

	<T> void setRequestData(T t);

	<T> T getResponseData();

	<T> void setChainReqData(String chainId, T t);

	<T> T getChainReqData(String chainId);

	<T> void setResponseData(T t);

	<T> T getData(String key);

	<T> void setData(String key, T t);

	<T> void setCondResult(String key, T t);

	<T> T getCondResult(String key);

	void addStep(CmpStep step);

	String printStep();

	void generateRequestId();

	String getRequestId();

	void setChainName(String chainName);

	String getChainName();

	void setException(Exception e);

	Exception getException();
}
