package cn.cyberpeace.mp.devops.cluster.repository;

import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.model.ImageModel;
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
    List<ImageModel> page(ImagePageDTO dto);

    /**
     * 新增
     * @param imageModel
     * @return
     */
    Integer insert(ImageModel imageModel);

    /**
     * 根据ID更新信息
     * @param imageModel
     * @return
     */
    Integer updateById(ImageModel imageModel);

    /**
     * 删除
     * @param dto
     * @return
     */
    Integer delete(ImageDeleteDTO dto);
}
