package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页返回结果
 * @param <T>
 */
@ApiModel
@Data
public class SnResultPage<T>{

    /**
     * 具体数据内容
     */
    @ApiModelProperty(value = "具体数据内容")
    private List<T> data = new ArrayList<>();

    /**
     * 关联分页信息
     */
    @ApiModelProperty(value = "关联分页信息")
    private Links links;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class Links{

        /**
         * 分页信息
         */
        private SnPage pagination;
    }


    public SnResultPage(){}


    public SnResultPage(List<T> data) {
        this.data = data;
    }

    public SnResultPage(Links links) {
        this.links = links;
    }

    public SnResultPage(SnPage snPage) {
        this.links = Links.builder().pagination(snPage).build();
    }

    public SnResultPage(List<T> data, Links links) {
        this.data = data;
        this.links = links;
    }

    public SnResultPage(List<T> data, SnPage snPage) {
        this.data = data;
        this.links = Links.builder().pagination(snPage).build();
    }
}
