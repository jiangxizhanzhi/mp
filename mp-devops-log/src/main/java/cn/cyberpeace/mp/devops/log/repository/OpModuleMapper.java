package cn.cyberpeace.mp.devops.log.repository;

import cn.cyberpeace.mp.devops.log.model.OpModuleModel;
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
public interface OpModuleMapper {
    /** 查询所有操作模块分类
     * @return
     */
    List<OpModuleModel> selectAll();
}
