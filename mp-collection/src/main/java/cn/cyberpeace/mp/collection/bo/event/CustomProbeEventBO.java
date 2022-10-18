package cn.cyberpeace.mp.collection.bo.event;

import cn.cyberpeace.mp.collection.bo.base.BaseEventBO;
import lombok.Data;

@Data
public class CustomProbeEventBO extends BaseEventBO {
    /**
     * 输出数据
     */
    private String rawOutPut;

    /**
     * 探针id
     */
    private String probeId;
}
