package cn.cyberpeace.mp.scene.service;

import com.google.common.collect.Lists;
import lombok.*;

import java.util.List;

public interface IFlowMirror {
    void flowMirror(NetworkTrafficMirrorRequest request);

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class NetworkTrafficMirrorRequest {
        /**
         * 虚拟机id列表
         */
        @Builder.Default
        private List<String> vmInstanceIds = Lists.newArrayList();

        /**
         * 容器id列表
         */
        @Builder.Default
        private List<String> containerInstanceIds = Lists.newArrayList();

        /**
         * 流量镜像名称列表,创建镜像返回的Id
         */
        @Builder.Default
        private List<String> arkimeInstanceNames = Lists.newArrayList();

        /**
         * 场景ID
         */
        private String sceneId;

        private Boolean entity;
    }
}
