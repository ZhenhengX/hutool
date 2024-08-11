/*
 * Copyright (c) 2024 looly(loolly@aliyun.com)
 * Hutool is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          https://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.dromara.hutool.core.util;

import org.dromara.hutool.core.io.CharsetDetector;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.*;

/**
 * 字符集工具类
 *
 * @author looly
 */
public class CharsetUtil {

	/**
	 * US_ASCII
	 */
	public static final String NAME_US_ASCII = "US_ASCII";
	/**
	 * ISO-8859-1
	 */
	public static final String NAME_ISO_8859_1 = "ISO-8859-1";
	/**
	 * UTF-8
	 */
	public static final String NAME_UTF_8 = "UTF-8";
	/**
	 * GBK
	 */
	public static final String NAME_GBK = "GBK";

	/**
	 * US_ASCII
	 */
	public static final Charset US_ASCII = StandardCharsets.US_ASCII;
	/**
	 * ISO-8859-1
	 */
	public static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;
	/**
	 * UTF-8
	 */
	public static final Charset UTF_8 = StandardCharsets.UTF_8;
	/**
	 * GBK
	 */
	public static final Charset GBK;

	static {
		//避免不支持GBK的系统中运行报错 issue#731
		Charset _GBK = null;
		try {
			_GBK = Charset.forName(NAME_GBK);
		} catch (final UnsupportedCharsetException e) {
			//ignore
		}
		GBK = _GBK;
	}

	/**
	 * 转换为Charset对象
	 *
	 * @param charsetName 字符集，为空则返回默认字符集
	 * @return Charset
	 * @throws UnsupportedCharsetException 编码不支持
	 */
	public static Charset charset(final String charsetName) throws UnsupportedCharsetException {
		return StrUtil.isBlank(charsetName) ? Charset.defaultCharset() : Charset.forName(charsetName);
	}

	/**
	 * 解析字符串编码为Charset对象，解析失败返回系统默认编码
	 *
	 * @param charsetName 字符集，为空则返回默认字符集
	 * @return Charset
	 * @since 5.2.6
	 */
	public static Charset parse(final String charsetName) {
		return parse(charsetName, Charset.defaultCharset());
	}

	/**
	 * 解析字符串编码为Charset对象，解析失败返回默认编码
	 *
	 * @param charsetName    字符集，为空则返回默认字符集
	 * @param defaultCharset 解析失败使用的默认编码
	 * @return Charset
	 * @since 5.2.6
	 */
	public static Charset parse(final String charsetName, final Charset defaultCharset) {
		if (StrUtil.isBlank(charsetName)) {
			return defaultCharset;
		}

		Charset result;
		try {
			result = Charset.forName(charsetName);
		} catch (final UnsupportedCharsetException e) {
			result = defaultCharset;
		}

		return result;
	}

	/**
	 * 转换字符串的字符集编码
	 *
	 * @param source      字符串
	 * @param srcCharset  源字符集，默认ISO-8859-1
	 * @param destCharset 目标字符集，默认UTF-8
	 * @return 转换后的字符集
	 */
	public static String convert(final String source, final String srcCharset, final String destCharset) {
		return convert(source, Charset.forName(srcCharset), Charset.forName(destCharset));
	}

	/**
	 * 转换字符串的字符集编码<br>
	 * 当以错误的编码读取为字符串时，打印字符串将出现乱码。<br>
	 * 此方法用于纠正因读取使用编码错误导致的乱码问题。<br>
	 * 例如，在Servlet请求中客户端用GBK编码了请求参数，我们使用UTF-8读取到的是乱码，此时，使用此方法即可还原原编码的内容
	 * <pre>
	 * 客户端 -》 GBK编码 -》 Servlet容器 -》 UTF-8解码 -》 乱码
	 * 乱码 -》 UTF-8编码 -》 GBK解码 -》 正确内容
	 * </pre>
	 *
	 * @param source      字符串
	 * @param srcCharset  源字符集，默认ISO-8859-1
	 * @param destCharset 目标字符集，默认UTF-8
	 * @return 转换后的字符集
	 */
	public static String convert(final String source, Charset srcCharset, Charset destCharset) {
		if (null == srcCharset) {
			srcCharset = ISO_8859_1;
		}

		if (null == destCharset) {
			destCharset = UTF_8;
		}

		if (StrUtil.isBlank(source) || srcCharset.equals(destCharset)) {
			return source;
		}
		return new String(source.getBytes(srcCharset), destCharset);
	}

