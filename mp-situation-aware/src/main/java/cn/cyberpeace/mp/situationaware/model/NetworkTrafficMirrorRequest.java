package cn.cyberpeace.mp.situationaware.model;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.*;

/**
  *@Desciption 新增流量镜像的参数请求
  *@Author zhangjin
  *@Date 11/23/21 3:07 下午
**/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetworkTrafficMirrorRequest {
    /**
     * 虚拟机id列表
     */
    private List<String> vmInstanceIds = Lists.newArrayList();

    /**
     * 容器id列表
     */
    private List<String> containerInstanceIds = Lists.newArrayList();

    /**
     * 流量镜像名称列表,创建镜像返回的Id
     */
    private List<String> arkimeInstanceNames = Lists.newArrayList();
}
