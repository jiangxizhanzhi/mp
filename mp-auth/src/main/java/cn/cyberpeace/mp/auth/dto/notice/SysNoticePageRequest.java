package cn.cyberpeace.mp.auth.dto.notice;

import cn.cyberpeace.mp.auth.dto.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 公告分页请求信息
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

    @ApiModelProperty(value = "角色ids")
    private List<String> roleIds;

    @ApiModelProperty(value = "公告id列表")
    private String[] ids;

}
