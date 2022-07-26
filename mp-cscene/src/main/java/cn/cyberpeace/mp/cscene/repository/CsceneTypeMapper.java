package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.cscene.model.CsceneTypeModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Mapper
@Repository
public interface CsceneTypeMapper {
    /** 查询所有靶场类型
     * @return
     */
    List<CsceneTypeModel> selectAll();
}
