package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.MajorModel;
import cn.cyberpeace.mp.auth.repository.MajorMapper;
import cn.cyberpeace.mp.auth.request.major.MajorCreateRequest;
import cn.cyberpeace.mp.auth.request.major.MajorPageRequest;
import cn.cyberpeace.mp.auth.request.major.MajorUpdateRequest;
import cn.cyberpeace.mp.auth.response.major.MajorDto;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;

import java.util.List;
import java.util.Set;

/**
 * 专业服务类
 */
public interface MajorService extends BaseModelService<MajorModel, QueryParams> {

    /**
     *  创建专业
     * @param majorCreateRequest 专业信息创建请求
     * @return
     */
    MajorModel createMajor(MajorCreateRequest majorCreateRequest);

    MajorMapper getMapper();

    ListObjServiceResult<MajorDto> pageMajors(MajorPageRequest majorPageRequest);

    List<MajorDto> listAllMajors();

    /**
     * 根据专业名称查询出专业信息
     * @param majorNameSet
     * @return
     */
    List<MajorDto> listTargetMajors(Set<String> majorNameSet);

    Boolean deleteMajors(List<String> ids);

    MajorModel updateMajor(MajorUpdateRequest majorUpdateRequest);

    void checkMajorId(String majorId);

    MajorModel getMajorById(String majorId);


}
