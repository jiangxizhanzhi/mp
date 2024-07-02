package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Data;

import java.util.List;

/**
 * @ClassName CreateMirrorData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see HttpData
 */
@Data
public class CreateMirrorData implements HttpData {
    private String tunnelType = "vxlan";
    /**
     * 1-65535
     */
    private Integer optionKey;
    private List<Param> srcParam;
    private Param destParam;

    private String name;

    @Data
    public static class Param {
        private String id;
        private String nicId;
        private String desId;
        private String type;
    }

    public static class Type {
        public static final String VM = "VM";
        public static final String DOCKER = "DOCKER";
        public static final String NETWORK = "NETWORK";
    }
}