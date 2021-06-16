package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow")
public class FlowModel extends BaseModel {

    /**
     * 流量标题
     */
    private String title;

    /**
     * 流量描述
     */
    @DefaultStr
    private String flowDesc;

    /**
     * 交互类型：0 背景流量，1 攻击流量，2 全流量
     */
    private int interactiveType;

    /**
     * 流量发生平台：0 通用，1 北京流量发生器
     */
    private int flowPlatform;

    /**
     * 资源类型：0 pcap包，1 脚本
     */
    private int resourceType;

    /**
     * pcap文件地址
     */
    @DefaultStr
    private String pcapFile;

    /**
     * 脚本文件地址
     */
    @DefaultStr
    private String scriptFile;

}
