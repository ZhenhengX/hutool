/*
 * Copyright (c) 2013-2024 Hutool Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hutool.core.exception;

/**
 * 验证异常
 *
 * @author Looly
 */
public class ValidateException extends StatefulException {
	private static final long serialVersionUID = 6057602589533840889L;

	/**
	 * 构造
	 */
	public ValidateException() {
		super();
	}

	/**
	 * 构造
	 *
	 * @param e 异常
	 */
	public ValidateException(final Throwable e) {
		super(e);
	}

	/**
	 * 构造
	 *
	 * @param message 消息
	 */
	public ValidateException(final String message) {
		super(message);
	}

	/**
	 * 构造
	 *
	 * @param messageTemplate 消息模板
	 * @param params          参数
	 */
	public ValidateException(final String messageTemplate, final Object... params) {
		super(messageTemplate, params);
	}

	/**
	 * 构造
	 *
	 * @param message 消息
	 * @param cause   被包装的子异常
	 */
	public ValidateException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * 构造
	 *
	 * @param message            消息
	 * @param cause              被包装的子异常
	 * @param enableSuppression  是否启用抑制
	 * @param writableStackTrace 堆栈跟踪是否应该是可写的
	 */
	public ValidateException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * 构造
	 *
	 * @param cause           被包装的子异常
	 * @param messageTemplate 消息模板
	 * @param params          参数
	 */
	public ValidateException(final Throwable cause, final String messageTemplate, final Object... params) {
		super(cause, messageTemplate, params);
	}

	/**
	 * 构造
	 *
	 * @param status 状态码
	 * @param msg    消息
	 */
	public ValidateException(final int status, final String msg) {
		super(status, msg);
	}

	/**
	 * 构造
	 *
	 * @param status    状态码
	 * @param throwable 异常
	 */
	public ValidateException(final int status, final Throwable throwable) {
		super(status, throwable);
	}

	/**
	 * @param status    状态码
	 * @param msg       消息
	 * @param throwable 异常
	 */
	public ValidateException(final int status, final String msg, final Throwable throwable) {
		super(status, msg, throwable);
	}
}
