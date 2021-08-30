package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeIdIntegerConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TypeIdStringConvertModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeAllResponse {

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
     * role/category/image_status/deploy_type/install_mode
     */
    private List<TypeIdIntegerConvertModel> idIntegerModelList = new ArrayList<>();

    /**
     * image_type/language/system_architecure/disk_format/access_type/categories
     */
    private List<TypeIdStringConvertModel> idStringModelList = new ArrayList<>();

    public TypeAllResponse(List<TypeConvertModel> firstTypeList, List<TypeConvertModel> secondTypeList, List<TypeConvertModel> thirdTypeList) {
        this.firstTypeList = firstTypeList;
        this.secondTypeList = secondTypeList;
        this.thirdTypeList = thirdTypeList;
    }
}
