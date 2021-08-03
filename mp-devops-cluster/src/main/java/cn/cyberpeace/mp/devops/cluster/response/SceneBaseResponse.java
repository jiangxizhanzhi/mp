package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.base.constant.BaseConstants;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 场景通用返回体
 * @Autho qiaokang
 * @Date：2021/7/9 10:22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@ApiModel("场景通用返回体")
public class SceneBaseResponse<T> {

    @ApiModelProperty("总数")
    private int total;

    @ApiModelProperty("数据")
    private List<T> list;

    /**
     * 结果解析
     * @param res
     * @param clazz
     * @return
     */
    public String parseResult(String res, Class clazz) {
        try {
            // 结果解析
            JSONObject jsonObject = JSONObject.parseObject(res);
            if (StringUtils.isNotBlank(jsonObject.getString(BaseConstants.STR_DETAIL))) {
                return jsonObject.getString(BaseConstants.STR_DETAIL);
            }

            JSONObject linkObj = jsonObject.getJSONObject(BaseConstants.STR_LINKS);
            Links links = JSONObject.toJavaObject(linkObj, Links.class);
            JSONArray dataArray = jsonObject.getJSONArray(BaseConstants.STR_DATA);
            List<T> monitorResponseList = JSONArray.parseArray(dataArray.toJSONString(), clazz);
            this.setList(monitorResponseList);
            this.setTotal(Objects.nonNull(links.getPagination()) ? links.getPagination().getTotal() : 0);
        } catch (Exception e) {
            log.error("结果解析异常", e);
            return "parseResult error";
        }

        return BaseConstants.STR_SUCCESS;
    }

}
