package cn.cyberpeace.mp.cscene.controller.collect.multi;

import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhDataQueryParamsBuilder;
import lombok.Getter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WazuhMultiDataQueryParamsBuilder implements WazuhDataQueryParamsBuilder {

    private List<WazuhDataQueryParamsBuilder> paramsBuilders;

    public WazuhMultiDataQueryParamsBuilder() {
        this.paramsBuilders = new ArrayList<>();
    }

    @Override
    public BoolQueryBuilder build() {
        BoolQueryBuilder finalBuilder = QueryBuilders.boolQuery();
        for (var builder : paramsBuilders) {
            finalBuilder.should(builder.build());
        }
        return finalBuilder;
    }

    @Override
    public List<SortBuilder<?>> sorts() {
        List<SortBuilder<?>> sortBuilders = new ArrayList<>();
        sortBuilders.add(SortBuilders.fieldSort("timestamp").order(SortOrder.ASC));
        return sortBuilders;
    }

    public void addBuilder(WazuhDataQueryParamsBuilder builder){
        this.paramsBuilders.add(builder);
    }

}
