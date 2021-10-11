package cn.cyberpeace.mp.devops.cluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.model.TargetModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 镜像管理
 */
@Mapper
@Repository
@ModelClass(TargetModel.class)
public interface ImageMapper extends BaseModelMapper<TargetModel> {

    /**
     * 分页查询
     * @param dto
     * @return
     */
    List<TargetModel> page(ImagePageDTO dto);

    /**
     * 获取总数
     * @param dto
     * @return
     */
    Integer totalCount(ImagePageDTO dto);

    /**
     * 新增
     * @param targetModel
     * @return
     */
    Integer insert(TargetModel targetModel);

    /**
     * 新增
     * @param uuid
     * @return
     */
    TargetModel findByUuid(String uuid);

    /**
     * 根据ID更新信息
     * @param targetModel
     * @return
     */
    Integer updateById(TargetModel targetModel);

    /**
     * 删除
     * @param dto
     * @return
     */
    Integer delete(ImageDeleteDTO dto);

    /**
     * 批量更新
     * @param list
     * @return
     */
    Integer updateBatch(@Param("list") List<TargetModel> list);

    /**
     * 新增
     * @param list
     * @return
     */
    Integer batchInsert(List<TargetModel> list);
}
