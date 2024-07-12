 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TypeAllConvertModel.java.
 // File size 1430.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TypeAllConvertModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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

    /**
     * hw_disk_bus hw_vif_model hw_video_model
     */
    private List<String> items = new ArrayList<>();



    public TypeAllConvertModel(List<TypeConvertModel> firstTypeList, List<TypeConvertModel> secondTypeList, List<TypeConvertModel> thirdTypeList) {
        this.firstTypeList = firstTypeList;
        this.secondTypeList = secondTypeList;
        this.thirdTypeList = thirdTypeList;
    }
}
