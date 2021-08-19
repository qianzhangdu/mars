
package com.qianzhang.mars.exception;

/**
 * 节点类型不支持异常
 * @author qianzhang
 * @since 2.6.0
 */
public class NodeTypeNotSupportException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/** 异常信息 */
	private String message;

	public NodeTypeNotSupportException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
