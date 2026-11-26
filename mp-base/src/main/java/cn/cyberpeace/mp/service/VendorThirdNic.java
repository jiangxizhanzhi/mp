package cn.cyberpeace.mp.service;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/** Parses provider-returned vnet0 configuration; never derives an address from a template. */
@Getter
@AllArgsConstructor
final class VendorThirdNic {
    private final String network, ip, mac, netmask, gateway;
    private final boolean virtio;

    static VendorThirdNic parse(JSONObject config) {
        String value = config == null ? null : config.getString("vnet0");
        Map<String, String> fields = new HashMap<>();
        if (value != null) for (String token : value.split(",")) {
            String[] pair = token.trim().split("=", 2);
            if (pair.length == 2) fields.put(pair[0].trim(), pair[1].trim());
        }
        String mac = fields.get("virtio");
        if (mac == null || !mac.matches("(?i)([0-9a-f]{2}:){5}[0-9a-f]{2}")) mac = null;
        String ip = fields.get("ip");
        String netmask = null;
        if (ip != null) {
            String[] cidr = ip.split("/", -1);
            try {
                long n = VendorSecondIpRange.number(cidr[0]);
                if (n == 0 || n == 0xffffffffL || cidr.length > 2) throw new IllegalArgumentException();
                ip = cidr[0];
                if (cidr.length == 2) {
                    int prefix = Integer.parseInt(cidr[1]);
                    if (prefix < 0 || prefix > 32) throw new IllegalArgumentException();
                    long mask = prefix == 0 ? 0 : (0xffffffffL << (32 - prefix)) & 0xffffffffL;
                    netmask = (mask >>> 24) + "." + ((mask >>> 16) & 255) + "." + ((mask >>> 8) & 255) + "." + (mask & 255);
                }
            } catch (IllegalArgumentException e) { ip = null; netmask = null; }
        }
        String gateway = fields.get("gateway");
        if (gateway == null) gateway = fields.get("gw");
        try { if (gateway != null) VendorSecondIpRange.number(gateway); }
        catch (IllegalArgumentException e) { gateway = null; }
        return new VendorThirdNic(fields.get("network"), ip, mac, netmask, gateway,
                value != null && (value.startsWith("virtio=") || value.startsWith("virtio,")));
    }

    boolean hasAddress() { return ip != null && mac != null; }
}
