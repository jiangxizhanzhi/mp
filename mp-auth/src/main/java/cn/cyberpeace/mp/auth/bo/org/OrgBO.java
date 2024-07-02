package cn.cyberpeace.mp.auth.bo.org;

import cn.cyberpeace.mp.auth.model.OrgModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @ClassName OrgBO
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/9/1 17:20
 * @Version 1.0.0
 * @see OrgModel
 */
@Data
public class OrgBO extends OrgModel {

    @ApiModelProperty(value = "路径")
    private String path;



    public static OrgBO fromModel(OrgModel orgModel, String path) {
        OrgBO orgBO = new OrgBO();
        BeanUtils.copyProperties(orgModel, orgBO);
        orgBO.setPath(path);
        return orgBO;
    }
}
