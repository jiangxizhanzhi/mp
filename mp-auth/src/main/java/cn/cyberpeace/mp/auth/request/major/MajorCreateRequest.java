package cn.cyberpeace.mp.auth.request.major;

import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.MajorModel;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


/**
 * 专业信息新增请求
 */
@Data
@ApiModel(value = "专业信息创建请求信息")
public class MajorCreateRequest extends MajorRequest {


}
