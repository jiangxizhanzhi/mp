package cn.cyberpeace.mp.situationaware.model.wazuh.rule;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("规则信息")
public class Rule {

    @ApiModelProperty(value = "ID")
    private int id;

    @ApiModelProperty(value = "级别")
    private int level;

    @ApiModelProperty(value = "文件名")
    private String filename;

    @ApiModelProperty(value = "相对路径")
    @JSONField(name = "relative_dirname")
    private String relativeDirname;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "pci_dss")
    @JSONField(name = "pci_dss")
    private List<String> pciDss;

    @ApiModelProperty(value = "gpg13")
    private List<String> gpg13;

    @ApiModelProperty(value = "gdpr")
    private List<String> gdpr;

    @ApiModelProperty(value = "hipaa")
    private List<String> hipaa;

    
    

    @ApiModelProperty(value = "tsc")
    private List<String> tsc;

    @ApiModelProperty(value = "mitre")
    private List<String> mitre;

    @ApiModelProperty(value = "组")
    private List<String> groups;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "规则详情")
    private Details details;

    @Data
    
    public static class Details {

        @ApiModelProperty(value = "触发")
        private String noalert;

        @ApiModelProperty(value = "类别")
        private String category;
    }

}
