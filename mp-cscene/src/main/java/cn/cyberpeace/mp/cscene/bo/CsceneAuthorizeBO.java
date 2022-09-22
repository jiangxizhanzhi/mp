package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CsceneAuthorizeBO {
    @ApiModelProperty(value = "场景模板授权类型 0:私有 1:授权所有 2:共享")
    private Integer authType;

    @ApiModelProperty(value = "场景模板授权用户列表")
    private List<SharedUser> sharedUsers;

    @Data
    public static class SharedUser {
        private String id;
        private String name;
        private String organization;
        private String organizationNameAll;
        private String username;
    }
}
