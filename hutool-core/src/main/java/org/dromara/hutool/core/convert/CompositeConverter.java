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

package org.dromara.hutool.core.convert;

import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.bean.RecordUtil;
import org.dromara.hutool.core.convert.impl.*;
import org.dromara.hutool.core.lang.Opt;
import org.dromara.hutool.core.reflect.ConstructorUtil;
import org.dromara.hutool.core.reflect.TypeReference;
import org.dromara.hutool.core.reflect.TypeUtil;
import org.dromara.hutool.core.reflect.kotlin.KClassUtil;
import org.dromara.hutool.core.util.ObjUtil;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * 复合转换器，融合了所有支持类型和自定义类型的转换规则
 * <p>
 * 将各种类型Convert对象放入符合转换器，通过convert方法查找目标类型对应的转换器，将被转换对象转换之。
 * </p>
 * <p>
 * 在此类中，存放着默认转换器和自定义转换器，默认转换器是Hutool中预定义的一些转换器，自定义转换器存放用户自定的转换器。
 * </p>
 *
 * @author Looly
 */
public class CompositeConverter extends RegisterConverter {
	private static final long serialVersionUID = 1L;

	/**
	 * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例 没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载
	 */
	private static class SingletonHolder {
		/**
		 * 静态初始化器，由JVM来保证线程安全
		 */
		private static final CompositeConverter INSTANCE = new CompositeConverter();
	}

	/**
	 * 获得单例的 ConverterRegistry
	 *
	 * @return ConverterRegistry
	 */
	public static CompositeConverter getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * 构造
	 */
	public CompositeConverter() {
		super();
	}

	/**
	 * 转换值为指定类型
	 *
	 * @param type  类型
	 * @param value 值
	 * @return 转换后的值，默认为{@code null}
	 * @throws ConvertException 转换器不存在
	 */
	@Override
	public Object convert(final Type type, final Object value) throws ConvertException {
		return convert(type, value, null);
	}

	/**
	 * 转换值为指定类型<br>
	 * 自定义转换器优先
	 *
	 * @param <T>          转换的目标类型（转换器转换到的类型）
	 * @param type         类型
	 * @param value        值
	 * @param defaultValue 默认值
	 * @return 转换后的值
	 * @throws ConvertException 转换器不存在
	 */
	@Override
	public <T> T convert(final Type type, final Object value, final T defaultValue) throws ConvertException {
		return convert(type, value, defaultValue, true);
	}

	/**
	 * 转换值为指定类型
	 *
	 * @param <T>           转换的目标类型（转换器转换到的类型）
	 * @param type          类型目标
	 * @param value         被转换值
	 * @param defaultValue  默认值
	 * @param isCustomFirst 是否自定义转换器优先
	 * @return 转换后的值
	 * @throws ConvertException 转换器不存在
	 */
	@SuppressWarnings("unchecked")
	public <T> T convert(Type type, Object value, final T defaultValue, final boolean isCustomFirst) throws ConvertException {
		if (ObjUtil.isNull(value)) {
			return defaultValue;
		}
		if (TypeUtil.isUnknown(type)) {
			// 对于用户不指定目标类型的情况，返回原值
			if (null == defaultValue) {
				return (T) value;
			}
			type = defaultValue.getClass();
		}

		// issue#I7WJHH，Opt和Optional处理
		if (value instanceof Opt) {
			value = ((Opt<T>) value).get();
			if (ObjUtil.isNull(value)) {
				return defaultValue;
			}
		}
		if (value instanceof Optional) {
			value = ((Optional<T>) value).orElse(null);
			if (ObjUtil.isNull(value)) {
				return defaultValue;
			}
		}

		// value本身实现了Converter接口，直接调用
		if (value instanceof Converter) {
			return ((Converter) value).convert(type, value, defaultValue);
		}

		if (type instanceof TypeReference) {
			type = ((TypeReference<?>) type).getType();
		}

		// 标准转换器
		final Converter converter = getConverter(type, isCustomFirst);
		if (null != converter) {
			return converter.convert(type, value, defaultValue);
		}

		Class<T> rowType = (Class<T>) TypeUtil.getClass(type);
		if (null == rowType) {
			if (null != defaultValue) {
				rowType = (Class<T>) defaultValue.getClass();
			} else {
				throw new ConvertException("Can not get class from type: {}", type);
			}
		}

		// 特殊类型转换，包括Collection、Map、强转、Array等
		final T result = convertSpecial(type, rowType, value, defaultValue);
		if (null != result) {
			return result;
		}

		// 尝试转Bean
		if (BeanUtil.isWritableBean(rowType)) {
			return (T) BeanConverter.INSTANCE.convert(type, value);
		}

		// 无法转换
		throw new ConvertException("Can not convert from {}: [{}] to [{}]", value.getClass().getName(), value, type.getTypeName());
	}

