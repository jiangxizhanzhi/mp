package cn.cyberpeace.mp.devops.log.request;

import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Objects;

/**
 * 操作日志实体类
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class OperationLogPageRequest extends BasePageRequest{

    /**
     * 站点类型，0：中台
     */
    @ApiModelProperty(value = "0:中台",hidden = true)
    private SiteTypeEnums siteType = SiteTypeEnums.MP;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String userName;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**
     * 模块
     */
    @ApiModelProperty(value = "模块 (0:系统; 1:工具; 2:课程; 3:资源; 4:账号; 5:场景; 6:职业体系; 7:考试; 8:习题集管理; 9:夺旗解题; 10:理论基础; 11:试卷; 12:任务; 13:流量; 14:探针; 15:靶标)")
    private Integer module;

    /**
     * 操作类型
     */
    @ApiModelProperty(value = "操作类型 (0:创建; 1:更新; 2:删除; 3:登录登出)")
    private Integer opType;

    /**
     * 用户名/操作对象
     */
    @ApiModelProperty(value = "用户名/操作对象")
    private String userNameOrOpTarget;

    /**
     * 构造BoolQueryBuilder
     * @return
     */
    public BoolQueryBuilder buildBoolQueryBuilder(){
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        String module = Objects.nonNull(this.getModule()) ? OpModuleEnums.fromValue(this.getModule()).getDesc() : null;
        String userName = this.getUserName();
        String userId = this.getUserId();
        String opType = Objects.nonNull(this.getOpType()) ? OpTypeEnums.fromValue(this.getOpType()).getDesc() : null;
        String userNameOrOpTarget = this.getUserNameOrOpTarget();
        if (StringUtils.isNotBlank(module)) {
            boolBuilder.must(QueryBuilders.termQuery("opModule.keyword", module));
        }
        if (StringUtils.isNotBlank(userId)) {
            boolBuilder.must(QueryBuilders.termQuery("userId.keyword",userId));
        }

        if (StringUtils.isNotBlank(userName)) {
            boolBuilder.must(QueryBuilders.wildcardQuery("userName.keyword", concat("*",userName,"*")));
        }
        if (StringUtils.isNotBlank(opType)) {
            boolBuilder.must(QueryBuilders.termQuery("opType.keyword", opType));
        }
        if (StringUtils.isNotBlank(userNameOrOpTarget)){
            BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
            orQuery.should(QueryBuilders.wildcardQuery("userName.keyword", concat("*",userNameOrOpTarget,"*")));
            orQuery.should(QueryBuilders.wildcardQuery("opTarget.keyword", concat("*",userNameOrOpTarget,"*")));
            boolBuilder.must(orQuery);
        }

        return boolBuilder;
    }

    /**
     * 构建SearchSourceBuilder对象
     * @return
     */
    public SearchSourceBuilder buildSearchSourceBuilder(){
        return SearchSourceBuilder.searchSource().query(this.buildBoolQueryBuilder())
                .sort("opTime", SortOrder.DESC)
                .from(this.getOffset() - 1 > 0 ? this.getOffset() - 1 : 0)
                .size(this.getLimit());
    }

    /**
     * 拼接字符串
     * @param prefix
     * @param middle
     * @param suffix
     * @return
     */
    private String concat(String prefix,String middle,String suffix){
        return prefix.concat(middle).concat(suffix);
    }
}
