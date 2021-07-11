package cn.cyberpeace.mp.cscene.dto.logfile;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LogFileDownloadUrlDto extends LogFileBasicDto {

    @ApiModelProperty(value = "下载链接")
    private String downloadUrl;
}
