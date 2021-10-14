package cn.cyberpeace.mp.devops.cluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devops.cluster.model.ScreenshotModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 截屏文件管理mapper
 * @Author qiaokang
 * @Date 2021-09-13 11:05:18
 */
@Mapper
@Repository
@ModelClass(ScreenshotModel.class)
public interface ScreenshotMapper extends BaseModelMapper<ScreenshotModel> {

    /**
     * 查询已用空间
     * @return
     */
    Long queryUsedSpace();

    /**
     * 根据创建时间查询
     * @param createTime
     * @return
     */
    List<ScreenshotModel> queryByCreateTime(String createTime);

    /**
     * 根据id批量查询
     * @param ids
     * @return
     */
    List<ScreenshotModel> selectByIds(@Param("ids") List<String> ids);
	
}
