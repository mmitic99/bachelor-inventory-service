package bachelor.InventoryService.service;

import bachelor.InventoryService.dto.ProductDto;
import bachelor.InventoryService.model.Product;

import java.util.List;

public interface ProductService {
    ProductDto getProductById(String id);
    List<ProductDto> getAllProducts();
    ProductDto createProduct(ProductDto productDto);
    Long changeQuantity(String id, long quantity);
    Boolean orderProduct(String id, long quantity);
}
