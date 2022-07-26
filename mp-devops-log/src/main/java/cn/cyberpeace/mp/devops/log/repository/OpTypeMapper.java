package cn.cyberpeace.mp.devops.log.repository;

import cn.cyberpeace.mp.devops.log.model.OpModuleModel;
import cn.cyberpeace.mp.devops.log.model.OpTypeModel;
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
public interface OpTypeMapper {
    /** 查询所有操作类型
     * @return
     */
    List<OpTypeModel> selectAll();
}
