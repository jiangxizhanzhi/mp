package cn.cyberpeace.mp.situationaware.task;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.situationaware.TestSituationAwareApplciation;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.enums.ProbeStatus;
import cn.cyberpeace.mp.situationaware.repository.ProbeModelMapper;

import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes = TestSituationAwareApplciation.class)
@RunWith(SpringRunner.class)
public class ProbeManagerTaskTest {

    @Autowired
    private ProbeManagerTask probeManagerTask;

    @Autowired
    private ProbeModelMapper probeModelMapper;


    @Test
    public void testQueryProbeExist() {
        String probeId = UUID.randomUUID().toString();
        ProbeModel probeModel = new ProbeModel();
        probeModel.setId(probeId);
        probeModel.setProbeName("system-monitor");
        probeModel.setCommandInterval("10080m");
        Boolean result =  probeManagerTask.queryProbeExist(probeModel);
        Assert.assertEquals(result, Boolean.TRUE);
    }

    @Test
    public void testModifyProbeStatus() {
        probeModelMapper.updateProbeStatus("383bcc02858946ebaf8e5b9177f157b6", ProbeStatus.TERMINATE);
    }

    @Test
    public void testBeanMerge() throws Exception {
        ProbeModel probeModel = probeModelMapper.queryByProbeId("d4707889737f40c2ab15e990bf691639");
        ProbeModel toUpdate = new ProbeModel();
        toUpdate.setCommandInterval("3m");
        toUpdate.setProbeStatus(ProbeStatus.RUNNING);
        BeanUtils.copyPropertiesIgnoreNull(toUpdate, probeModel);
        Assert.assertEquals(probeModel.getProbeStatus(), ProbeStatus.RUNNING);
    }
}
