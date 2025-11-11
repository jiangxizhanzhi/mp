package cn.cyberpeace.mp.devopscluster.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.service.InterfaceService;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devopscluster.model.InterfaceModel;
import cn.cyberpeace.mp.devopscluster.repository.InterfaceMapper;
import cn.cyberpeace.mp.devopscluster.service.fields.InterfaceFields;
import cn.cyberpeace.mp.devopscluster.vo.InterfaceVO;
import cn.cyberpeace.mp.devopscluster.request.InterfacePageRequest;
import cn.cyberpeace.mp.frontadmin.util.SQLUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author guquan
 * @Date：2025/9/9 11:13
 */
@Service
@Slf4j
public class InterfaceServiceImpl extends BaseModelServiceImpl<InterfaceModel, InterfaceMapper, InterfaceFields, QueryParams> implements InterfaceService {

    @Autowired
    private InterfaceMapper interfaceMapper;

    @Autowired
    public InterfaceServiceImpl(InterfaceMapper modelMapper, InterfaceFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public InterfaceVO interfaceModel2InterfaceVO(InterfaceModel interfaceModel) {
        if (Objects.isNull(interfaceModel)) {
            return null;
        }

        InterfaceVO interfaceVO = new InterfaceVO();
        BeanUtils.copyProperties(interfaceModel, interfaceVO);
        return interfaceVO;
    }

    @Override
    public InterfaceModel interfaceVO2InterfaceModel(InterfaceVO interfaceVO) {
        if (Objects.isNull(interfaceVO)) {
            return null;
        }

        InterfaceModel interfaceModel = new InterfaceModel();
        BeanUtils.copyProperties(interfaceVO, interfaceModel);
        return interfaceModel;
    }

    @Override
    public ServiceResult page(InterfacePageRequest req) {
        String likeDesc = SQLUtils.escapeLike(req.getDesc());
        HumanCondition condition = new HumanCondition();
        if (StringUtils.isNotBlank(likeDesc)) {
            condition.filterContains(InterfaceModel::getDesc, likeDesc);
        }
        int total = interfaceMapper.total(condition);
        condition.pagination(req.getPage(), req.getPerPage());
        List<InterfaceModel> models = interfaceMapper.list(condition);
        List<InterfaceVO> interfaceVOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(models)) {
            for (InterfaceModel temp : models) {
                InterfaceVO interfaceVO = interfaceModel2InterfaceVO(temp);
                interfaceVOs.add(interfaceVO);
            }
        }
        ListObjServiceResult<InterfaceModel> result = new ListObjServiceResult(new ListObjResult(interfaceVOs, total).asResult()).asResult();
        return result;
    }

    @Override
    public InterfaceVO add(InterfaceVO interfaceVO) {
        InterfaceModel interfaceModel = interfaceVO2InterfaceModel(interfaceVO);
        interfaceMapper.create(interfaceModel);
        return interfaceModel2InterfaceVO(interfaceModel);
    }

    @Override
    public InterfaceVO modify(InterfaceVO interfaceVO) {
        InterfaceModel interfaceModel = interfaceVO2InterfaceModel(interfaceVO);
        interfaceMapper.update(interfaceModel);
        return interfaceModel2InterfaceVO(interfaceModel);
    }

    @Override
    public Boolean deleteInterface(String id) {
        Boolean rtn = interfaceMapper.delete(id);
        return rtn;
    }

}
