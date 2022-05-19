package bachelor.InventoryService.repository;

import bachelor.InventoryService.model.FeatureName;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeatureNameRepository extends MongoRepository<FeatureName, ObjectId> {
}
