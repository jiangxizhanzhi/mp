package cn.cyberpeace.mp.devops.cluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ServiceConvertModel extends BasePageConvertModel{

    /**
     * 列表
     */
    private List<RealEquipment> modelList;

}
