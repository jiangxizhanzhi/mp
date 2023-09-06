package cn.cyberpeace.mp.auth.request.major;


import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;

@Data
@ApiModel("专业信息更新请求")
public class MajorUpdateRequest extends MajorRequest {

    @ApiModelProperty(value = "专业id")
    private String id;

    @Override
    public void paramCheck() {
        super.paramCheck();
        ParamCheck.notBlank(this.id, AuthBasei18nCode.MAJOR_ID);
    }

}
