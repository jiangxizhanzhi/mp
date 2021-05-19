package cn.cyberpeace.mp.data.sandtable.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 组是晒盘的抽象概念，创建组用于查询，分析流量（组的业务含义是场景）
 */
@Data
@Builder
@AllArgsConstructor
public class GroupModel {

    private String groupId;

    /**
     * 物理机地址
     */
    private List<String> macIds;
}
