package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 录屏分页查询请求
 * @Author qiaokang
 * @Date：2021/9/13 16:33
 */
@Data
@ApiModel("录屏分页查询请求")
public class ScreenrecordPageRequest {

    /**
     * 搜索关键词
     */
    @ApiModelProperty(value = "搜索关键词")
    private String search;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    /**
     * 分页页码
     */
    @ApiModelProperty(value = "分页页码")
    private Integer page;

    /**
     * 分页每页条数
     */
    @ApiModelProperty(value = "分页每页条数")
    private Integer pageSize;

}
