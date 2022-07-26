package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.situationaware.model.OsTypeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeTypeModel;
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
public interface ProbeTypeMapper {

    /**
     * 查询所有探针类型
     * @return
     */
    List<ProbeTypeModel> selectAll();
}
