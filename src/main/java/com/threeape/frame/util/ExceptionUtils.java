package com.threeape.frame.util;

/**
 * 异常信息公共类
 * @author Bill
 */
public class ExceptionUtils {
	
	
	/**
	 * 传入异常消息返回报错路径文本
	 * @param ex
	 * @return
	 */
	public static String getExceptionAllinformation(Throwable ex){
        String sOut = ex.toString()+"\r\n";
        StackTraceElement[] trace = ex.getStackTrace();
        for (StackTraceElement s : trace) {
            sOut += "\tat " + s + "\r\n";
        }
        return sOut;
    }
}
