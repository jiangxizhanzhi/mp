package cn.cyberpeace.mp.sceneplugincloud.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincloud.model.CsceneElementSoftVersionModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(CsceneElementSoftVersionModel.class)
/**
 * 场景元素软件版本管理接口。
 * 该接口继承自BaseModelMapper，用于对场景元素的软件版本进行CRUD操作。
 * 它定义了对数据库中场景元素软件版本模型的映射行为，是数据访问层的一部分。
 *
 * @param <T> 泛型参数，指定为CsceneElementSoftVersionModel类型，表示操作的对象是场景元素的软件版本模型。
 */
public interface CsceneElementSoftVersionMapper extends BaseModelMapper<CsceneElementSoftVersionModel> {

}

