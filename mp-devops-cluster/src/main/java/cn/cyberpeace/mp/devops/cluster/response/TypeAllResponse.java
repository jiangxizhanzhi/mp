package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeIdIntegerConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeIdStringConvertModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeAllResponse {

    /**
     * 一级类型
     */
    private List<TypeConvertModel> firstTypeList;

    /**
     * 二级类型
     */
    private List<TypeConvertModel> secondTypeList;

    /**
     * 三级类型
     */
    private List<TypeConvertModel> thirdTypeList;

    /**
     * 角色
     */
    private List<TypeIdIntegerConvertModel> role;

    /**
     * 分类
     */
    private List<TypeIdIntegerConvertModel> category;

    /**
     * 部署类型
     */
    private List<TypeIdIntegerConvertModel> deployType;

    /**
     * 安装模式
     */
    private List<TypeIdIntegerConvertModel> installMode;

    /**
     * 镜像类型
     */
    private List<TypeIdStringConvertModel> imageType;

    /**
     * 语言
     */
    private List<TypeIdStringConvertModel> language;

    /**
     * 系统架构
     */
    private List<TypeIdStringConvertModel> systemArchitecure;

    /**
     * 磁盘
     */
    private List<TypeIdStringConvertModel> diskFormat;

    /**
     * 访问方式
     */
    private List<TypeIdStringConvertModel> accessType;

    /**
     * 服务类别
     */
    private List<TypeIdStringConvertModel> serviceCategories;

    /**
     * 服务类别
     */
    private List<TypeIdStringConvertModel> serviceStartTypes;

    /**
     * 安装平台
     */
    private List<TypeIdStringConvertModel> installerPlatforms;

    public TypeAllResponse(List<TypeConvertModel> firstTypeList, List<TypeConvertModel> secondTypeList, List<TypeConvertModel> thirdTypeList) {
        this.firstTypeList = firstTypeList;
        this.secondTypeList = secondTypeList;
        this.thirdTypeList = thirdTypeList;
    }
}
