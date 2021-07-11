package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ISOUploadVO {

    
    @ApiModelProperty("临时文件UUID")
    private String tmpdirUuid;

    
    @ApiModelProperty("上传云平台生成的文件名")
    private String fileName;
}
