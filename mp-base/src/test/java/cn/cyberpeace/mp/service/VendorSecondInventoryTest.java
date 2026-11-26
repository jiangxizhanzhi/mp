package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.util.http.HttpResult;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.cyberpeace.mp.service.VendorSecondAddressPoolTest.*;
import static org.junit.jupiter.api.Assertions.*;

class VendorSecondInventoryTest {
    private static final String A = "198.51.100.10", B = "198.51.100.11";

    static HttpResult page(int number, int total, String next, JSONObject... vms) {
        JSONObject body = JSONObject.parseObject(listResponse(total, vms).getResult());
        JSONObject data = body.getJSONObject("data");
        data.put("page_num", number);
        data.put("next_page_num", next);
        return response(200, body.toJSONString());
    }

    @Test void followsPagesAndFindsVmBeyondFirstPage() {
        List<Integer> requests = new ArrayList<>();
        VendorSecondInventory inventory = VendorSecondInventory.readAll(number -> {
            requests.add(number);
            return number == 0 ? page(0, 2, "1", vm("first", "fixture", A))
                    : page(1, 2, "", vm("second", "fixture", B));
        });
        assertEquals(Arrays.asList(0, 1), requests);
        assertTrue(inventory.complete);
        assertEquals(B, inventory.primaryAddress("second")[0]);
    }

    @Test void repeatedPageOrChangedTotalCannotProveAbsence() {
        VendorSecondInventory repeated = VendorSecondInventory.readAll(number ->
                page(0, 2, "1", vm("first", "fixture", A)));
        assertFalse(repeated.complete);
        VendorSecondInventory changed = VendorSecondInventory.readAll(number -> number == 0
                ? page(0, 2, "1", vm("first", "fixture", A))
                : page(1, 3, "", vm("second", "fixture", B)));
        assertFalse(changed.complete);
    }

    @Test void contradictoryOrTransientPowerStateIsUnknown() {
        JSONObject record = vm("vm", "fixture", A);
        record.put("status", "creating");
        record.put("power_state", "off");
        record.put("is_stopped", 1);
        assertEquals(VmPowerState.UNKNOWN, VendorSecondInventory.parse(listResponse(1, record)).powerState("vm"));
        record.put("status", "running");
        assertEquals(VmPowerState.UNKNOWN, VendorSecondInventory.parse(listResponse(1, record)).powerState("vm"));
        record.put("status", "stopped");
        assertEquals(VmPowerState.STOPPED, VendorSecondInventory.parse(listResponse(1, record)).powerState("vm"));
    }

    @Test void prefersTheCreatedNet0InsteadOfAnotherInterface() {
        JSONObject record = vm("vm", "fixture", B);
        JSONObject first = record.getJSONArray("networks").getJSONObject(0);
        first.put("vif_id", "net1");
        JSONObject second = new JSONObject();
        second.put("vif_id", "net0");
        second.put("ip_address", A);
        second.put("mac_address", "02:00:00:00:00:02");
        record.getJSONArray("networks").add(second);
        assertEquals(A, VendorSecondInventory.parse(listResponse(1, record)).primaryAddress("vm")[0]);
    }
}
