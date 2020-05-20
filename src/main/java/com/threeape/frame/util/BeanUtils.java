package com.threeape.frame.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BeanUtils {
    private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);
    private BeanUtils() {
        throw new IllegalStateException("BeanUtils class");
    }

   /**
     * 用于model修改时的对象复制,把srcModel复制到destModel,srcModel中为null的字段不复制，同名且类型相同的属性才复制
     * 
     * @param srcModel
     *            表单提交的源对象
     * @param destModel
     *            数据库中的目标对象
     */
    public static void copyNotNullFields(Object srcModel, Object destModel) throws IntrospectionException,
            IllegalAccessException, InvocationTargetException{
        if (srcModel == null || destModel == null) {
            return;
        }
         invokePropertyCopy(srcModel, destModel);
    }

    private static void invokePropertyCopy(Object srcModel, Object destModel) throws IntrospectionException,
            IllegalAccessException, InvocationTargetException {
    	
        PropertyDescriptor[] srcDescriptors = Introspector.getBeanInfo(srcModel.getClass()).getPropertyDescriptors();
        PropertyDescriptor[] destDescriptors = Introspector.getBeanInfo(destModel.getClass()).getPropertyDescriptors();
        Map<String, PropertyDescriptor> destPropertyNameDescriptorMap = new HashMap<>();
        for (PropertyDescriptor destPropertyDescriptor : destDescriptors) {
            destPropertyNameDescriptorMap.put(destPropertyDescriptor.getName(), destPropertyDescriptor);
        }
        for (PropertyDescriptor srcDescriptor : srcDescriptors) {
            PropertyDescriptor destDescriptor = destPropertyNameDescriptorMap.get(srcDescriptor.getName());
            // 类型相同的属性才复制
            if (destDescriptor != null && destDescriptor.getPropertyType() == srcDescriptor.getPropertyType()
                    && destDescriptor.getPropertyType() != Class.class) {
                Object val = srcDescriptor.getReadMethod().invoke(srcModel);
                if (val != null && destDescriptor.getWriteMethod() != null) {
                    destDescriptor.getWriteMethod().invoke(destModel, val);
                }
            }
        }

    }

    /**
     * 根据属性名获取属性值
     * */
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


    /**
     * 将map转成bean
     * @param map
     * @param obj
     */
    public static void transMapBean(Map<String, Object> map, Object obj) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }
            }

        } catch (Exception e) {
            logger.error("transMap2Bean",e);
        }
    }

    public static Map<String, String> transBeanMapStr(Object obj) {
        if(obj == null){
            return null;
        }
        Map<String, String> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    String value = "";
                    if(getter.invoke(obj) != null){
                        value = getter.invoke(obj).toString();
                    }
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            logger.error("transBean2Map",e);
        }
        return map;
    }
}
