package cn.cyberpeace.mp.devopscluster.utils;

import java.util.Comparator;

public class RealCompare<T> implements Comparator<T> {
 
    @Override
    public int compare(Object obj1, Object obj2) {
        if (obj1 == null) {
            return 1;
        }
        if (obj2 == null) {
            return -1;
        }
        return (int)obj1 - (int)obj2;
    }
}