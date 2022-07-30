package bachelor.InventoryService.service;

import bachelor.InventoryService.api.ProductDto;
import bachelor.InventoryService.dto.ImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductDto getProductById(String id);
    List<ProductDto> getAllProducts();
    ProductDto createProduct(ProductDto productDto);
    Long changeQuantity(String id, long quantity);
    ProductDto orderProduct(String id, long quantity);

    List<ProductDto> orderProducts(List<ProductDto> products);

    ImageDto uploadImage(MultipartFile image, String productId);

    List<ProductDto> getProductsByCategoryName(String categoryName);

    void removeImage(String imageId, String productId);

    ProductDto editProduct(ProductDto productDto);
}