	// ----------------------------------------------------------- Private method start

	/**
	 * 特殊类型转换<br>
	 * 包括：
	 *
	 * <pre>
	 * Collection
	 * Map
	 * 强转（无需转换）
	 * 数组
	 * </pre>
	 *
	 * @param <T>          转换的目标类型（转换器转换到的类型）
	 * @param type         类型
	 * @param value        值
	 * @param defaultValue 默认值
	 * @return 转换后的值
	 */
	@SuppressWarnings("unchecked")
	private <T> T convertSpecial(final Type type, final Class<T> rowType, final Object value, final T defaultValue) {
		if (null == rowType) {
			return null;
		}

		// 日期、java.sql中的日期以及自定义日期统一处理
		if (Date.class.isAssignableFrom(rowType)) {
			return DateConverter.INSTANCE.convert(type, value, defaultValue);
		}

		// 集合转换（含有泛型参数，不可以默认强转）
		if (Collection.class.isAssignableFrom(rowType)) {
			return (T) CollectionConverter.INSTANCE.convert(type, value, (Collection<?>) defaultValue);
		}

		// Map类型（含有泛型参数，不可以默认强转）
		if (Map.class.isAssignableFrom(rowType)) {
			return (T) MapConverter.INSTANCE.convert(type, value, (Map<?, ?>) defaultValue);
		}

		// issue#I6SZYB Entry类（含有泛型参数，不可以默认强转）
		if (Map.Entry.class.isAssignableFrom(rowType)) {
			return (T) EntryConverter.INSTANCE.convert(type, value);
		}

		// 默认强转
		if (rowType.isInstance(value)) {
			return (T) value;
		}

		// 原始类型转换
		if (rowType.isPrimitive()) {
			return PrimitiveConverter.INSTANCE.convert(type, value, defaultValue);
		}

		// 数字类型转换
		if (Number.class.isAssignableFrom(rowType)) {
			return NumberConverter.INSTANCE.convert(type, value, defaultValue);
		}

		// 枚举转换
		if (rowType.isEnum()) {
			return EnumConverter.INSTANCE.convert(type, value, defaultValue);
		}

		// 数组转换
		if (rowType.isArray()) {
			return ArrayConverter.INSTANCE.convert(type, value, defaultValue);
		}

		// Record
		if (RecordUtil.isRecord(rowType)) {
			return (T) RecordConverter.INSTANCE.convert(type, value);
		}

		// Kotlin Bean
		if (KClassUtil.isKotlinClass(rowType)) {
			return (T) KBeanConverter.INSTANCE.convert(type, value);
		}

		// issue#I7FQ29 Class
		if ("java.lang.Class".equals(rowType.getName())) {
			return (T) ClassConverter.INSTANCE.convert(type, value);
		}

		// 空值转空Bean
		if(ObjUtil.isEmpty(value)){
			// issue#3649 空值转空对象，则直接实例化
			return ConstructorUtil.newInstanceIfPossible(rowType);
		}

		// 表示非需要特殊转换的对象
		return null;
	}
	// ----------------------------------------------------------- Private method end
}
