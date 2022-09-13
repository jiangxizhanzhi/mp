package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.switchs;

import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.base.BaseNodeCreateData;

/**
 * @Description
 * @Author maojun
 * @Date 2022/9/7
 */
public class SwitchNodeCreateData extends BaseNodeCreateData {

    private static String COMPUTE_ID = "local";
    private static String NODE_TYPE = "ethernet_switch";
    private static String SYMBOL = ":/symbols/ethernet_switch.svg";

    public SwitchNodeCreateData() {
        super(COMPUTE_ID, NODE_TYPE, SYMBOL);
    }

}
