package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Firewall;
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
public class FirewallPageConverterModel extends BasePageConvertModel {

    /**
     * 列表
     */
    private List<Firewall> modelList;

}
