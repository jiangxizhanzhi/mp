package cn.cyberpeace.mp.front.scene.dto.event;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description: 节点监控统计信息
 * @Author zhouxinghuai
 * @Date：2021/11/22 14:03
 */
@Data
@Builder
@ApiModel(value = "节点监控信息")
public class ElementMonitorInfo {

    @ApiModelProperty(value = "节点基本信息-实例名称")
    private String name;

    @ApiModelProperty(value = "节点基本信息-IP地址")
    private List<String> ips;

    @ApiModelProperty(value = "节点基本信息-操作系统")
    private String os;

    @ApiModelProperty(value = "节点基本信息-镜像名称")
    private String imageName;

    @ApiModelProperty(value = "节点基本信息-物理节点")
    private String physicalNode;

    @ApiModelProperty(value = "节点基本信息-实例类型")
    private String instanceType;


    @ApiModelProperty(value = "节点资源-CPU")
    private int nodeCPUs;

    @ApiModelProperty(value = "节点资源-内存")
    private String memSize;

    @ApiModelProperty(value = "节点资源-硬盘")
    private String diskSize;


    @ApiModelProperty(value = "实时状态-进程")
    private int processCount;

    @ApiModelProperty(value = "实时状态-用户")
    private int userCount;

    @ApiModelProperty(value = "实时状态-端口")
    private int portsCount;

    @ApiModelProperty(value = "实时状态-网络连接")
    private int connectionCount;

    @ApiModelProperty(value = "实时状态-文件操作")
    private int fileOpCount;

    @ApiModelProperty(value = "实时状态-注册表操作")
    private int registryOpCount;

    @ApiModelProperty(value = "实时状态-环境变量操作")
    private int envOpCount;


}
