package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Network;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 容器创建模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ContainerCreateCloudPlatformModel extends BaseModel implements Serializable {

    /**
     * 容器名称
     */
    private String name;

    /**
     * 靶标镜像名称
     */
    private String image;

    /**
     * cpu数量
     */
    private Integer cpu;

    /**
     * 虚拟内存大小（MB）
     */
    private Integer ram;

    /**
     * 虚拟磁盘大小（GB）
     */
    private Integer disk;

    /**
     * 用户名称
     */
    private String requestUser;

    /**
     * 是否启用交互模式
     */
    private Boolean interactive;

    /**
     * 是否创建tty
     */
    private Boolean tty;

    /**
     * 创建后是否运行
     */
    private Boolean run;

    /**
     * 是否开启特权模式
     */
    private Boolean privilege;

    /**
     * 是否需要保持网卡顺序
     */
    private Boolean keepNicsSequence;

    /**
     * 容器启动命令
     */
    private String command;

    /**
     * 容器启动后执行的命令
     */
    private String postCommand;

    /**
     * 工作目录
     */
    private String workdir;

    /**
     * 环境变量
     */
    private String environment;

    /**
     * 指定主机名
     */
    private String hostname;

    /**
     * 指定主机创建
     */
    private String host;

    /**
     * 网络
     */
    private List<Network> networks;
}
