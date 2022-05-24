package bachelor.InventoryService.api;

import bachelor.InventoryService.model.Category;
import bachelor.InventoryService.model.FeatureName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    private String id;
    private String name;
    private double price;
    private long quantity;
    private String category;
    private Map<String, byte[]> images;
    private Map<String, List<String>> features;
}
