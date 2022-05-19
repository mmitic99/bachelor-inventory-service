package bachelor.InventoryService.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("products")
public class Product {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String name;
    private double price;
    private long quantity;
    private Category category;
    private Map<String, byte[]> images;
    private Map<FeatureName, List<String>> features;
}
