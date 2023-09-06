package cn.cyberpeace.mp.auth.request.major;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.model.MajorModel;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class MajorRequest {
    @ApiModelProperty(value = "专业名称")
    private String majorName;

    @ApiModelProperty(value = "专业描述")
    private String majorDescription;

    public void paramCheck() {
        ParamCheck.lengthInScope(this.majorDescription, AuthBasei18nCode.MAJOR_DESCRIPTION, 1000);
        ParamCheck.notBlank(this.majorName, AuthBasei18nCode.MAJOR_NAME);
        ParamCheck.lengthInScope(this.majorName, AuthBasei18nCode.MAJOR_NAME, 60);
    }


    public MajorModel convertToModel() {
        MajorModel majorModel = new MajorModel();
        BeanUtils.copyProperties(this, majorModel);
        return majorModel;
    }
}
