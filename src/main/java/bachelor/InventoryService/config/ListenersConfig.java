package bachelor.InventoryService.config;

import bachelor.InventoryService.config.mongoDB.MongoDBAfterLoadEventListener;
import bachelor.InventoryService.config.mongoDB.MongoDBBeforeSaveEventListener;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class ListenersConfig {

    @Bean
    public MongoDBBeforeSaveEventListener mongoDBBeforeSaveEventListener() {
        return new MongoDBBeforeSaveEventListener();
    }

    @Bean
    public MongoDBAfterLoadEventListener mongoDBAfterLoadEventListener() {
        return new MongoDBAfterLoadEventListener();
    }
}