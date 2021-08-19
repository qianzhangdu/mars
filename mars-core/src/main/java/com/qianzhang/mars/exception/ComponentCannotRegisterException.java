
package com.qianzhang.mars.exception;

/**
 * 流程规则主要执行器类
 * @author qianzhang
 * @since 2.5.3
 */
public class ComponentCannotRegisterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/** 异常信息 */
	private String message;

	public ComponentCannotRegisterException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
