package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.scene.core.bo.ThirdPartyElementBO;
import cn.cyberpeace.mp.scene.core.bo.ThirdPartyElementConnectBO;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class ThirdPartyElementReader implements ReadListener<ThirdPartyElementBO> {

    private final List<ThirdPartyElementBO> cachedElements = new ArrayList<>();

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        ReadListener.super.onException(exception, context);
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        ReadListener.super.invokeHead(headMap, context);
    }

    @Override
    public void invoke(ThirdPartyElementBO data, AnalysisContext context) {
        cachedElements.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        validateElementData();
    }

    private void validateElementData() {
        if (CollectionUtils.isEmpty(cachedElements)) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("导入拓扑文件中，节点数据为空"));
        } else {
            long nodeTypeisInvalidCount = cachedElements.stream().filter(this::nodeTypeIsNotValid).count();
            if (nodeTypeisInvalidCount > 0) {
                throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("导入拓扑文件中，存在节点类型不合法"));
            }

            if (!nodeNameIsDistinct()) {
                throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("导入拓扑文件中，节点名称不唯一"));
            }
        }
    }


    private boolean nodeTypeIsNotValid(ThirdPartyElementBO thirdPartyElementBO) {
        if (ThirdPartyElementBO.NodeType.END_DEVICE.equals(thirdPartyElementBO.getNodeType())
                || ThirdPartyElementBO.NodeType.ROUTER.equals(thirdPartyElementBO.getNodeType())
                || ThirdPartyElementBO.NodeType.SWITCH.equals(thirdPartyElementBO.getNodeType())
            || ThirdPartyElementBO.NodeType.FIREWALL.equals(thirdPartyElementBO.getNodeType())
            || ThirdPartyElementBO.NodeType.OTHER.equals(thirdPartyElementBO.getNodeType())) {
            return false;
        } else {
            return true;
        }
    }

    private boolean nodeNameIsDistinct() {
        long distinctNodeNameNums = cachedElements.stream().map(ThirdPartyElementBO::getNodeName).distinct().count();
        if (distinctNodeNameNums != cachedElements.size()) {
            return false;
        } else {
            return true;
        }
    }
}
