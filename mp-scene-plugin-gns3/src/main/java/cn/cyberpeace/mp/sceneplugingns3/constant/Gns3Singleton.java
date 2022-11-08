package cn.cyberpeace.mp.sceneplugingns3.constant;

import cn.cyberpeace.mp.sceneplugingns3.config.Gns3AppConfig;
import org.springframework.stereotype.Component;

@Component
public class Gns3Singleton {

    public static Gns3AppConfig gns3AppConfig;

    private Gns3Singleton(Gns3AppConfig gns3AppConfig) {
        Gns3Singleton.gns3AppConfig = gns3AppConfig;
    }

}