package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.scene.core.bo.ThirdPartyElementConnectBO;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Getter
public class ThirdPartyElementConnectReader implements ReadListener<ThirdPartyElementConnectBO> {

    private final List<ThirdPartyElementConnectBO> cachedConnects = new ArrayList<>();

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)$"
    );

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        ReadListener.super.onException(exception, context);
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        ReadListener.super.invokeHead(headMap, context);
    }

    @Override
    public void invoke(ThirdPartyElementConnectBO data, AnalysisContext context) {
        cachedConnects.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        checkElementConnectData();
    }

    private void checkElementConnectData() {
        if (cachedConnects.isEmpty()) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("导入拓扑文件中，节点连线数据为空"));
        } else {
            for (ThirdPartyElementConnectBO cachedConnect : cachedConnects) {
                if (ipIsInvalid(cachedConnect.getFromNodeIp()) || ipIsInvalid(cachedConnect.getToNodeIp())) {
                    throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("节点连线数据的ip不符合ipv4格式"));
                }
            }
        }
    }


    private boolean ipIsInvalid(String ipv4) {
        if (StringUtils.isBlank(ipv4)) {
            return false;
        } else {
            return !IPV4_PATTERN.matcher(ipv4).matches();
        }
    }
}
