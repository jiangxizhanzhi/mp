package cn.cyberpeace.mp.frontadmin.request.notice;

import cn.cyberpeace.mp.frontadmin.request.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName SysNoticePageRequest
 * @Description 公告分页请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see PageRequest
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class SysNoticePageRequest extends PageRequest {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "组织ids")
    private List<String> orgIds;

    //@ApiModelProperty(value = "用户ids")
    //private List<String> userIds;

    @ApiModelProperty(value = "角色ids")
    private List<String> roleIds;

    @ApiModelProperty(value = "公告id列表")
    private String[] ids;

}
