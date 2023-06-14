package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

@Data
public class VirtualDynamicActionData implements CloudHttpData {

    public final static String RESTART = "restart";
    public final static String START = "resume";
    public final static String STOP = "suspend";
    public final static String RESUME = "resume";
    public final static String SUSPEND = "suspend";
    public final static String PAUSE = "pause";
    public final static String UNPAUSE = "unpause";

    private String action;
}
