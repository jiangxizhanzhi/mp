package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

/** A positive observation is useful even when this account's VM list is incomplete. */
final class VendorSecondInventory {
    final Map<String, JSONObject> vms;
    final boolean complete;
    private final Integer page, total, nextPage;
    private final boolean valid;

    private VendorSecondInventory(Map<String, JSONObject> vms, boolean complete) {
        this(vms, complete, null, null, null, false);
    }

    private VendorSecondInventory(Map<String, JSONObject> vms, boolean complete, Integer page,
                                  Integer total, Integer nextPage, boolean valid) {
        this.vms = vms;
        this.complete = complete;
        this.page = page;
        this.total = total;
        this.nextPage = nextPage;
        this.valid = valid;
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
        Integer nextPage = null;
        String next = data.getString("next_page_num");
        if (next != null && !next.trim().isEmpty()) {
            try {
                nextPage = Integer.valueOf(next);
                if (page == null || nextPage <= page) valid = false;
            } catch (NumberFormatException e) {
                valid = false;
            }
        }
        valid &= total != null && total >= entries.size() && page != null && page >= 0;
        boolean complete = valid && total == entries.size() && page == 0 && nextPage == null;
        return new VendorSecondInventory(values, complete, page, total, nextPage, valid);
    }

    /** Follow the returned page number; an inconsistent/failed page never proves absence or uniqueness. */
    static VendorSecondInventory readAll(IntFunction<HttpResult> fetchPage) {
        VendorSecondInventory first = parse(fetchPage.apply(0));
        if (first.complete || !first.valid || !Integer.valueOf(0).equals(first.page)) return first;
        Map<String, JSONObject> all = new LinkedHashMap<>(first.vms);
        VendorSecondInventory current = first;
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(60);
        for (int count = 1; count < 100 && current.nextPage != null && System.nanoTime() < deadline; count++) {
            int requested = current.nextPage;
            VendorSecondInventory next;
            try {
                next = parse(fetchPage.apply(requested));
            } catch (RuntimeException e) {
                break;
            }
            if (!next.valid || !Integer.valueOf(requested).equals(next.page) || !Objects.equals(first.total, next.total)) break;
            if (!Collections.disjoint(all.keySet(), next.vms.keySet())) break;
            all.putAll(next.vms);
            if (all.size() > first.total) break;
            if (next.nextPage == null && all.size() == first.total) {
                return new VendorSecondInventory(all, true);
            }
            current = next;
        }
        return new VendorSecondInventory(all, false);
    }

    VmPowerState powerState(String uuid) {
        JSONObject vm = vms.get(uuid);
        if (vm == null) return VmPowerState.UNKNOWN;
        String status = vm.getString("status");
        String power = vm.getString("power_state");
        Integer stopped = vm.getInteger("is_stopped");
        if ("running".equalsIgnoreCase(status) && (power == null || "on".equalsIgnoreCase(power))
                && (stopped == null || stopped == 0)) return VmPowerState.RUNNING;
        if ("stopped".equalsIgnoreCase(status) && (power == null || "off".equalsIgnoreCase(power))
                && (stopped == null || stopped == 1)) return VmPowerState.STOPPED;
        // The observed schema also exposes explicit power/stopped fields. Never
        // interpret an arbitrary non-running status (e.g. creating) as stopped.
        if ((status == null || status.isEmpty()) && "off".equalsIgnoreCase(power)
                && Integer.valueOf(1).equals(stopped)) return VmPowerState.STOPPED;
        return VmPowerState.UNKNOWN;
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
        if (networks == null || networks.isEmpty()) return null;
        JSONObject first = null;
        for (int i = 0; i < networks.size(); i++) {
            JSONObject network = networks.getJSONObject(i);
            if (network != null && "net0".equals(network.getString("vif_id"))) {
                if (first != null) return null;
                first = network;
            }
        }
        if (first == null && networks.size() == 1) first = networks.getJSONObject(0);
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
