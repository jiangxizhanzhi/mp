package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceUserCountModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Description 用户场景统计mapper
 * @Author qiaokang
 * @Date 2023-06-20 14:26:35
 */
@Mapper
@Repository
@ModelClass(CsceneInstanceUserCountModel.class)
public interface CsceneInstanceUserCountMapper extends BaseModelMapper<CsceneInstanceUserCountModel> {

    /**
     * 用户启动场景数量加1
     * @param userId
     * @return
     */
    int increaseCount(@Param("userId") String userId);

    /**
     * 用户启动场景数量减1
     * @param userId
     * @return
     */
    int decreaseCount(@Param("userId") String userId);

}
