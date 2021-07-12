package cn.cyberpeace.mp.systemoperation.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
@ApiModel("节点监控返回")
public class SceneBaseResponse<T> {

    @ApiModelProperty("关联关系")
    private Links links;

    @ApiModelProperty("数据")
    private List<T> data;

    /**
     * 结果解析
     * @param res
     * @param clazz
     * @return
     */
    public SceneBaseResponse<T> parseResult(String res, Class clazz) {
        try {
            // 结果解析
            JSONObject jsonObject = JSONObject.parseObject(res);
            // TODO
//            if (jsonObject.getBoolean("success")) {
                JSONObject linkObj = jsonObject.getJSONObject("links");
                Links links = JSONObject.toJavaObject(linkObj, Links.class);
                JSONArray dataArray = jsonObject.getJSONArray("data");
                List<T> monitorResponseList = JSONArray.parseArray(dataArray.toJSONString(), clazz);
                this.setData(monitorResponseList);
                this.setLinks(links);
                // TODO
//            }
        } catch (Exception e) {
            log.error("结果解析异常", e);
            return new SceneBaseResponse();
        }

        return this;
    }

}
