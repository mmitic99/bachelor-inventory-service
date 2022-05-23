package bachelor.InventoryService.repository;

import bachelor.InventoryService.model.Category;
import bachelor.InventoryService.model.FeatureName;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, ObjectId> {
    Category findByName(String category);

}
