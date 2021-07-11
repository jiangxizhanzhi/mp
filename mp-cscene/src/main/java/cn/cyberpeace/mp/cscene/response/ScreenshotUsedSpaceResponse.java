package cn.cyberpeace.mp.cscene.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "截屏使用空间返回")
public class ScreenshotUsedSpaceResponse {

    public ScreenshotUsedSpaceResponse(Long usedSpace) {
        this.usedSpace = usedSpace;
    }

    
    @ApiModelProperty("已用空间")
    private Long usedSpace;

    
    @ApiModelProperty("总共空间")
    private Long totalSpace = (long) 1024 * 1024 * 1024 * 1024;

}
