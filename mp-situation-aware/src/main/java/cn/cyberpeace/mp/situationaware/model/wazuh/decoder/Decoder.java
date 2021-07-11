package cn.cyberpeace.mp.situationaware.model.wazuh.decoder;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



@Data
@ApiModel("解析器信息")
public class Decoder {

    @ApiModelProperty(value = "文件名")
    private String filename;

    @ApiModelProperty(value = "相对路径")
    @JSONField(name = "relative_dirname")
    private String relativeDirname;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "位置")
    private int position;

    @ApiModelProperty(value = "详细信息")
    private Details details;

    @Data
    
    public static class Details {

        @ApiModelProperty(value = "上级解析器")
        private String parent;

        @ApiModelProperty(value = "前置匹配")
        private Prematch prematch;

        @ApiModelProperty(value = "正则")
        private Regex regex;

        @ApiModelProperty(value = "排序")
        private String order;

        @ApiModelProperty(value = "程序名")
        private ProgramName programName;
    }

    @Data
    
    public static class Prematch {

        @ApiModelProperty(value = "模式")
        private String pattern;

        @ApiModelProperty(value = "偏移")
        private String offset;

    }


    @Data
    public static class ProgramName {

        @ApiModelProperty(value = "模式")
        private String pattern;

    }

    @Data
    
    public static class Regex {

        @ApiModelProperty(value = "模式")
        private String pattern;

        @ApiModelProperty(value = "偏移")
        private String offset;

    }

}
