package cn.cyberpeace.mp.devops.cluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeAllConvertModel {

    /**
     * 一级类型
     */
    private List<TypeConvertModel> firstTypeList = new ArrayList<>();

    /**
     * 二级类型
     */
    private List<TypeConvertModel> secondTypeList = new ArrayList<>();

    /**
     * 三级类型
     */
    private List<TypeConvertModel> thirdTypeList = new ArrayList<>();


    /**
     * 角色列表
     */
    private List<TypeIdIntegerConvertModel> idIntegerModelList = new ArrayList<>();

    /**
     * 平台&镜像类型
     */
    private List<TypeIdStringConvertModel> idStringModelList = new ArrayList<>();

    public TypeAllConvertModel(List<TypeConvertModel> firstTypeList, List<TypeConvertModel> secondTypeList, List<TypeConvertModel> thirdTypeList) {
        this.firstTypeList = firstTypeList;
        this.secondTypeList = secondTypeList;
        this.thirdTypeList = thirdTypeList;
    }
}
