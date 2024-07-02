package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

/**
 * @ClassName CloudPlatformPageResult
 * @Description 分页基础模型，对应云平台接口返回信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPlatformResult
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel
public class CloudPlatformPageResult<T> extends BaseCloudPlatformResult {

    /**
     * 数据列表
     */
    private List<T> list;


}
