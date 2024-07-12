 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionPluginWazuhRuleModel.java.
 // File size 959.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CollectionPluginWazuhRuleModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
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
