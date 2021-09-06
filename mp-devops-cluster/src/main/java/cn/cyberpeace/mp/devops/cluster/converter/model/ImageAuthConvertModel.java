package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ImageAuthConvertModel extends BasePageConvertModel{

    private List<String> userIds;

    /**
     * 是否授权所有，0：自定义，1：授权所有
     */
    private PublicEnum publicEnum;
}
