package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.MajorModel;
import cn.cyberpeace.mp.auth.repository.MajorMapper;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.request.major.MajorCreateRequest;
import cn.cyberpeace.mp.auth.request.major.MajorPageRequest;
import cn.cyberpeace.mp.auth.request.major.MajorUpdateRequest;
import cn.cyberpeace.mp.auth.response.major.MajorDto;
import cn.cyberpeace.mp.auth.service.MajorService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.MajorFields;
import cn.cyberpeace.mp.auth.service.fields.OrgFields;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 专业服务实现类
 */
@Service
@Slf4j
public class MajorServiceImpl extends BaseModelServiceImpl<MajorModel, MajorMapper, MajorFields, QueryParams> implements MajorService {

    @Autowired
    private UserService userService;
    public MajorServiceImpl(MajorMapper modelMapper, MajorFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public MajorModel createMajor(MajorCreateRequest majorCreateRequest) {
        majorCreateRequest.paramCheck();
        MajorModel majorModel = majorCreateRequest.convertToModel();
        majorModel.checkMajorNameNotDuplicate(this);

        ObjServiceResult<MajorModel> objServiceResult = super.create(majorModel);
        ResultUtils.throwExceptionIfError(objServiceResult, AuthErrorCode.MAJOR_CREATE_FAIL);
        return objServiceResult.getSource().getObj();

    }

    @Override
    public MajorMapper getMapper() {
        return getModelMapper();
    }

    @Override
    public ListObjServiceResult<MajorDto> pageMajors(MajorPageRequest majorPageRequest) {
        HumanCondition humanCondition = majorPageRequest.convertToHumanCondition();
        List<MajorModel> majorModelList = getMapper().list(humanCondition);
        int total = getMapper().total(humanCondition);

        List<MajorDto> majorDtoList;
        if (CollectionUtils.isEmpty(majorModelList)) {
            majorDtoList = Collections.emptyList();
        } else {
            majorDtoList = majorModelList.stream().map(MajorDto::convertModelToDto).collect(Collectors.toList());
        }

        return new ListObjServiceResult<>(new ListObjResult<>(majorDtoList, total).asResult()).asResult();
    }

    @Override
    public List<MajorDto> listAllMajors() {
        List<MajorModel> allMajorModels = this.getMapper().list
                (new HumanCondition().select(MajorModel::getMajorName, MajorModel::getId));
        if (CollectionUtils.isEmpty(allMajorModels)) {
            return Collections.emptyList();
        } else {
            return allMajorModels.stream().map(MajorDto::convertModelToDto).collect(Collectors.toList());
        }

    }

    @Override
    public List<MajorDto> listTargetMajors(Set<String> majorNameSet) {
        List<MajorModel> targetMajorModels = this.getMapper().list(new HumanCondition().select(MajorModel::getMajorName, MajorModel::getId).filterIn(MajorModel::getMajorName, majorNameSet.toArray(new String[0])));
        if (CollectionUtils.isEmpty(targetMajorModels)) {
            return Collections.emptyList();
        } else {
            return targetMajorModels.stream().map(MajorDto::convertModelToDto).collect(Collectors.toList());
        }
    }

    @Override
    public Boolean deleteMajors(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new MpRuntimeException(AuthErrorCode.PARAMS_SHOULD_NOT_EMPTY);
        } else {
            List<UserBo> userBoList = userService.listByMajorIds(ids);
            if (CollectionUtils.isEmpty(userBoList)) {
                getMapper().bulkDelete(ids.toArray(new String[0]));
            } else {
                throw new MpRuntimeException(AuthErrorCode.MAJOR_DELETE_USING_NAME_ERROR);
            }
        }
        return true;
    }

    @Override
    public MajorModel updateMajor(MajorUpdateRequest majorUpdateRequest) {
        majorUpdateRequest.paramCheck();
        MajorModel majorModel = majorUpdateRequest.convertToModel();
        majorModel.checkMajorNameNotDuplicate(this);
        ObjServiceResult<MajorModel> updateResult = super.update(majorModel);
        ResultUtils.throwExceptionIfError(updateResult, AuthErrorCode.MAJOR_UPDATE_FAIL);
        return updateResult.getSource().getObj();
    }

    @Override
    public void checkMajorId(String majorId) {
        getMajorById(majorId);
    }

    @Override
    public MajorModel getMajorById(String majorId) {
        ParamCheck.notBlank(majorId, AuthBasei18nCode.MAJOR_ID);
        MajorModel majorModel = this.getMapper().get(majorId);
        if (Objects.isNull(majorModel)) {
            throw new MpRuntimeException(AuthErrorCode.MAJOR_NOT_EXISTED.format(majorId));
        }
        return majorModel;
    }


}
