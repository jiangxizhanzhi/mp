package cn.cyberpeace.mp.collection.util;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.search.SearchResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EsResponseParseUtil {
    public static <T> List<T> parseResponse(SearchResponse response, Class<T> tClass) {
        List<T> list = new ArrayList<>();
        if (Objects.isNull(response) || Objects.isNull(response.getHits()) || Objects.isNull(response.getHits().getHits())) {
            return list;
        }
        for (var hit : response.getHits().getHits()) {
            list.add(JSON.parseObject(hit.getSourceAsString(), tClass));
        }
        return list;
    }
}
