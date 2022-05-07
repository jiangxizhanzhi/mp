package cn.cyberpeace.mp.cscene.dto.log;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/6 22:53
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LogFileDownloadUrlDto extends LogFileBasicDto {

    @ApiModelProperty(value = "下载链接")
    private String downloadUrl;
}
