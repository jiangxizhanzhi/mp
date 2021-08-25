package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.converter.model.TypeConvertModel;
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
}
