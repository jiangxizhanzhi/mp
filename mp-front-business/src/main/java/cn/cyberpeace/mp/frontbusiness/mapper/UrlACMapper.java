package cn.cyberpeace.mp.frontbusiness.mapper;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.frontbusiness.dto.UrlAccessCtlDto;
import cn.cyberpeace.mp.frontbusiness.model.UrlACModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
@ModelClass(UrlACModel.class)
public interface UrlACMapper extends BaseModelMapper<UrlACModel> {

    UrlAccessCtlDto getById(@Param("id") String id);

    List<UrlAccessCtlDto> listAllUrlAccessCtlDtos();

    List<UrlAccessCtlDto> listAllUrlAccessCtlPatternDtos();

    List<UrlAccessCtlDto> listByTargetIds(@Param("targetIds") List<String> targetIds);

}
