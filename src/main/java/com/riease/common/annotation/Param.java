/**
 * 
 */
package com.riease.common.annotation;

import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 用來宣告呼叫方法的參數
 * @author wesleyzhuang
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(PARAMETER)
public @interface Param {
	String value();
}
