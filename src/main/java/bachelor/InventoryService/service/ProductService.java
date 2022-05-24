package bachelor.InventoryService.service;

import bachelor.InventoryService.api.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto getProductById(String id);
    List<ProductDto> getAllProducts();
    ProductDto createProduct(ProductDto productDto);
    Long changeQuantity(String id, long quantity);
    ProductDto orderProduct(String id, long quantity);

    List<ProductDto> orderProducts(List<ProductDto> products);
}
