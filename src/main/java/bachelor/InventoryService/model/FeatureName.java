package bachelor.InventoryService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("featureNames")
public class FeatureName {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
