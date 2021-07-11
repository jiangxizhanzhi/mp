package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class ParamUtils {


    
    public static String wrapperGetParamsByFieldsAndObject(List<Field> fieldList, Object object) {
        String result = "";
        StringBuffer joinerStr = new StringBuffer();
        final BeanWrapper objWrapper = new BeanWrapperImpl(object);
        fieldList.stream().forEach(field -> {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldVal = objWrapper.getPropertyValue(fieldName);
            if (Objects.nonNull(fieldVal)) {
                joinerStr.append(Joiner.on("&").join(Joiner.on("=").join(fieldName, fieldVal), ""));
            }
        });
        result = StringUtils.substringBeforeLast(joinerStr.toString(),"&");
        return StringUtils.isBlank(result) ? "" : "?" + result;
    }

    
    public static String wrapperGetParamsByFieldsAndObject(List<Field> fieldList, Object object,Boolean humpToLine) {
        String result = "";
        StringBuffer joinerStr = new StringBuffer();
        final BeanWrapper objWrapper = new BeanWrapperImpl(object);
        fieldList.stream().forEach(field -> {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldVal = objWrapper.getPropertyValue(fieldName);
            if (humpToLine){
                fieldName = TextUtils.humpToLine(fieldName);
            }
            if (Objects.nonNull(fieldVal)) {
                joinerStr.append(Joiner.on("&").join(Joiner.on("=").join(fieldName, fieldVal), ""));
            }
        });
        result = StringUtils.substringBeforeLast(joinerStr.toString(),"&");
        return StringUtils.isBlank(result) ? "" : "?" + result;
    }

    
    public static List<Field> getFieldsByObject(Object object) {
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            fieldList.addAll(Arrays.asList(fields));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    
    public static String wrapperGetParamsByObject(Object object){
        List<Field> fieldList = getFieldsByObject(object);
        return wrapperGetParamsByFieldsAndObject(fieldList,object);
    }

    
    public static String wrapperGetParamsByObject(Object object,String... ignoreFields){
        List<Field> fieldList = getFieldsByObject(object);
        fieldList = fieldList.stream().filter(field -> Arrays.stream(ignoreFields).noneMatch(f ->field.getName().equals(f))).collect(Collectors.toList());
        return wrapperGetParamsByFieldsAndObject(fieldList,object,Boolean.FALSE);
    }

    
    public static String wrapperGetParamsByObject(Boolean humpToLine,Object object,String... ignoreFields){
        List<Field> fieldList = getFieldsByObject(object);
        fieldList = fieldList.stream().filter(field -> Arrays.stream(ignoreFields).noneMatch(f ->field.getName().equals(f))).collect(Collectors.toList());
        return wrapperGetParamsByFieldsAndObject(fieldList,object,humpToLine);
    }
    
}
