package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.model.common.TargetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeL2AllResponse {

    /**
     * 数据列表
     */
    private List<TargetType> items;
}
