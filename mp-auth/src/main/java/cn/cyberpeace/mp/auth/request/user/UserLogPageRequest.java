package cn.cyberpeace.mp.auth.request.user;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName UserLogPageRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see PageRequest
 */
@Data
@ApiModel
public class UserLogPageRequest extends PageRequest {

    @ApiModelProperty(value = "username")
    private String username;

    @ApiModelProperty(value = "用户id")
    private String userid;

    @ApiModelProperty(value = "模块")
    private String module;

    @ApiModelProperty(value = "日志类型")
    private String type;


    public void ensure() {
        super.ensure();

        if(super.getPage() < 1) {
            super.setPage(1);
        }
        super.setPage(super.getPage() - 1);

        if(super.getPageSize() < 1) {
            super.setPageSize(10);
        }
    }

}
