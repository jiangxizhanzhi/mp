package cn.cyberpeace.mp.base.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.xdapp")
@Data
public class XdAppConfig {

    @Value("${mp.xdapp.xd-one-server:http://192.168.140.4:10060}")
    private String xdOneServer;
    @Value("${mp.xdapp.xd-two-server:https://192.168.140.15}")
    private String xdTwoServer;
    @Value("${mp.xdapp.xd-two-access-key:e2a21753c0904c299512408b8ea97372}")
    private String accessKey;
    @Value("${mp.xdapp.xd-two-secret-key:769ce2d564fa4399b1ade854daccde19}")
    private String secretKey;
    @Value("${mp.xdapp.xd-two-device-id}")
    private String xdTwoDeviceId;
    @Value("${mp.xdapp.xd-two-port-id}")
    private String xdTwoPortId;
    @Value("${mp.xdapp.xd-two-ip-pool-start:192.168.140.76}")
    private String xdTwoIpPoolStart = "192.168.140.76";
    @Value("${mp.xdapp.xd-two-ip-pool-end:192.168.140.225}")
    private String xdTwoIpPoolEnd = "192.168.140.225";
    @Value("${mp.xdapp.xd-two-ip-netmask:255.255.255.0}")
    private String xdTwoIpNetmask = "255.255.255.0";
    @Value("${mp.xdapp.xd-two-ip-gateway:192.168.140.254}")
    private String xdTwoIpGateway = "192.168.140.254";
    // Only initial, manually reviewed free addresses are opened. Never resets existing leases.
    @Value("${mp.xdapp.xd-two-ip-reviewed-free:}")
    private String xdTwoIpReviewedFree = "";
    // The site confirmed the .76-.225 address pool is assigned to vendor two.
    // An explicit empty override disables the initial-pending fallback.
    @Value("${mp.xdapp.xd-two-ip-on-demand-probe-range:192.168.140.76-192.168.140.225}")
    private String xdTwoIpOnDemandProbeRange = "192.168.140.76-192.168.140.225";
    @Value("${mp.xdapp.xd-two-ip-probe-enabled:true}")
    private boolean xdTwoIpProbeEnabled = true;
    @Value("${mp.xdapp.xd-two-ip-probe-timeout-ms:1000}")
    private int xdTwoIpProbeTimeoutMs = 1000;
    @Value("${mp.xdapp.xd-two-create-confirm-timeout-ms:300000}")
    private long xdTwoCreateConfirmTimeoutMs = 300000;
    @Value("${mp.xdapp.xd-two-create-confirm-poll-ms:3000}")
    private long xdTwoCreateConfirmPollMs = 3000;
    @Value("${mp.xdapp.xd-two-recycle-stop-timeout-ms:120000}")
    private long xdTwoRecycleStopTimeoutMs = 120000;
    @Value("${mp.xdapp.xd-two-recycle-delete-timeout-ms:120000}")
    private long xdTwoRecycleDeleteTimeoutMs = 120000;
    @Value("${mp.xdapp.xd-two-recovery-poll-ms:3000}")
    private long xdTwoRecoveryPollMs = 3000;
    @Value("${mp.xdapp.xd-two-allocation-recovery-timeout-ms:300000}")
    private long xdTwoAllocationRecoveryTimeoutMs = 300000;

    @Value("${mp.xdapp.xd-three-server:http://192.168.140.20:5701}")
    private String xdThreeServer;
    @Value("${mp.xdapp.xd-three-network-id:36eca7b1-5fe7-48a5-be8e-19f1a01017d2}")
    private String xdThreeNetworkId = "36eca7b1-5fe7-48a5-be8e-19f1a01017d2";
    @Value("${mp.xdapp.xd-three-clone-timeout-ms:600000}")
    private long xdThreeCloneTimeoutMs = 600000;
    @Value("${mp.xdapp.xd-three-network-timeout-ms:120000}")
    private long xdThreeNetworkTimeoutMs = 120000;
    @Value("${mp.xdapp.xd-three-ready-timeout-ms:600000}")
    private long xdThreeReadyTimeoutMs = 600000;
    @Value("${mp.xdapp.xd-three-delete-timeout-ms:300000}")
    private long xdThreeDeleteTimeoutMs = 300000;
    @Value("${mp.xdapp.xd-three-poll-ms:3000}")
    private long xdThreePollMs = 3000;



}
