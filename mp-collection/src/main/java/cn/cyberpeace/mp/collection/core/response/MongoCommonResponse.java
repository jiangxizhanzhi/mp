package cn.cyberpeace.mp.collection.core.response;

import com.mongodb.client.FindIterable;
import lombok.Data;
import org.bson.Document;

@Data
public class MongoCommonResponse {
    private FindIterable<Document> findIterable;
    private long total;
}
