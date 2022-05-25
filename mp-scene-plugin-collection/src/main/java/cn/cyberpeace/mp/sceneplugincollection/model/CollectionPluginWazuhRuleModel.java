package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sceneplugincollection_wazuh_rule")
public class CollectionPluginWazuhRuleModel extends BaseModel {
    private String file;
    private String relativeDirname;
    private Integer level;
    private String status;
    private String details;
    private String pciDss;
    private String gpg13;
    private String gdpr;
    private String hipaa;
    private String nist;
    private String tsc;
    private String mitre;
    private String groups;
    private String description;
}
