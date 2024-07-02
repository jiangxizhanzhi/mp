package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineGetData;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName VmClearJob
 * @Description
 * @Author maojun
 * @Date 2022/3/24
 * @Version 1.0.0
 * @see AbstractClearJob
 */
public class VmClearJob extends AbstractClearJob<VirtualMachineGetData> {

    private VirtualMachine vmClient = new VirtualMachine();

    /**
     * VmClearJob
     *
     * @param sceneElementMapper
     */
    public VmClearJob(SceneElementMapper sceneElementMapper) {
        super(sceneElementMapper);
    }

    /**
     * getEid2Node
     *
     * @param vms
     * @return
     */
    @Override
    protected Map<String, VirtualMachineGetData> getEid2Node(List<VirtualMachineGetData> vms) {
        Map<String, VirtualMachineGetData> result = new HashMap<>();
        for (VirtualMachineGetData vm : vms) {
            if (vm.getName() != null && vm.getId() != null) {
                String eid = super.getEid(vm.getName());
                if (StringUtils.isNotEmpty(eid)) {
                    result.put(eid, vm);
                }
            }
        }
        return result;
    }

    /**
     * doQuery
     *
     * @param offset
     * @return
     */
    @Override
    protected List<VirtualMachineGetData> doQuery(int offset) {
        return vmClient.getAllInstance(LIMIT, offset);
    }

    /**
     * doClear
     *
     * @param data
     */
    @Override
    protected void doClear(VirtualMachineGetData data) {
        vmClient.deleteInstanceById(data.getId());
    }

    /**
     * forceClear
     *
     * @param data
     */
    @Override
    protected void forceClear(VirtualMachineGetData data) {
        vmClient.deleteInstanceById(data.getId());

    }
}
