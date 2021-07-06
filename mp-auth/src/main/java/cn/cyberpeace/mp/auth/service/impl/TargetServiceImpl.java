package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.TargetType;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.repository.TargetMapper;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.fields.TargetFields;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class TargetServiceImpl extends BaseModelServiceImpl<TargetModel, TargetMapper, TargetFields, QueryParams>
        implements TargetService {

    @Autowired
    public TargetServiceImpl(TargetMapper modelMapper, TargetFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public TargetModel getByIdentifier(String identifier) {
        Assert.notBlank(identifier);
        return getModelMapper().first(new HumanCondition().filterEqual("identifier", identifier));
    }

    @Override
    public List<TargetModel> listByTenantId(String tenantId) {
        List<TargetModel> targets = getModelMapper().listByTenantId(tenantId);
        return targets;
    }

    @Override
    public boolean exists(String id) {
        Assert.notBlank(id, "id不能为空！");
        TargetModel targetModel = getModelMapper().get(id);
        return Objects.nonNull(targetModel);
    }

    @Override
    public ObjServiceResult<TargetModel> create(TargetModel obj, String[] fields) {
        check(obj);
        return super.create(obj, fields);
    }



    private void check(TargetModel target) {
        boolean supportedType = false;
        if (target.getType() != null) {
            for (TargetType ty : TargetType.values()) {
                if(target.getType() == ty.getType()) {
                    supportedType = true;
                    break;
                }
            }
        }

        Assert.isTrue(supportedType, AuthConstant.ERROR_CODE_PREFIX + "target_type_not_support");

//        TargetModel targetModel = getByIdentifier(target.getIdentifier());
//        if (targetModel != null) {
//            throw new MpRuntimeException("Identifier为" + target.getIdentifier() + "的记录已经存在!");
//        }
    }
}
