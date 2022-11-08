package cn.cyberpeace.mp.cscene.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 截屏使用空间返回
 * @Author qiaokang
 * @Date：2021/9/13 15:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "截屏使用空间返回")
public class ScreenshotUsedSpaceResponse {

    public ScreenshotUsedSpaceResponse(Long usedSpace) {
        this.usedSpace = usedSpace;
    }

    /**
     * 已用空间
     */
    @ApiModelProperty("已用空间")
    private Long usedSpace;

    /**
     * 总共空间：默认1T
     */
    @ApiModelProperty("总共空间")
    private Long totalSpace = (long) 1024 * 1024 * 1024 * 1024;

}
