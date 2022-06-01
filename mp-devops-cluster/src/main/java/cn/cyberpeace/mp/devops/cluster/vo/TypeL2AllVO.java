package cn.cyberpeace.mp.devops.cluster.vo;

import cn.cyberpeace.mp.devops.cluster.model.common.TargetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeL2AllVO {

    /**
     * 数据列表
     */
    private List<TargetType> items;
}
