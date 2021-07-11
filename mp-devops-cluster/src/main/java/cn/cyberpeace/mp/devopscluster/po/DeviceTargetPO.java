package cn.cyberpeace.mp.devopscluster.po;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import cn.cyberpeace.mp.devopscluster.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_devops_target")
public class DeviceTargetPO extends BaseModel {

    
    private String uuid;

    
    private String name;

    
    private String logo;

    
    private Integer role;

    
    private Integer mainCategory;

    
    private Integer subCategory;

    
    private Integer thirdCategory;

    
    private String imageType;

    
    private Boolean isImport;

    
    private PublicEnum isPublic;

    
    private Boolean quickCreateSupport;

    
    private StatusEnum inPosition;

    
    private StatusEnum useStatus;

    
    private Integer referenceStatus;

    
    private Integer parentSystemType;

    
    private Integer systemType;

    
    private String systemArchitecure;

    
    private String language;

    
    private Integer imageStatus;

    
    private Integer imageSource;

    
    private Boolean baseImageSupport;

    
    private Boolean initSupport;

    
    private Boolean isHide = Boolean.FALSE;

    
    private Boolean isInit = Boolean.FALSE;

    
    @DefaultInt
    private Integer instanceState;

    
    private String instanceCreateUser;

    
    @DefaultBool
    private Boolean supportExport ;

    
    @DefaultStr
    private String createUser ;

    
    @DefaultStr
    private String createUserName;
}
