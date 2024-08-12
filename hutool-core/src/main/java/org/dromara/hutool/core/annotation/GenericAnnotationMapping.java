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

package org.dromara.hutool.core.annotation;

import org.dromara.hutool.core.reflect.ClassUtil;
import org.dromara.hutool.core.reflect.method.MethodUtil;
import org.dromara.hutool.core.text.CharSequenceUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * {@link AnnotationMapping}的基本实现，仅仅是简单包装了注解对象
 *
 * @author huangchengxing
 * @since 6.0.0
 */
public class GenericAnnotationMapping implements AnnotationMapping<Annotation> {

	/**
	 * 创建一个通用注解包装类
	 *
	 * @param annotation 注解对象
	 * @param isRoot     是否根注解
	 * @return {@code GenericAnnotationMapping}实例
	 */
	public static GenericAnnotationMapping create(final Annotation annotation, final boolean isRoot) {
		return new GenericAnnotationMapping(annotation, isRoot);
	}

	private final Annotation annotation;
	private final boolean isRoot;
	private final Method[] attributes;

	/**
	 * 创建一个通用注解包装类
	 *
	 * @param annotation 注解对象
	 * @param isRoot     是否根注解
	 */
	GenericAnnotationMapping(final Annotation annotation, final boolean isRoot) {
		this.annotation = Objects.requireNonNull(annotation);
		this.isRoot = isRoot;
		this.attributes = AnnotationUtil.getAnnotationAttributes(annotation.annotationType());
	}

	/**
	 * 当前注解是否为根注解
	 *
	 * @return 是否
	 */
	@Override
	public boolean isRoot() {
		return isRoot;
	}

	/**
	 * 获取注解对象
	 *
	 * @return 注解对象
	 */
	@Override
	public Annotation getAnnotation() {
		return annotation;
	}

	/**
	 * 同{@link #getAnnotation()}
	 *
	 * @return 注解对象
	 */
	@Override
	public Annotation getResolvedAnnotation() {
		return getAnnotation();
	}

	/**
	 * 总是返回{@code false}
	 *
	 * @return {@code false}
	 */
	@Override
	public boolean isResolved() {
		return false;
	}

	/**
	 * 获取注解原始属性
	 *
	 * @return 注解属性
	 */
	@Override
	public Method[] getAttributes() {
		return attributes;
	}

	/**
	 * 获取属性值
	 *
	 * @param attributeName 属性名称
	 * @param attributeType 属性类型
	 * @param <R>           返回值类型
	 * @return 属性值
	 */
	@Override
	public <R> R getAttributeValue(final String attributeName, final Class<R> attributeType) {
		return Stream.of(attributes)
			.filter(attribute -> CharSequenceUtil.equals(attribute.getName(), attributeName))
			.filter(attribute -> ClassUtil.isAssignable(attributeType, attribute.getReturnType()))
			.findFirst()
			.map(method -> MethodUtil.invoke(annotation, method))
			.map(attributeType::cast)
			.orElse(null);
	}

	/**
	 * 获取解析后的属性值
	 *
	 * @param attributeName 属性名称
	 * @param attributeType 属性类型
	 * @param <R>           返回值类型
	 * @return 属性值
	 */
	@Override
	public <R> R getResolvedAttributeValue(final String attributeName, final Class<R> attributeType) {
		return getAttributeValue(attributeName, attributeType);
	}

	/**
	 * 比较两个实例是否相等
	 *
	 * @param o 对象
	 * @return 是否
	 */
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final GenericAnnotationMapping that = (GenericAnnotationMapping)o;
		return isRoot == that.isRoot && annotation.equals(that.annotation);
	}

	/**
	 * 获取实例哈希值
	 *
	 * @return 哈希值
	 */
	@Override
	public int hashCode() {
		return Objects.hash(annotation, isRoot);
	}
}
