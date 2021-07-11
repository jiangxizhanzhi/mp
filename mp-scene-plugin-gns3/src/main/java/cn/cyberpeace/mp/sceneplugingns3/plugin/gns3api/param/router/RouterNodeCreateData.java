package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.router;

import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.base.BaseNodeCreateData;


public class RouterNodeCreateData extends BaseNodeCreateData {

    private static String COMPUTE_ID = "local";
    private static String NODE_TYPE = "dynamips";
    private static String SYMBOL = ":/symbols/router.svg";

    public RouterNodeCreateData(String name) {
        super(COMPUTE_ID, NODE_TYPE, SYMBOL);
        this.setName(name);
    }

}
