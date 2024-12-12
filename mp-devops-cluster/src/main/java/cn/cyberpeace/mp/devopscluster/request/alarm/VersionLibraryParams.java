package cn.cyberpeace.mp.devopscluster.request.alarm;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @Description
 * @Author shgang
 * @Date 2022/11/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VersionLibraryParams extends QueryParams {
    @ApiModelProperty(value = "软件名称")
    private String name;

    @ApiModelProperty(value = "类型(0:软件 1:固件)")
    private Integer type;

    public void ensure() {
        if (getPerPage() != null) {
            setPageSize(getPerPage());
        }

        /*// 默认值
        if (Objects.isNull(getPage()) || getPage() <= 1) {
            setPage(1);
        }

        if (Objects.isNull(getPageSize())) {
            setPageSize(10);
        }*/
    }
}
