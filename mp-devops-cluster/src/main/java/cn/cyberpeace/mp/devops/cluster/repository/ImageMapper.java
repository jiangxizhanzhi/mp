package cn.cyberpeace.mp.devops.cluster.repository;

import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.model.ImagesModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 镜像管理
 */
@Mapper
@Repository
public interface ImageMapper {

    /**
     * 分页查询
     * @param dto
     * @return
     */
    List<ImagesModel> page(ImagePageDTO dto);

    /**
     * 新增
     * @param imagesModel
     * @return
     */
    ImagesModel insert(ImagesModel imagesModel);

    /**
     * 根据ID更新信息
     * @param imagesModel
     * @return
     */
    Integer updateById(ImagesModel imagesModel);

    /**
     * 删除
     * @param dto
     * @return
     */
    Integer delete(ImageDeleteDTO dto);
}
