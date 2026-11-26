package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/** The cluster resource list is the available authoritative identity source. */
final class VendorThirdInventory {
    final Map<Integer, JSONObject> vms;

    private VendorThirdInventory(Map<Integer, JSONObject> vms) {
        this.vms = Collections.unmodifiableMap(vms);
    }

    static VendorThirdInventory parse(HttpResult response) {
        if (response == null || response.getException() != null || !response.isSuccess()) {
            throw new MpRuntimeException("厂商三资源清单查询失败");
        }
        JSONObject root = JSONObject.parseObject(response.getResult());
        JSONArray data = root == null ? null : root.getJSONArray("data");
        if (root == null || !Integer.valueOf(1).equals(root.getInteger("success")) || data == null) {
            throw new MpRuntimeException("厂商三资源清单业务状态异常");
        }
        Integer total = root.getInteger("total");
        if ((total != null && total != data.size()) ||
                (root.getString("next") != null && !root.getString("next").isEmpty())) {
            throw new MpRuntimeException("厂商三资源清单不完整");
        }
        Map<Integer, JSONObject> vms = new LinkedHashMap<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject vm = data.getJSONObject(i);
            if (vm == null) throw new MpRuntimeException("厂商三资源清单记录异常");
            Integer id = vm.getInteger("vmid");
            if (id == null) {
                String type = vm.getString("type");
                if ("qemu".equals(type) || "lxc".equals(type) || "vm".equals(type)) {
                    throw new MpRuntimeException("厂商三资源清单缺少VMID");
                }
                continue; // cluster/storage/host resources do not have VMIDs
            }
            if (id <= 0 || vms.putIfAbsent(id, vm) != null) {
                throw new MpRuntimeException("厂商三资源清单含无效或重复VMID");
            }
        }
        return new VendorThirdInventory(vms);
    }

    JSONObject find(int vmid) { return vms.get(vmid); }

    boolean hasName(String name) {
        return vms.values().stream().anyMatch(vm -> name.equals(vm.getString("name")));
    }

    boolean owns(VendorThirdVmLedger.Lease lease, JSONObject vm) {
        return isQemu(vm) && vm.getIntValue("template") != 1
                && lease.getRequestName().equals(vm.getString("name"))
                && vms.values().stream().filter(row -> lease.getRequestName().equals(row.getString("name"))).count() == 1;
    }

    static boolean isQemu(JSONObject vm) {
        if (vm == null) return false;
        String type = vm.getString("type");
        // Older vendor responses omit type; explicitly different resource types
        // must never be treated as QEMU resources.
        return type == null || "qemu".equals(type) || "vm".equals(type);
    }
}
