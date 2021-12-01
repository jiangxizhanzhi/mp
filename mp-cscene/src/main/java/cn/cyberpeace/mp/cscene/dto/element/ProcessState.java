package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/29 20:14
 */
@Data
public class ProcessState {

    @ApiModelProperty("进程号")
    private String pid;

    @ApiModelProperty("内存占用")
    private Integer memory;

    @ApiModelProperty("命令")
    private String cmd;

    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty("创建时间")
    private String createTime;

}
