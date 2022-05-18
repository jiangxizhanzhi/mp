package cn.cyberpeace.mp.situationaware.model.wazuh.syscollector;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/25 09:32
 */
@Data
@ApiModel("操作系统")
public class OS {

    private OSDetail os;

    private Scan scan;

    @ApiModelProperty(value = "版本")
    @JSONField(name = "version")
    private String version;

    @ApiModelProperty(value = "主机名")
    @JSONField(name = "hostname")
    private String hostname;

    @ApiModelProperty(value = "发布版本")
    private String release;

    @ApiModelProperty(value = "架构")
    private String architecture;

    @ApiModelProperty(value = "系统名称")
    private String sysname;

    @ApiModelProperty(value = "探针ID")
    @JSONField(name = "agent_id")
    private String agentId;

    @Data
    @ApiModel("OSDetail")
    public static class OSDetail {

        @ApiModelProperty(value = "大版本号")
        private String major;

        @ApiModelProperty(value = "小版本号")
        private String minor;

        @ApiModelProperty(value = "操作系统名称")
        private String name;

        @ApiModelProperty(value = "平台")
        private String platform;

        @ApiModelProperty(value = "版本号")
        private String version;
    }

}
