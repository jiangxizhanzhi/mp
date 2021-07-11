package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_devops_cpnode_monitor_script_config")
public class CpNodeMonitorScriptConfigModel extends BaseModel {

    
    public static final Integer STATUS_INSTALLED = 1;

    
    public static final Integer STATUS_TO_INSTALL = 2;

    
    public static final Integer STATUS_FAILED = 3;

    
    private String agentName;

    
    private String hostname;

    
    @DefaultStr
    private String ip;

    
    private Integer status;

    
    private String config;

    private static final long serialVersionUID = 1L;


    @Data
    public static class ParamConfig {

        private String id;

        private String scriptUrl;

        private String interval;
    }

}
