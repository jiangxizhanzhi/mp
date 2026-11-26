package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.*;

/** A positive observation is useful even when this account's VM list is incomplete. */
final class VendorSecondInventory {
    final Map<String, JSONObject> vms;
    final boolean complete;

    private VendorSecondInventory(Map<String, JSONObject> vms, boolean complete) {
        this.vms = vms;
        this.complete = complete;
    }

    static VendorSecondInventory unavailable() {
        return new VendorSecondInventory(Collections.emptyMap(), false);
    }

    static VendorSecondInventory parse(HttpResult response) {
        if (response == null || !response.isSuccess() || response.getException() != null) {
            throw new MpRuntimeException("厂商二虚拟机列表查询失败");
        }
        JSONObject root = JSONObject.parseObject(response.getResult());
        if (root == null || !Integer.valueOf(0).equals(root.getInteger("code"))) {
            throw new MpRuntimeException("厂商二虚拟机列表业务状态异常");
        }
        JSONObject data = root.getJSONObject("data");
        JSONArray entries = data == null ? null : data.getJSONArray("data");
        if (entries == null) throw new MpRuntimeException("厂商二虚拟机列表数据缺失");
        Map<String, JSONObject> values = new LinkedHashMap<>();
        boolean valid = true;
        for (int i = 0; i < entries.size(); i++) {
            JSONObject vm = entries.getJSONObject(i);
            String id = vm == null ? null : vm.getString("id");
            if (id == null || id.trim().isEmpty() || values.putIfAbsent(id, vm) != null) valid = false;
        }
        Integer total = data.getInteger("total_size");
        Integer page = data.getInteger("page_num");
        boolean complete = valid && total != null && total == entries.size() && page != null && page == 0;
        return new VendorSecondInventory(values, complete);
    }

    String byName(String name) {
        if (name == null || name.isEmpty()) return null;
        String found = null;
        for (Map.Entry<String, JSONObject> entry : vms.entrySet()) {
            if (!name.equals(entry.getValue().getString("name"))) continue;
            if (found != null) return null;
            found = entry.getKey();
        }
        return found;
    }

    boolean containsName(String name) {
        return name != null && vms.values().stream().anyMatch(vm -> name.equals(vm.getString("name")));
    }

    String[] primaryAddress(String uuid) {
        JSONObject vm = vms.get(uuid);
        JSONArray networks = vm == null ? null : vm.getJSONArray("networks");
        JSONObject first = networks == null || networks.isEmpty() ? null : networks.getJSONObject(0);
        return first == null ? null : new String[]{first.getString("ip_address"), first.getString("mac_address")};
    }

    Map<String, Set<String>> ownersByAddress() {
        Map<String, Set<String>> owners = new LinkedHashMap<>();
        vms.forEach((id, vm) -> {
            JSONArray networks = vm.getJSONArray("networks");
            if (networks == null) return;
            for (int i = 0; i < networks.size(); i++) {
                JSONObject network = networks.getJSONObject(i);
                String ip = network == null ? null : network.getString("ip_address");
                if (ip != null && !ip.isEmpty()) owners.computeIfAbsent(ip, k -> new LinkedHashSet<>()).add(id);
            }
        });
        return owners;
    }
}
