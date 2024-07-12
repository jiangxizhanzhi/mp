 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InitFlowScriptDemoInitJob.java.
 // File size 4937.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.flow.model.FlowScriptDemoModel;
import cn.cyberpeace.mp.flow.service.impl.FlowScriptDemoServiceImpl;
import cn.cyberpeace.mp.flow.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName InitFlowScriptDemoInitJob
 * @Description 脚本模板初始化
 * @Author qiaokang
 * @Date 2021/9/1 14:36
 * @Version 1.0.0
 */
@Component
@Slf4j
public class InitFlowScriptDemoInitJob {

    @Autowired
    private FlowScriptDemoServiceImpl flowScriptDemoService;

    private static final String PY_DEMO_NAME = "demo_py.py";
    private static final String SH_DEMO_NAME = "demo_sh.sh";

    private static final String PROBE_SH_DEMO_NAME = "get_host_name.sh";
    private static final String PROBE_PY_DEMO_NAME = "get_host_name.py";
    private static final String PROBE_BAT_DEMO_NAME = "get_host_name.bat";

    @MpInitiate(description = "流量脚本模板初始化", id = "init_flow_script_demo")
    public void run() {
        InputStream pyDemo = null;
        InputStream shDemo = null;
        InputStream probeShDemo = null;
        InputStream probePyDemo = null;
        InputStream probeBatDemo = null;

        try {
            List<FlowScriptDemoModel> demoList = flowScriptDemoService.listAll(null);
            if (CollectionUtils.isEmpty(demoList)) {
                pyDemo = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("scriptdemo/flow/demo_py.py"));
                shDemo = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("scriptdemo/flow/demo_sh.sh"));

                probeShDemo = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("scriptdemo/probe/get_host_name.sh"));
                probePyDemo = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("scriptdemo/probe/get_host_name.py"));
                probeBatDemo = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("scriptdemo/probe/get_host_name.bat"));

                String pySuffix = FileUtil.getSuffix(PY_DEMO_NAME);
                String shSuffix = FileUtil.getSuffix(SH_DEMO_NAME);
                String pyPath = FsCommon.upload(pyDemo, pySuffix);
                String shPath = FsCommon.upload(shDemo, shSuffix);

                String probeShSuffix = FileUtil.getSuffix(PROBE_SH_DEMO_NAME);
                String probePySuffix = FileUtil.getSuffix(PROBE_PY_DEMO_NAME);
                String probeBatSuffix = FileUtil.getSuffix(PROBE_BAT_DEMO_NAME);
                String probeShPath = FsCommon.upload(probeShDemo, probeShSuffix);
                String probePyPath = FsCommon.upload(probePyDemo, probePySuffix);
                String probeBatPath = FsCommon.upload(probeBatDemo, probeBatSuffix);

                FlowScriptDemoModel pyDemoModel = new FlowScriptDemoModel(PY_DEMO_NAME, pyPath, FlowScriptDemoModel.TYPE_FLOW);
                FlowScriptDemoModel shDemoModel = new FlowScriptDemoModel(SH_DEMO_NAME, shPath, FlowScriptDemoModel.TYPE_FLOW);
                FlowScriptDemoModel probeShModel = new FlowScriptDemoModel(PROBE_SH_DEMO_NAME, probeShPath, FlowScriptDemoModel.TYPE_PROBE);
                FlowScriptDemoModel probePyModel = new FlowScriptDemoModel(PROBE_PY_DEMO_NAME, probePyPath, FlowScriptDemoModel.TYPE_PROBE);
                FlowScriptDemoModel probeBatModel = new FlowScriptDemoModel(PROBE_BAT_DEMO_NAME, probeBatPath, FlowScriptDemoModel.TYPE_PROBE);

                List<FlowScriptDemoModel> addDemoList = new ArrayList<>();
                addDemoList.add(pyDemoModel);
                addDemoList.add(shDemoModel);
                addDemoList.add(probeShModel);
                addDemoList.add(probePyModel);
                addDemoList.add(probeBatModel);
                flowScriptDemoService.bulkCreate(addDemoList);
            } else {
                log.info("============ no need to init script demo ==============");
            }
        } catch (Exception e) {
            log.error("init flow script demo error, {}", ExceptionUtils.getStackTrace(e));
        } finally {
            IOUtils.closeQuietly(pyDemo);
            IOUtils.closeQuietly(shDemo);
            IOUtils.closeQuietly(probeShDemo);
            IOUtils.closeQuietly(probePyDemo);
            IOUtils.closeQuietly(probeBatDemo);
        }
    }
}
