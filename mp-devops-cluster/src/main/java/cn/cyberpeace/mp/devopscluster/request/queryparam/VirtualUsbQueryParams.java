package cn.cyberpeace.mp.devopscluster.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 虚拟USB通用查询条件
 * @Author: guquan
 * @Date：2023/4/15 14:53
 */
@Data
@ApiModel(value = "虚拟USB通用查询条件", parent = QueryParams.class)
@EqualsAndHashCode(callSuper = true)
public class VirtualUsbQueryParams extends QueryParams {
}
