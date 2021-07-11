package cn.cyberpeace.mp.devopscluster.converter.bo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devopscluster.converter.model.BasePageConvertModel;
import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import cn.cyberpeace.mp.devopscluster.enums.StatusEnum;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RealEquipmentBO extends BasePageConvertModel {

    
    private String uuid;

    
    private String name;

    
    private String logo;

    
    private Integer role;

    
    private Integer mainCategory;

    
    private Integer subCategory;

    
    private Integer thirdCategory;

    
    private String imageType;

    
    private String description;

    
    private Boolean isImport;

    
    private Boolean isInternal;

    
    private ExtSystemInfo extSystemInfo;

    
    private ExtEntity extEntity;


    
    private ExtImageInfo extImageInfo;

    
    private List<ExtEntityAccess> extEntityAccess;

    
    private ExtVm extVm;

    
    private ExtGateway extGateway;

    
    private ExtDocker extDocker;

    
    private PublicEnum isPublic;

    
    private Integer authTotal;

    
    private StatusEnum inPosition;

    
    private StatusEnum useStatus;

    
    private Integer referenceStatus;

    
    private EntityUsb extEntityUsb;
}
