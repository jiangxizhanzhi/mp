package cn.cyberpeace.mp.auth.request.common;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.Data;

import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class PageRequest extends QueryParams {

    public void ensure() {
        if (this.getPerPage() != null) {
            this.setPageSize(this.getPerPage());
        }

        // 默认值
        if (Objects.isNull(this.getPage()) || this.getPage() <= 1) {
            this.setPage(1);
        }

        if (Objects.isNull(this.getPageSize())) {
            this.setPageSize(10);
        }

        if (this.getSort() != null && this.getSort().length > 0) {
            this.setOrders(this.getSort());
        }
    }

}
