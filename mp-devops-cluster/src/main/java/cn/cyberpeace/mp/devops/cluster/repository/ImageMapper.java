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
import java.util.Map;

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
     * 查询
     * @param uuid
     * @return
     */
    TargetModel findByUuid(String uuid);

    /**
     * 根据UUID批量查询
     * @param uuids
     * @return
     */
    List<TargetModel> findByUuids(@Param("uuids") List<String> uuids);

    /**
     * 根据ID批量查询
     * @param ids
     * @return
     */
    List<TargetModel> findByIds(@Param("ids") List<Integer> ids);

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
    Integer batchInsert(@Param("list") List<TargetModel> list);

    /**
     * 靶标类型统计
     * @return
     */
    Map<String,Object> countTarget();

    /**
     * 虚拟靶标统计
     * @return
     */
    Map<String,Object> countVmTarget();

    /**
     * 实体靶标统计
     * @return
     */
    Map<String,Object> countEntityTarget();
}
