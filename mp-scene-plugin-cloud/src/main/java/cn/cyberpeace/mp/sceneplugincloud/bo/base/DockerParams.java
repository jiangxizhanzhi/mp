package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.PythonExecUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DockerParams {
    private Boolean run;
    private Boolean interactive;
    private Boolean privileged;
    private String environmentStr;
    private Boolean tty;
    private String hostConfigExStr;
}
