package bachelor.InventoryService.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("products")
public class Product {
    @Id
    private ObjectId id;
    private String name;
    private double price;
    private long quantity;
    private String category;
    private Map<String, byte[]> images;
    private Map<String, List<String>> features;

    public String AddImage(String name, byte[] image){
        if(images == null){
            images = new LinkedHashMap<>();
        }
        images.put(name, image);
        return Base64.getEncoder().encodeToString(image);
    }
}
