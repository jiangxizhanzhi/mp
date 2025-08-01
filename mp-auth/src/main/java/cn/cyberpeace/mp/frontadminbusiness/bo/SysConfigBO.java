package cn.cyberpeace.mp.frontadminbusiness.bo;

import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

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

    @ApiModelProperty(value = "前端组件类型")
    private String type;

    @ApiModelProperty(value = "范围")
    private String range;

    @ApiModelProperty(value = "select组件的下拉")
    private Option options;

    @ApiModelProperty(value = "单位")
    private String unit;

    @Override
    public SysConfigModel createModel() {
        return new SysConfigModel();
    }


    @Data
    @Accessors(chain = true)
    public static class Option {
        @ApiModelProperty(value = "value")
        private String value;
        @ApiModelProperty(value = "label")
        private String label;
    }
}
