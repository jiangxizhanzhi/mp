package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineGetData;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/24
 */
public class VmClearJob extends AbstractClearJob<VirtualMachineGetData> {

    private final VirtualMachine vmClient = new VirtualMachine();

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
}
