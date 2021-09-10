package cn.cyberpeace.mp.base.util.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2021/9/8
 */
public class MpHttpHeaders {
    protected static MpHttpHeaders DEFAULT_POST_HEADERS = MpHttpHeaders.builder()
            .addContentType("application/json")
            .build();

    private MergeStrategy mergeStrategy;

    private Map<String, String> values;

    public static Builder builder() {
        return new Builder(MergeStrategy.CUSTOMER_FIRST_MERGE);
    }

    public static Builder builder(MergeStrategy mergeStrategy) {
        return new Builder(mergeStrategy);
    }

    private MpHttpHeaders(MergeStrategy mergeStrategy) {
        this.mergeStrategy = mergeStrategy;
        this.values = new HashMap<>();
    }

    public Map<String, String> getValues() {
        return this.values;
    }

    protected void add(String name, String value) {
        this.values.put(name, value);
    }

    protected void merge(MpHttpHeaders defaultHeaders) {
        if (this.mergeStrategy == MergeStrategy.IGNORE_DEFAULT) {
            return;
        } else if (this.mergeStrategy == MergeStrategy.CUSTOMER_FIRST_MERGE) {
            Map<String, String> defaultHeaderMaps = defaultHeaders.getValues();
            defaultHeaderMaps.keySet().forEach(key -> {
                if (!this.values.containsKey(key)) {
                    this.values.put(key, defaultHeaderMaps.get(key));
                }
            });
        } else if (this.mergeStrategy == MergeStrategy.DEFAULT_FIRST_MERGE) {
            Map<String, String> defaultHeaderMaps = defaultHeaders.getValues();
            defaultHeaderMaps.keySet().forEach(key -> {
                this.values.put(key, defaultHeaderMaps.get(key));
            });
        }
    }

    public static class Builder {
        private MpHttpHeaders mpHttpHeaders;

        public Builder(MergeStrategy mergeStrategy) {
            this.mpHttpHeaders = new MpHttpHeaders(mergeStrategy);
        }

        public Builder addAuthorization(String auth) {
            this.add("Authorization", auth);
            return this;
        }

        public Builder addContentType(String contentType) {
            this.add("Content-Type", contentType);
            return this;
        }

        public Builder add(String name, String value) {
            this.mpHttpHeaders.add(name, value);
            return this;
        }

        public MpHttpHeaders build() {
            return this.mpHttpHeaders;
        }
    }

    public enum MergeStrategy {
        /**
         *
         */
        IGNORE_DEFAULT, DEFAULT_FIRST_MERGE, CUSTOMER_FIRST_MERGE
    }
}
