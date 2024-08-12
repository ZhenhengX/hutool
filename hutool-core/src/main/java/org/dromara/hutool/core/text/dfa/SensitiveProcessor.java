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

package org.dromara.hutool.core.text.dfa;

/**
 * @author 肖海斌
 * 敏感词过滤处理器，默认按字符数替换成*
 */
public interface SensitiveProcessor {

	/**
	 * 敏感词过滤处理
	 * @param foundWord 敏感词匹配到的内容
	 * @return 敏感词过滤后的内容，默认按字符数替换成*
	 */
	default String process(final FoundWord foundWord) {
		final int length = foundWord.getFoundWord().length();
		final StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append("*");
		}
		return sb.toString();
	}
}
