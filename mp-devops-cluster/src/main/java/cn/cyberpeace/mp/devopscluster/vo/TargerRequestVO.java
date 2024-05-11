package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 获取云镜像列表的请求参数格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "获取云镜像列表的请求参数格式")
public class TargerRequestVO {

    @ApiModelProperty(value = "云类型：1002 混合云配置项菜单, 1003 私有云配置项菜单 1004 公有云配置项菜单")
    private String cloudType;

    @ApiModelProperty(value = "云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储)")
    private String cloudServiceType;

    @ApiModelProperty(value = "pageNum")
    private Integer page;

    @ApiModelProperty(value = "pageSize")
    private Integer perPage;

    @ApiModelProperty(value = "各级")
    private List<MpSysCodeMenuVO> codes;
}
