package bachelor.InventoryService.service.impl;

import bachelor.InventoryService.model.Category;
import bachelor.InventoryService.repository.CategoryRepository;
import bachelor.InventoryService.service.CategoryService;
import bachelor.InventoryService.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public List<String> GetAllCategories() {
        return categoryRepository.findAll().stream().map(Category::getName).collect(Collectors.toList());
    }
}
