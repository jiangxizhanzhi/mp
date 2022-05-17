package cn.cyberpeace.mp.devops.cluster.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

/**
 * 分页基础模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel
public class CloudPlatformPageModel<T> extends BaseCloudPlatformModel {

    /**
     * 数据列表
     */
    private List<T> list;


}
