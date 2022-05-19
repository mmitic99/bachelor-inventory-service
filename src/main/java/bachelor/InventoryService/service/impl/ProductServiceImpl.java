package bachelor.InventoryService.service.impl;

import bachelor.InventoryService.exception.BadRequestException;
import bachelor.InventoryService.model.Product;
import bachelor.InventoryService.repository.ProductRepository;
import bachelor.InventoryService.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public void create() {
        try {
            productRepository.save(Product.builder().name("ime").quantity(154).build());
        }
        catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }
}
