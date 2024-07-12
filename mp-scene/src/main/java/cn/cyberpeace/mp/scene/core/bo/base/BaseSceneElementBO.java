 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseSceneElementBO.java.
 // File size 4741.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.define.SerializableFunction;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BaseSceneElementBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneNodeBO
 */
@Data
public abstract class BaseSceneElementBO<M extends BaseSceneElementModel, P extends BaseSceneElementPortBO<?, P, T>,
        T extends BaseSceneElementBO<M, P, T>> extends BaseSceneNodeBO<M, T> {
    @ApiModelProperty(value = "网元类型")
    private Integer type;
    @ApiModelProperty(value = "节点文件")
    private String file;
    @ApiModelProperty(value = "系统类型")
    private String systemType;
    @ApiModelProperty(value = "系统架构")
    private String systemArchitecture;
    @ApiModelProperty(value = "系统语言")
    private String systemLanguage;
    @ApiModelProperty(value = "cpu数量")
    private Integer cpu;
    @ApiModelProperty(value = "内存大小")
    private Integer ram;
    @ApiModelProperty(value = "磁盘大小")
    private Integer disk;
    @ApiModelProperty(value = "初始化命令")
    private List<String> initCommands;
    @ApiModelProperty(value = "最大端口数量")
    private Integer maxPortNumber;
    @ApiModelProperty(value = "网元端口")
    @Valid
    private List<P> ports;
    @ApiModelProperty(value = "节点文件命令")
    private String nodeCommand;
    @ApiModelProperty(value = "是否辅助节点")
    private Boolean isAuxiliary;
    @ApiModelProperty(value = "是否是攻防模拟器")
    private Boolean attackMockDeviceFlag;
    @ApiModelProperty(value = "是否支持行为")
    private Boolean behaveSupport;
    @ApiModelProperty(value = "攻防模拟器类型 0-攻击 1-防守")
    private Integer attackMockDeviceType;
    @ApiModelProperty(value = "配置命令")
    private Map<String, Object> configCommand;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseSceneElementModel::getInitCommands),
                BeanUtils.getBeanGetterFieldName(BaseSceneElementModel::getConfigCommand)));
        initCommands = StringUtils.isEmpty(obj.getInitCommands()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getInitCommands(), new TypeReference<List<String>>(){});
        configCommand = StringUtils.isEmpty(obj.getConfigCommand()) ? new HashMap<>()
                : JsonCommon.parse(obj.getConfigCommand(), new TypeReference<Map<String,Object>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        M obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseSceneElementModel::getInitCommands)));
        if (initCommands != null) {
            obj.setInitCommands(JsonCommon.stringify(initCommands));
        }
        if(configCommand != null){
            obj.setConfigCommand(JsonCommon.stringify(configCommand));
        }
        return obj;
    }

    @Override
    public T export() {
        T pObj = super.export();
        if (ports != null) {
            List<P> list = new ArrayList<>();
            for (P port : ports) {
                P export = port.export();
                list.add(export);
            }
            pObj.setPorts(list);
        }

        return pObj;
    }

    @Override
    public <P1> P1 export(Class<P1> pClass, String... ignoreFields) {
        return super.export(pClass, ArrayUtils.addAll(ignoreFields, "ports"));
    }

    public <P2 extends BaseSceneElementPortBO<?, P2, P1>, P1 extends BaseSceneElementBO<?, P2, P1>> P1 export(
            Class<P1> pClass, Class<P2> portClass, SerializableFunction<T>... ignoreFieldGetters) {
        P1 pObj = super.exportIgnore(pClass, ignoreFieldGetters);
        if (ports != null) {
            List<P2> list = new ArrayList<>();
            for (P port : ports) {
                P2 export = port.export(portClass);
                list.add(export);
            }
            pObj.setPorts(list);
        }

        return pObj;
    }
}