	/**
	 * 转换文件编码<br>
	 * 此方法用于转换文件编码，读取的文件实际编码必须与指定的srcCharset编码一致，否则导致乱码
	 *
	 * @param file        文件
	 * @param srcCharset  原文件的编码，必须与文件内容的编码保持一致
	 * @param destCharset 转码后的编码
	 * @return 被转换编码的文件
	 * @since 3.1.0
	 */
	public static File convert(final File file, final Charset srcCharset, final Charset destCharset) {
		final String str = FileUtil.readString(file, srcCharset);
		return FileUtil.writeString(str, file, destCharset);
	}

	/**
	 * 系统字符集编码，如果是Windows，则默认为GBK编码，否则取 {@link CharsetUtil#defaultCharsetName()}
	 *
	 * @return 系统字符集编码
	 * @see CharsetUtil#defaultCharsetName()
	 * @since 3.1.2
	 */
	public static String systemCharsetName() {
		return systemCharset().name();
	}

	/**
	 * 系统字符集编码，如果是Windows，则默认为GBK编码，否则取 {@link CharsetUtil#defaultCharsetName()}
	 *
	 * @return 系统字符集编码
	 * @see CharsetUtil#defaultCharsetName()
	 * @since 3.1.2
	 */
	public static Charset systemCharset() {
		return FileUtil.isWindows() ? GBK : defaultCharset();
	}

	/**
	 * 系统默认字符集编码
	 *
	 * @return 系统字符集编码
	 */
	public static String defaultCharsetName() {
		return defaultCharset().name();
	}

	/**
	 * 系统默认字符集编码
	 *
	 * @return 系统字符集编码
	 */
	public static Charset defaultCharset() {
		return Charset.defaultCharset();
	}

	/**
	 * 探测编码<br>
	 * 注意：此方法会读取流的一部分，然后关闭流，如重复使用流，请使用使用支持reset方法的流
	 *
	 * @param in       流，使用后关闭此流
	 * @param charsets 需要测试用的编码，null或空使用默认的编码数组
	 * @return 编码
	 * @see CharsetDetector#detect(InputStream, Charset...)
	 * @since 5.7.10
	 */
	public static Charset detect(final InputStream in, final Charset... charsets) {
		return CharsetDetector.detect(in, charsets);
	}

	/**
	 * 探测编码<br>
	 * 注意：此方法会读取流的一部分，然后关闭流，如重复使用流，请使用使用支持reset方法的流
	 *
	 * @param bufferSize 自定义缓存大小，即每次检查的长度
	 * @param in         流，使用后关闭此流
	 * @param charsets   需要测试用的编码，null或空使用默认的编码数组
	 * @return 编码
	 * @see CharsetDetector#detect(int, InputStream, Charset...)
	 * @since 5.7.10
	 */
	public static Charset detect(final int bufferSize, final InputStream in, final Charset... charsets) {
		return CharsetDetector.detect(bufferSize, in, charsets);
	}

	/**
	 * 创建一个新的CharsetEncoder实例，配置指定的字符集和错误处理策略。
	 *
	 * @param charset 指定的字符集，不允许为null。
	 * @param action  对于不合法的字符或无法映射的字符的处理策略，不允许为null。
	 * @return 配置好的CharsetEncoder实例。
	 * @since 6.0.0
	 */
	public static CharsetEncoder newEncoder(final Charset charset, final CodingErrorAction action) {
		return Assert.notNull(charset)
			.newEncoder()
			.onMalformedInput(action)
			.onUnmappableCharacter(action);
	}

	/**
	 * 创建一个新的CharsetDecoder实例，配置指定的字符集和错误处理行为。
	 *
	 * @param charset 指定的字符集，不允许为null。
	 * @param action  当遇到不合法的字符编码或不可映射字符时采取的行动，例如忽略、替换等。
	 * @return 配置好的CharsetDecoder实例，用于解码字符。
	 * @since 6.0.0
	 */
	public static CharsetDecoder newDecoder(final Charset charset, final CodingErrorAction action) {
		return Assert.notNull(charset)
			.newDecoder()
			.onMalformedInput(action)
			.onUnmappableCharacter(action)
			// 设置遇到无法解码的字符时的替换字符串。
			.replaceWith("?");
	}
}
