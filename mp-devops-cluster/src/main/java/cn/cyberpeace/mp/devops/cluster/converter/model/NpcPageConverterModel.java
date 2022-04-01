package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.model.common.NpcModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网络分页返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NpcPageConverterModel extends BasePageConvertModel {

    /**
     * 列表
     */
    private List<NpcModel> modelList;
}
