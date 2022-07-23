package bachelor.InventoryService.service.impl;

import bachelor.InventoryService.model.Category;
import bachelor.InventoryService.model.FeatureName;
import bachelor.InventoryService.repository.CategoryRepository;
import bachelor.InventoryService.repository.FeatureNameRepository;
import bachelor.InventoryService.service.FeatureNameService;
import bachelor.InventoryService.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FeatureNameServiceImpl implements FeatureNameService {
    private final FeatureNameRepository featureNameRepository;

    @Override
    public List<String> GetAllFeatureNames() {
        return featureNameRepository.findAll().stream().map(FeatureName::getName).collect(Collectors.toList());
    }
}
