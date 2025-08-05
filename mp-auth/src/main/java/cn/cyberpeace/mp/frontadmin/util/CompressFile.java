package cn.cyberpeace.mp.frontadmin.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.Compress;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadmin.repository.SysConfigMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

public class CompressFile {


    public static File compressZip(File inputFile) {
        SysConfigMapper sysConfigMapper = BeanCommon.getBean(SysConfigMapper.class);
        List<SysConfigModel> sysConfigModelList = sysConfigMapper.list(new HumanCondition().filterEqual(SysConfigModel::getKeyName, SysConfigModel.KeyName.FLOW_COMPRESSION_TYPE));
        List<SysConfigModel> passwordList = sysConfigMapper.list(new HumanCondition().filterEqual(SysConfigModel::getKeyName, SysConfigModel.KeyName.FLOW_COMPRESSION__PASSWORD));
        String password = "";
        if (CollectionUtils.isNotEmpty(passwordList) && StringUtils.isNotBlank(passwordList.get(0).getValue())) {
            password = passwordList.get(0).getValue();
        }
        if (CollectionUtils.isEmpty(sysConfigModelList) || SysConfigModel.COMPRESSION_TYPE.ZIP.equals(
                sysConfigModelList.get(0).getValue())) {
            return Compress.compressZip(inputFile, password);
        } else {
            return Compress.compressLZ4(inputFile);
        }
    }
}
