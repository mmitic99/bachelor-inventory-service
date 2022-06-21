package bachelor.InventoryService.config.mongoDB;

import bachelor.InventoryService.service.AwsKeyManagementService;
import com.google.gson.Gson;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;

import java.util.Arrays;
import java.util.List;

public class MongoDBBeforeSaveEventListener extends AbstractMongoEventListener<Object> {

    @Autowired
    private AwsKeyManagementService awsKeyManagementService;
    @Autowired
    private Gson gson;

    @Override
    public void onBeforeSave(BeforeSaveEvent<Object> event) {
        if (!event.getCollectionName().equals("categories") && !event.getCollectionName().equals("featureNames")) {
            String keyId = awsKeyManagementService.GetKeyByAlias("bachelor-order");

            Document eventObject = event.getDocument();

            List<String> keysNotToEncrypt = Arrays.asList("_class", "_id");
            System.out.println(eventObject.toString());
            for (String key :
                    eventObject.keySet()) {
                if (!keysNotToEncrypt.contains(key)) {
                    if (key.equals("images") || key.equals("features")) {
                        eventObject.put(key, this.awsKeyManagementService.EncryptText(gson.toJson(eventObject.get(key)), keyId));

                    } else {
                        eventObject.put(key, this.awsKeyManagementService.EncryptText(eventObject.get(key).toString(), keyId));
                    }
                }
            }
        }
        super.onBeforeSave(event);
    }
}
