package cn.cyberpeace.mp.front.admin.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.front.admin.model.SysActivCtlModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@ModelClass(SysActivCtlModel.class)
public interface SysActivCtlMapper extends BaseModelMapper<SysActivCtlModel> {

    List<SysActivCtlModel> selectAll();

    SysActivCtlModel selectValid();

}
