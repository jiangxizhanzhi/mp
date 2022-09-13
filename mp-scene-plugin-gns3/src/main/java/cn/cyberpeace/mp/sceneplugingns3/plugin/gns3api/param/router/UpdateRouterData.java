package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.router;

import cn.cyberpeace.mp.sceneplugingns3.util.gns3http.Gns3HttpData;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Setter
@Getter
public class UpdateRouterData implements Gns3HttpData {

    private List<Port> ports;

    private List<StaticRoute> staticRoutes;

    @Override
    public String toContent() {
        StringBuilder portsStr = new StringBuilder();
        for (int i = 0; i < ports.size(); i++) {
            var port = ports.get(i);
            String s = getConfigTemplate().replace("{tag}", port.tag.toString());
            s = s.replace("{ip}", port.ip);
            s = s.replace("{index}", String.valueOf(i + 1));
            portsStr.append(s);
        }

        StringBuilder routesStr = new StringBuilder();
        if (CollectionUtils.isNotEmpty(staticRoutes)) {
            for (var route : staticRoutes) {
                String s = getStaticRouteTemplate().replace("{ip}", route.getIp());
                s = s.replace("{mask}", route.getMask());
                s = s.replace("{nexthop}", route.getNexthop());
                routesStr.append(s);
            }
        }

        return getContent(portsStr.toString(), routesStr.toString());
    }

    @Override
    public boolean textPlain() {
        return true;
    }

    @Setter
    @Getter
    public static class Port {
        private Integer tag;
        private String ip;
    }

    @Setter
    @Getter
    public static class StaticRoute {
        private String ip;
        private String mask;
        private String nexthop;
    }

    private String getConfigTemplate() {
        return "!\n" +
                "interface FastEthernet0/0.{index}\n" +
                " encapsulation dot1Q {tag}\n" +
                " ip address {ip} 255.255.255.0\n" +
                "!\n";
    }

    private String getStaticRouteTemplate() {
        return "ip route {ip} {mask} {nexthop}\n";
    }

    private String getContent(String ports, String staticRoutes) {
        return "version 12.3\n" +
                "service timestamps debug datetime msec\n" +
                "service timestamps log datetime msec\n" +
                "no service password-encryption\n" +
                "!\n" +
                "hostname R1\n" +
                "!\n" +
                "boot-start-marker\n" +
                "boot-end-marker\n" +
                "!\n" +
                "!\n" +
                "no aaa new-model\n" +
                "ip subnet-zero\n" +
                "no ip icmp rate-limit unreachable\n" +
                "!\n" +
                "!\n" +
                "ip cef\n" +
                "no ip domain lookup\n" +
                "!\n" +
                "ip tcp synwait-time 5\n" +
                "!\n" +
                "!\n" +
                "interface FastEthernet0/0\n" +
                " no ip address\n" +
                " duplex auto\n" +
                " speed auto\n" +
                "!\n" +
                ports +
                "\n" +
                "!\n" +
                "no ip http server\n" +
                "no ip http secure-server\n" +
                "ip classless\n" +
                staticRoutes +
                "!\n" +
                "!\n" +
                "no cdp log mismatch duplex\n" +
                "!\n" +
                "line con 0\n" +
                " exec-timeout 0 0\n" +
                " privilege level 15\n" +
                " logging synchronous\n" +
                "line aux 0\n" +
                " exec-timeout 0 0\n" +
                " privilege level 15\n" +
                " logging synchronous\n" +
                "line vty 0 4\n" +
                " login\n" +
                "!\n" +
                "end";
    }
}
