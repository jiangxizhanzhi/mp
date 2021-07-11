package cn.cyberpeace.mp.base.config.webmvc;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.ServletRequest;
import java.util.LinkedList;
import java.util.List;


public class CustomServletRequestDataBinder extends ServletRequestDataBinder {

    public CustomServletRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    public CustomServletRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    
    @Override
    protected void addBindValues(MutablePropertyValues mutablePropertyValues, ServletRequest request) {
        List<PropertyValue> propertyValueList = mutablePropertyValues.getPropertyValueList();
        List<PropertyValue> adds = new LinkedList<>();
        for (PropertyValue propertyValue : propertyValueList) {
            String name = propertyValue.getName();
            String camel = TextUtils.lineToHump(name);
            if (!name.equals(camel)) {
                adds.add(new PropertyValue(camel, propertyValue.getValue()));
            }
        }
        propertyValueList.addAll(adds);
    }
}
