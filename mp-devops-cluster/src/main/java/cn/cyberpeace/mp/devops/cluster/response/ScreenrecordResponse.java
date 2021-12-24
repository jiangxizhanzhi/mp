package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.devops.cluster.model.ScreenshotModel;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 录屏结果
 * @Author qiaokang
 * @Date：2021/9/13 18:51
 */
@Data
@ApiModel("录屏结果")
public class ScreenrecordResponse extends ScreenshotModel {

    /**
     * 录屏时长
     */
    @ApiModelProperty(value = "录屏时长")
    private Double recortime;

    /**
     * 结果转换
     * @param record
     */
    public void parseResult(ScreenrecordPageResponse.Record record) {
        this.setFileName(record.getRecordingFile());
        this.setFileSize(record.getFileSize());
        this.setSceneName(record.getScene());
        // 组织： 管理员|分公司01
        String org = record.getDepartment();
        if (StringUtils.isNotBlank(org) && org.contains("|")) {
            // 将组织拆分成姓名和组织
            int start = org.indexOf("|");
            String userName = org.substring(0, start);
            String orgName = org.substring(start + 1);
            this.setUserName(userName);
            this.setUserOrgName(orgName);
        } else {
            this.setUserName(record.getUsername());
            this.setUserOrgName(org);
        }
        this.setRecortime(record.getRecortime());
        this.setCreateTime(TimeUtils.utcStringToLocalDateTime(record.getRecordate()));
    }

}
