package cn.cyberpeace.mp.devopscluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeAllConvertModel {

    
    private List<TypeConvertModel> firstTypeList = new ArrayList<>();

    
    private List<TypeConvertModel> secondTypeList = new ArrayList<>();

    
    private List<TypeConvertModel> thirdTypeList = new ArrayList<>();


    
    private List<TypeIdIntegerConvertModel> idIntegerModelList = new ArrayList<>();

    
    private List<TypeIdStringConvertModel> idStringModelList = new ArrayList<>();

    
    private List<String> items = new ArrayList<>();



    public TypeAllConvertModel(List<TypeConvertModel> firstTypeList, List<TypeConvertModel> secondTypeList, List<TypeConvertModel> thirdTypeList) {
        this.firstTypeList = firstTypeList;
        this.secondTypeList = secondTypeList;
        this.thirdTypeList = thirdTypeList;
    }
}
