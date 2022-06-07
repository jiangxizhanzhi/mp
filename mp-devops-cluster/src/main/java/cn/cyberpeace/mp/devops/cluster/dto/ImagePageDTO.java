package cn.cyberpeace.mp.devops.cluster.dto;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.request.ImagePageRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.TargetSceneRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagePageDTO extends BasePageDTO{

    /**
     * 角色
     */
    private Integer role;

    /**
     * 系统类型
     */
    private Integer mainCategory;

    /**
     * 子分类
     */
    private Integer subCategory;

    /**
     * 三级分类
     */
    private Integer thirdCategory;

    /**
     * 搜索类型，0：全部; 1：'vm','container','simple'; 2: entity
     */
    private Integer searchType;

    /**
     * 镜像类型
     */
    private String imageType;

    /**
     * 镜像名称
     */
    private String name;

    /**
     * 用户ID
     */
    private String owner;

    /**
     * 是否公开
     */
    private PublicEnum isPublic;

    /**
     * 是否支持快速创建
     */
    private Boolean quickCreateSupport;

    /**
     * 在位状态
     */
    private Integer inPosition;

    /**
     * 使用状态
     */
    private Integer useStatus;

    /**
     * 1:查询私有和公开的数据
     */
    private Integer searchPublicEnum;

    /**
     * 系统类型
     */
    private Integer systemType;

    /**
     * 系统架构
     */
    private String systemArchitecure;

    /**
     * 系统语言
     */
    private String language;

    /**
     * 镜像状态
     */
    private Integer imageStatus;

    /**
     * 是否支持基础镜像
     */
    private Boolean baseImageSupport;

    /**
     * 靶标分类，1：内置靶标，2：实体靶标，3：自定义靶标
     */
    private Integer targetCategory;

    /**
     * 是否模糊匹配镜像名称
     */
    private Boolean likeName = Boolean.TRUE;

    private Boolean isHide = Boolean.FALSE;

    public Boolean getLikeName() {
        return Objects.isNull(likeName) ? Boolean.TRUE : likeName;
    }


    /**
     * 转换ImagePageRequest
     * @param request
     */
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
    }

    /**
     * 转换RealEquipmentPageRequest对象
     * @param request
     */
    public ImagePageDTO(RealEquipmentPageRequest request){
        this.setMainCategory(request.getCategory());
        this.setSubCategory(request.getSecondCategory());
        this.setThirdCategory(request.getThirdCategory());
        this.setImageType(ImageTypeEnum.ENTITY.toValue());
        this.setUseStatus(request.getUseStatus());
        this.setName(request.getName());
        this.setInPosition(request.getInPosition());
        this.setOffset((request.getPage() - 1) * request.getPerPage());
        this.setLimit(request.getPerPage());
        this.setIsHide(Boolean.FALSE);
    }

    public ImagePageDTO(TargetSceneRequest request){
        this.setTargetCategory(request.getTargetCategory());
        this.setMainCategory(request.getCategory());
        this.setSubCategory(request.getSecondCategory());
        this.setThirdCategory(request.getThirdCategory());
        this.setImageType(Objects.nonNull(request.getImageType()) ? request.getImageType().toValue() : "");
        this.setName(request.getName());
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
    }
}
