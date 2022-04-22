package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.vo.OpenFlowVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路由分页返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenFlowPageConverterModel extends BasePageConvertModel {

    /**
     * 列表
     */
    private List<OpenFlowVO> openFlowVOS;

}
