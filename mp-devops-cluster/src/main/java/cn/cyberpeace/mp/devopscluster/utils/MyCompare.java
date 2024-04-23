package cn.cyberpeace.mp.devopscluster.utils;

import lombok.Data;
import org.apache.commons.beanutils.PropertyUtils;

import java.util.Comparator;

@Data
public class MyCompare<T> implements Comparator<T> {
 
    private String param;
 
    private Boolean flag;
 
    private Comparator<Object> comparator;
 
    public MyCompare(String param, Boolean flag) {
        this.setParam(param);
        this.setFlag(flag);
        this.comparator = new RealCompare<>();
    }
 
    @Override
    public int compare(T o1, T o2) {
        try {
            Object value1 = PropertyUtils.getProperty(o1, this.param);
            Object value2 = PropertyUtils.getProperty(o2, this.param);
 
            if (value1 == null) {
                return 1;
            }
            if (value2 == null) {
                return -1;
            }
 
            Comparator<Object> comparator = this.comparator;
            return flag ? comparator.compare(value1, value2) : -comparator.compare(value1, value2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}