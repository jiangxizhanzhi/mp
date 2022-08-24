package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.model.common.TargetType;
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
