package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.flow.model.FlowScriptDemoModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description:  脚本模板初始化
 * @Author qiaokang
 * @Date：2021/9/1 14:36
 */
@Component
@Slf4j
public class InitFlowScriptDemoService implements CommandLineRunner {

    @Autowired
    private FlowScriptDemoServiceImpl flowScriptDemoService;

    @Autowired
    private FastDFSClient fastDFSClient;

    private static final String PY_DEMO_NAME = "demo_py.py";

    private static final String SH_DEMO_NAME = "demo_sh.sh";

    @Override
    public void run(String... args) {
        InputStream pyDemo = null;
        InputStream shDemo = null;

        try {
            List<FlowScriptDemoModel> demoList = flowScriptDemoService.listAll();
            if (CollectionUtils.isEmpty(demoList)) {
                log.info("============ init script demo start ==============");
                pyDemo = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("scriptdemo/demo_py.py"));
                shDemo = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("scriptdemo/demo_sh.sh"));

                String pyPath = fastDFSClient.upload(pyDemo, PY_DEMO_NAME, null, FdfsGroupEnum.DEFAULT.toString());
                String shPath = fastDFSClient.upload(shDemo, SH_DEMO_NAME, null, FdfsGroupEnum.DEFAULT.toString());

                FlowScriptDemoModel pyDemoModel = new FlowScriptDemoModel(PY_DEMO_NAME, pyPath);
                FlowScriptDemoModel shDemoModel = new FlowScriptDemoModel(SH_DEMO_NAME, shPath);

                List<FlowScriptDemoModel> addDemoList = new ArrayList<>();
                addDemoList.add(pyDemoModel);
                addDemoList.add(shDemoModel);
                flowScriptDemoService.bulkCreate(addDemoList);

                log.info("============ init script demo end ==============");
            } else {
                log.info("============ no need to init script demo ==============");
            }
        } catch (Exception e) {
            log.error("init flow script demo error", e);
        } finally {
            IOUtils.closeQuietly(pyDemo);
            IOUtils.closeQuietly(shDemo);
        }
    }
}
