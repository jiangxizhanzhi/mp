package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.TypeItemModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 类型模型，仅支持role
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class TypeIdIntegerCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * 数据列表
     */
    private List<TypeItemModel> items;

}
