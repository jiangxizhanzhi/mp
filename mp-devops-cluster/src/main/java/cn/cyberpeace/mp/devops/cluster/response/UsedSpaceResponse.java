package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 使用空间返回
 * @Author qiaokang
 * @Date：2021/9/13 15:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "使用空间返回")
public class UsedSpaceResponse {

    public UsedSpaceResponse(Long userSpace) {
        this.userSpace = userSpace;
    }

    /**
     * 已用空间
     */
    @ApiModelProperty("已用空间")
    private Long userSpace;

    /**
     * 总共空间：默认10G
     */
    @ApiModelProperty("总共空间")
    private Long totalSpace = 10 * 1024 * 1024 * 1024 * 1024L;

}
