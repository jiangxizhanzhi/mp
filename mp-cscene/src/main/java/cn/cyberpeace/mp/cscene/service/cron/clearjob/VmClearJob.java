package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineGetData;
import cn.cyberpeace.mp.sceneplugincloud.util.ForceReleaseResourceUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VmClearJob extends AbstractClearJob<VirtualMachineGetData> {

    private VirtualMachine vmClient = new VirtualMachine();

    public VmClearJob(SceneElementMapper sceneElementMapper) {
        super(sceneElementMapper);
    }

    @Override
    protected Map<String, VirtualMachineGetData> getEid2Node(List<VirtualMachineGetData> vms) {
        Map<String, VirtualMachineGetData> result = new HashMap<>();
        vms.forEach(vm -> {
            if (vm.getName() != null && vm.getId() != null) {
                String eid = super.getEid(vm.getName());
                if (StringUtils.isNotEmpty(eid)) {
                    result.put(eid, vm);
                }
            }
        });
        return result;
    }

    @Override
    protected List<VirtualMachineGetData> doQuery(int offset) {
        return vmClient.getAllInstance(LIMIT, offset);
    }

    @Override
    protected void doClear(VirtualMachineGetData data) {
        vmClient.deleteInstanceById(data.getId());
    }

    @Override
    protected void forceClear(VirtualMachineGetData data) {
        ForceReleaseResourceUtils.forceDeleteVm(Arrays.asList(data.getId()));
    }
}
