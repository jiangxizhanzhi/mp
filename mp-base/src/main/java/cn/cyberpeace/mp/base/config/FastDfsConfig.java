package cn.cyberpeace.mp.base.config;

import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: FastDFS配置类
 * @Autho qiaokang
 * @Date：2021/6/23 20:14
 */
@Component
@ConfigurationProperties(prefix="mp.base.fastdfs")
@Slf4j
@Data
public class FastDfsConfig {

    /**
     * 最大连接数
     */
    private int maxStorageConnection;

    private String connectTimeoutInSeconds;

    private String networkTimeoutInSeconds;

    private String charset;

    private String httpTrackerHttpPort;

    private List<Group> groups;

    @Data
    public static class Group {

        private String groupName;

        private String trackerServers;

        private String fileServerAddr;

        private String innerFileServerAddr;

    }

    /**
     * 根据集群名称获取集群配置
     * @param fdfsGroupEnum
     * @return
     */
    public Group getGroupByName(FdfsGroupEnum fdfsGroupEnum) {
        FastDfsConfig.Group group = this.groups.stream()
                .filter(g -> fdfsGroupEnum.toString().equals(g.getGroupName()))
                .findFirst().orElse(new Group());

        return group;
    }

}
