package cn.cyberpeace.mp.devops.cluster.service;

import java.util.List;

public interface TargetLogoService {

    /**
     * 初始化默认logo
     * @return
     */
    int init();

    /**
     * 获取默认logo数据
     * @return
     */
    List<String> list();
}
