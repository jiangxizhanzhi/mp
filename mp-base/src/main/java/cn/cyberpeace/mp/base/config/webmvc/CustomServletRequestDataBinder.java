package cn.cyberpeace.mp.base.config.webmvc;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.ServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义请求数据绑定处理类，主要解决将前端传递的下划线格式的字段自动转为驼峰格式
 */
public class CustomServletRequestDataBinder extends ServletRequestDataBinder {

    /**
     * 下划线正则匹配规则
     */
    private final Pattern underLinePattern = Pattern.compile("_(\\w)");

    public CustomServletRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    public CustomServletRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    /** 遍历请求参数对象 把请求参数的名转换成驼峰体
     * 重写addBindValues绑定数值的方法
     * @param mutablePropertyValues 请求参数列表
     * @param request 请求
     */
    @Override
    protected void addBindValues(MutablePropertyValues mutablePropertyValues, ServletRequest request) {
        List<PropertyValue> propertyValueList = mutablePropertyValues.getPropertyValueList();
        List<PropertyValue> adds = new LinkedList<>();
        for (PropertyValue propertyValue : propertyValueList) {
            String name = propertyValue.getName();
            String camel = this.underLineToCamel(name);
            if (!name.equals(camel)) {
                adds.add(new PropertyValue(camel, propertyValue.getValue()));
            }
        }
        propertyValueList.addAll(adds);
    }

    /**
     * 下划线转驼峰格式
     * @param value
     * @return
     */
    private String underLineToCamel(final String value) {
        Matcher matcher = underLinePattern.matcher(value);
        StringBuffer result = new StringBuffer();
        while (matcher.find()){
            matcher.appendReplacement(result,matcher.group(1).toUpperCase());
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
