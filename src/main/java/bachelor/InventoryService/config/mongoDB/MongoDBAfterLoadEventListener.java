package bachelor.InventoryService.config.mongoDB;

import bachelor.InventoryService.model.Product;
import bachelor.InventoryService.service.AwsKeyManagementService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent;

import java.lang.reflect.Type;
import java.util.*;

public class MongoDBAfterLoadEventListener extends AbstractMongoEventListener<Object> {
    @Autowired
    private AwsKeyManagementService awsKeyManagementService;
    @Autowired
    private Gson gson;

    @SneakyThrows
    @Override
    public void onAfterLoad(AfterLoadEvent<Object> event) {
        if (!event.getCollectionName().equals("categories") && !event.getCollectionName().equals("featureNames")) {
            Document eventObject = event.getDocument();

            List<String> keysNotToEncrypt = Arrays.asList("_class", "_id", "images");

            for (String key :
                    eventObject.keySet()) {
                if (!keysNotToEncrypt.contains(key)) {
                    Binary bytes = (Binary) eventObject.get(key);

                    String value = this.awsKeyManagementService.DecryptText(bytes.getData());

                    if (key.equals("images")) {
                        Type listType = new TypeToken<Map<String, byte[]>>() {
                        }.getType();
                        eventObject.put(key, gson.fromJson(value, listType));
                    } else if (key.equals("features")) {
                        Type listType = new TypeToken<HashMap<String, List<String>>>() {
                        }.getType();
                        eventObject.put(key, gson.fromJson(value, listType));
                    } else {
                        eventObject.put(key, value);
                    }
                }
            }
        }
        super.onAfterLoad(event);

    }
}
