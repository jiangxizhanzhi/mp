package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.cloud;

import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.base.BaseNodeCreateData;


public class CloudNodeCreateData extends BaseNodeCreateData {

    private static String COMPUTE_ID = "local";
    private static String NODE_TYPE = "cloud";
    private static String SYMBOL = ":/symbols/cloud.svg";

    public CloudNodeCreateData() {
        super(COMPUTE_ID, NODE_TYPE, SYMBOL);
    }

}
