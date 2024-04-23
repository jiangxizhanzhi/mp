package cn.cyberpeace.mp.devopscluster.utils;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtVm;
import cn.cyberpeace.mp.devopscluster.vo.ImagesPageVO;

import java.util.Comparator;

public class VmCompare implements Comparator<ImagesPageVO> {
    @Override
    public int compare(ImagesPageVO b1, ImagesPageVO b2) {
        // 获取bean2对象
        ExtVm bean2_1 = b1.getExtVm();
        ExtVm bean2_2 = b2.getExtVm();

        // 获取需要比较的字段值
        Integer field1 = bean2_1.getDisk();
        Integer field2 = bean2_2.getDisk();

        // 根据字段值进行比较
        // return field1.compareTo(field2); // 按照字符串排序
         return Integer.compare(field1, field2); // 按照数字排序
    }
}
