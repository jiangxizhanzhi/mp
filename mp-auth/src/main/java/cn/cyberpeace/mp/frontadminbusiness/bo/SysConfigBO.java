package cn.cyberpeace.mp.frontadminbusiness.bo;

import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysConfigBO extends ModelBO<SysConfigModel, SysConfigBO> {
    @ApiModelProperty(value = "租户")
    private String tenant;

    @ApiModelProperty(value = "配置名")
    private String keyName;

    @ApiModelProperty(value = "配置值")
    private String value;

    @ApiModelProperty(value = "初始值")
    private String initValue;

    @Override
    public SysConfigModel createModel() {
        return new SysConfigModel();
    }
}
