package cn.cyberpeace.mp.devopscluster.dto;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.devopscluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import cn.cyberpeace.mp.devopscluster.request.ImagePageRequest;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devopscluster.request.TargetSceneRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagePageDTO extends BasePageDTO{

    
    private Integer role;

    
    private Integer mainCategory;

    
    private Integer subCategory;

    
    private List<Integer> subCategories;

    
    private Integer thirdCategory;

    
    private Integer searchType;

    
    private String imageType;

    
    private String name;

    
    private List<String> nameList;

    
    private String owner;

    
    private PublicEnum isPublic;

    
    private Boolean quickCreateSupport;

    
    private Integer inPosition;

    
    private Integer useStatus;

    
    private Integer searchPublicEnum;

    
    private Integer systemType;

    
    private String systemArchitecure;

    
    private String language;

    
    private Integer imageStatus;

    
    private Boolean baseImageSupport;

    
    private Integer targetCategory;

    
    private Boolean likeName = Boolean.TRUE;

    private Boolean isHide = Boolean.FALSE;

    
    private Boolean vmStarted;

    
    private List<String> imageTypeList;

    
    private List<Integer> preTypeList;

    
    private Integer parentSystemType;

    
    private String createUserName ;

    
    private String orders;

    
    private String ordersType;

    public Boolean getLikeName() {
        return Objects.isNull(likeName) ? Boolean.TRUE : likeName;
    }


    
    public ImagePageDTO(ImagePageRequest request){
        this.setRole(request.getRole());
        this.setMainCategory(request.getMainCategory());
        this.setSubCategory(request.getSubCategory());
        this.setThirdCategory(request.getThirdCategory());
        this.setQuickCreateSupport(request.getQuickCreateSupport());
        if (Objects.isNull(request.getImageType())){
            this.setSearchType(1);
        }else {
            this.setImageType(request.getImageType());
        }
        this.setName(request.getName());
        this.setOwner(Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getId() : null);
        this.setSearchPublicEnum(1);
        this.setOffset((request.getPage() - 1) * request.getPerPage());
        this.setLimit(request.getPerPage());
        this.setSystemType(request.getSystemType());
        this.setSystemArchitecure(request.getSystemArchitecure());
        this.setLanguage(request.getLanguage());
        this.setImageStatus(request.getImageStatus());
        this.setBaseImageSupport(request.getBaseImageSupport());
        this.setLikeName(request.getLikeName());
        this.setIsHide(Boolean.FALSE);
        this.setVmStarted(request.getVmStarted());
        this.setCreateUserName(request.getCreateUserName());
        if (StringUtils.isNotEmpty(request.getOrders()) && request.getOrders().startsWith("-")) {
            this.setOrders(request.getOrders().substring(1));
            this.setOrdersType("desc");
        } else {
            this.setOrders(request.getOrders());
            this.setOrdersType("asc");
        }
    }

    
    public ImagePageDTO(RealEquipmentPageRequest request){
        this.setMainCategory(request.getCategory());
        this.setSubCategory(request.getSecondCategory());
        this.setThirdCategory(request.getThirdCategory());
        this.setImageType(ImageTypeEnum.ENTITY.toValue());
        this.setUseStatus(request.getUseStatus());
        if (StringUtils.isNotEmpty(request.getExactName())) {
            this.setName(request.getExactName());
            this.setLikeName(false);
        } else {
            this.setName(request.getName());
        }
        this.setInPosition(request.getInPosition());
        this.setOffset((request.getPage() - 1) * request.getPerPage());
        this.setLimit(request.getPerPage());
        this.setIsHide(Boolean.FALSE);
    }

    public ImagePageDTO(TargetSceneRequest request){
        this.setTargetCategory(request.getTargetCategory());
        this.setMainCategory(request.getCategory());
        this.setSubCategory(request.getSecondCategory());
        this.setSubCategories(request.getSecondCategories());
        this.setThirdCategory(request.getThirdCategory());
        this.setImageType(Objects.nonNull(request.getImageType()) ? request.getImageType().toValue() : "");
        this.setName(request.getName());
        this.setNameList(request.getNameList());
        this.setSearchPublicEnum(1);
        this.setRole(request.getRole());
        this.setIsHide(request.getIsHide());
        this.setSystemType(request.getSystemType());
        this.setSystemArchitecure(request.getSystemArchitecure());
        this.setQuickCreateSupport(request.getQuickCreateSupport());
        if (Objects.nonNull(request.getPage()) && Objects.nonNull(request.getPerPage())) {
            this.setOffset((request.getPage() - 1) * request.getPerPage());
            this.setLimit(request.getPerPage());
        }
        if (Objects.isNull(request.getIgnoreUser()) || !request.getIgnoreUser()) {
            this.setOwner(Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getId() : null);
        }
        if (Objects.isNull(request.getImageType())){
            this.setSearchType(0);
        }
        if (request.getLikeName() != null) {
            this.setLikeName(request.getLikeName());
        }
        this.setLanguage(request.getLanguage());
        this.setImageStatus(request.getImageStatus());
        this.setBaseImageSupport(request.getBaseImageSupport());
        this.setImageTypeList(request.getImageTypeList());
        this.setPreTypeList(request.getPreTypeList());
        this.setParentSystemType(request.getParentSystemType());
    }
}
