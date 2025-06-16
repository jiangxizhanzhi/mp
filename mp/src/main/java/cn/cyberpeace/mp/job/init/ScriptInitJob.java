package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.ScriptInit;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScriptInitJob {

    @MpInitiate(description = "将代码中的各种脚本拷贝到mp运行环境", id = "code_script_copy", order = 1)
    public void execute() {
        ScriptInit.init();
    }

}
