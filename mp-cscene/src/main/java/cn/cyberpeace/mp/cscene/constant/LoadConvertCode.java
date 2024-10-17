package cn.cyberpeace.mp.cscene.constant;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoadConvertCode {

    // 运行平台code转换map
    private static Map<String, String> runTableConvertMap = new HashMap<>();
    // 控制类型code转换map
    private static Map<String, String> controlTypeConvertMap = new HashMap<>();
    // 作用类型code转换map
    private static Map<String, String> actionTypeConvertMap = new HashMap<>();

    public static final String RUN_TABLE_OTHER = "1005020204";
    public static final String CONTROL_TYPE_OTHER = "1005020303";
    public static final String ACTION_TYPE_OTHER = "1005020104";

    @PostConstruct
    private void init() {
        this.runTableConvertMap.put("docker", "1005020201");
        this.runTableConvertMap.put("VMware", "1005020202");
        this.runTableConvertMap.put("VM", "1005020203");

        this.controlTypeConvertMap.put("shell", "1005020301");
        this.controlTypeConvertMap.put("web", "1005020302");

        this.actionTypeConvertMap.put("1", "1005020101");
        this.actionTypeConvertMap.put("2", "1005020102");
        this.actionTypeConvertMap.put("3", "1005020103");
        this.actionTypeConvertMap.put("5", "1005020105");
        this.actionTypeConvertMap.put("6", "1005020106");
        this.actionTypeConvertMap.put("7", "1005020107");
        this.actionTypeConvertMap.put("8", "1005020108");
    }

    public static Map<String, String> getRunTableConvertMap() {
        return runTableConvertMap;
    }

    public static Map<String, String> getControlTypeConvertMap() {
        return controlTypeConvertMap;
    }

    public static Map<String, String> getActionTypeConvertMap() {
        return actionTypeConvertMap;
    }
}
