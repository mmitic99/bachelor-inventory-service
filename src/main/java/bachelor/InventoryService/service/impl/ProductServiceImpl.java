package bachelor.InventoryService.service.impl;

import bachelor.InventoryService.dto.ProductDto;
import bachelor.InventoryService.exception.BadRequestException;
import bachelor.InventoryService.model.Category;
import bachelor.InventoryService.model.FeatureName;
import bachelor.InventoryService.model.Product;
import bachelor.InventoryService.repository.CategoryRepository;
import bachelor.InventoryService.repository.FeatureNameRepository;
import bachelor.InventoryService.repository.ProductRepository;
import bachelor.InventoryService.service.ProductService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FeatureNameRepository featureNameRepository;
    private final ModelMapper mapper;

    @Override
    public ProductDto getProductById(String id) {
        if (ObjectId.isValid(id)) {
            ObjectId objectId = new ObjectId(id);
            Product product = productRepository.findById(objectId).orElseThrow(() -> new BadRequestException("Product is not found"));
            return mapper.map(product, ProductDto.class);
        } else {
            throw new BadRequestException("Product id is invalid");
        }
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(product -> mapper.map(product, ProductDto.class)).collect(Collectors.toList());
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        try {
            if (categoryRepository.findByName(productDto.getCategory().toLowerCase()) == null) {
                //throw new BadRequestException("Not recognize category");
                categoryRepository.save(Category.builder().name(productDto.getCategory().toLowerCase()).build());
            }

            productDto.getFeatures().forEach((key, value) -> {
                if (featureNameRepository.findByName(key) == null) {
                    //throw new BadRequestException("Not recognize feature");
                    featureNameRepository.save(FeatureName.builder().name(key.toLowerCase()).build());
                }
            });

            Product product = Product.builder().name(productDto.getName()).category(productDto.getCategory()).images(productDto.getImages()).price(productDto.getPrice()).quantity(0L).features(productDto.getFeatures()).build();
            return mapper.map(productRepository.save(product), ProductDto.class);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Long changeQuantity(String id, long quantity) {
        Product product = mapper.map(getProductById(id), Product.class);
        product.setId(new ObjectId(id));
        product.setQuantity(product.getQuantity() + quantity);
        if (product.getQuantity() < 0) {
            throw new BadRequestException("Product quantity can't be lower than 0");
        }
        productRepository.save(product);
        return product.getQuantity();
    }

    @Override
    public Boolean orderProduct(String id, long quantity) {
        Product product = mapper.map(getProductById(id), Product.class);
        changeQuantity(id, quantity);
        Product changedProduct = mapper.map(getProductById(id), Product.class);
        if(product.getQuantity() == changedProduct.getQuantity()){
            return false;
        }
        return true;
    }
}
