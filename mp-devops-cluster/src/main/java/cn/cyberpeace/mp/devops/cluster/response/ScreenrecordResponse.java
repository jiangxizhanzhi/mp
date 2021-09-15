package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.base.util.LocalDateTimeUtils;
import cn.cyberpeace.mp.devops.cluster.model.ScreenshotModel;
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
        this.setUserName(record.getUsername());
        this.setUserOrgName(record.getDepartment());
        this.setRecortime(record.getRecortime());
        this.setCreateTime(LocalDateTimeUtils.stringToLocalDateTime(record.getRecordate()));
    }

}
