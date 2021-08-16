package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_devops_images")
public class ImagesModel extends BaseModel {

    /**
     * 名称
     */
    private String name;

    /**
     * 角色
     */
    private Integer role;

    /**
     * 分类
     */
    private Integer mainCategory;

    /**
     * 子分类
     */
    private Integer subCategory;

    /**
     * 镜像类型
     */
    private Integer type;

}
