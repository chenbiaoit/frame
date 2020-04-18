package com.threeape.frame.util;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by lee on 2019/7/27.
 */
public class BusinessUtil {

    public static void assertTrue(boolean bool, ErrorInfo errorInfo) {
        if(!bool){
            throw new BusinessException(errorInfo);
        }
    }

    public static void assertFlase(boolean bool, ErrorInfo errorInfo) {
        if(bool){
            throw new BusinessException(errorInfo);
        }
    }

    public static void assertNotEmpty(String value, ErrorInfo errorInfo) {
        if(!StringUtils.isEmpty(value)){
            throw new BusinessException(errorInfo);
        }
    }

    public static void assertEmpty(String value, ErrorInfo errorInfo) {
        if(StringUtils.isEmpty(value) || value.equals("null")){
            throw new BusinessException(errorInfo);
        }
    }

    public static void notNull(Object obj, ErrorInfo errorInfo) {
        if(obj == null){
            throw new BusinessException(errorInfo);
        }
    }

    public static void isNull(Object obj, ErrorInfo errorInfo) {
        if(obj != null){
            throw new BusinessException(errorInfo);
        }
    }

    public static void isEmptyList(List list, ErrorInfo errorInfo) {
        if(list != null && !list.isEmpty()){
            throw new BusinessException(errorInfo);
        }
    }

    public static boolean isDateTime(String datetime){
        Pattern p = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1][0-9])|([2][0-4]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        return p.matcher(datetime).matches();
    }

}
