package cn.cyberpeace.mp.flow.integration.cloud.lincloud;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractCreateVirtualMachineParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.VirtualNic;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class LinyunCreateVirtualMachineParams extends AbstractCreateVirtualMachineParams {
    private String name;
    private String templateId;
    private String image;
    private Integer cpu;
    private Integer ram;
    private Integer disk;
    private Boolean mainCloud;
    private String osType;
    private List<VirtualNic> networks;

    public static LinyunCreateVirtualMachineParams getDefault() {
        FlowConfig flowConfig = BeanCommon.getBean(FlowConfig.class);
        int trexCpu = flowConfig.getTrexCpu();
        int trexRam = flowConfig.getTrexRam();
        int trexDisk = flowConfig.getTrexDisk();
        String networkId = flowConfig.getNetworkId();
        String trexImage = flowConfig.getTrexImage();
        String cidr = flowConfig.getCidr();
        String name = "Trex-" + TextUtils.uuid();

        LinyunCreateVirtualMachineParams params = new LinyunCreateVirtualMachineParams();
        params.setOsType("linux");
        params.setMainCloud(true);
        params.setTemplateId(flowConfig.getTrexTemplateId());
        params.setCpu(trexCpu);
        params.setDisk(trexDisk);
        params.setRam(trexRam);
        params.setImage(trexImage);
        params.setName(name);
        params.setNetworks(new ArrayList<>());

        VirtualNic virtualNic = new VirtualNic();

        virtualNic.setNetworkId(networkId);
        virtualNic.setCidr(cidr);
        virtualNic.setFixedIp(generateRandomIP(cidr));

        virtualNic.setAccessModes(new ArrayList<>());
        VirtualNic.AccessMode accessMode = new VirtualNic.AccessMode();
        accessMode.setProtocol("ssh");
        accessMode.setUsername("root");
        accessMode.setPassword("111111");
        accessMode.setPort(22);
        virtualNic.getAccessModes().add(accessMode);
        params.getNetworks().add(virtualNic);

        return params;
    }

    public static String generateRandomIP(String cidr){
        String[] parts = cidr.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("CIDR格式错误！");
        }

        // 解析CIDR中的网络地址和子网掩码
        String network = parts[0]; // 网络部分
        int prefixLength = Integer.parseInt(parts[1]); // 子网掩码长度

        // 将网络地址转换为整数形式
        InetAddress networkAddress = null;
        try {
            networkAddress = InetAddress.getByName(network);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        byte[] networkBytes = networkAddress.getAddress();
        int networkInt = 0;
        for (int i = 0; i < networkBytes.length; i++) {
            networkInt |= (networkBytes[i] & 0xFF) << (8 * (3 - i));
        }

        // 计算子网掩码
        int subnetMask = ~((1 << (32 - prefixLength)) - 1);

        // 网络的开始地址
        int networkStart = networkInt & subnetMask;
        // 网络的结束地址（广播地址）
        int networkEnd = networkStart | ~subnetMask;

        // 从网络的开始地址和结束地址生成随机地址
        Random random = new Random();
        int randomIPInt = networkStart + random.nextInt(networkEnd - networkStart + 1);

        // 将随机生成的IP整数转换回字节形式
        byte[] randomIPBytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            randomIPBytes[i] = (byte) ((randomIPInt >> (8 * (3 - i))) & 0xFF);
        }

        // 转换为字符串形式的IP地址
        InetAddress randomIP = null;
        try {
            randomIP = InetAddress.getByAddress(randomIPBytes);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return randomIP.getHostAddress();
    }
}
