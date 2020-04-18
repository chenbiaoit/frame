/**
 * 
 */
package com.threeape.frame.config.aop;

import java.lang.annotation.*;

/**
 * 操作日志
 * @author Bill
 * @created 2017年7月16日 下午4:38:43
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

}
