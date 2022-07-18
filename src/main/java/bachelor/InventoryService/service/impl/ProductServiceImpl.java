package bachelor.InventoryService.service.impl;

import bachelor.InventoryService.api.ProductDto;
import bachelor.InventoryService.exception.BadRequestException;
import bachelor.InventoryService.model.Category;
import bachelor.InventoryService.model.FeatureName;
import bachelor.InventoryService.model.Product;
import bachelor.InventoryService.repository.CategoryRepository;
import bachelor.InventoryService.repository.FeatureNameRepository;
import bachelor.InventoryService.repository.ProductRepository;
import bachelor.InventoryService.service.AwsKeyManagementService;
import bachelor.InventoryService.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FeatureNameRepository featureNameRepository;
    private final ModelMapper mapper;
    private final AwsKeyManagementService awsKeyManagementService;

    @Override
    public ProductDto getProductById(String id) {
        if (ObjectId.isValid(id)) {
            ObjectId objectId = new ObjectId(id);
            Product product = productRepository.findById(objectId).orElseThrow(() -> new BadRequestException("Product is not found"));
            ProductDto retVal = mapper.map(product, ProductDto.class);
            retVal.setImages(imagesToBase64(product.getImages()));
            return retVal;
        } else {
            throw new BadRequestException("Product id is invalid");
        }
    }

    private Product getProductModelById(String id) {
        if (ObjectId.isValid(id)) {
            ObjectId objectId = new ObjectId(id);
            Product product = productRepository.findById(objectId).orElseThrow(() -> new BadRequestException("Product is not found"));
            return product;
        } else {
            throw new BadRequestException("Product id is invalid");
        }
    }

    private Map<String, String> imagesToBase64(Map<String, byte[]> images) {

        Map<String, String> retVal = new HashMap<>();
        if (images == null) {
            return retVal;
        }
        for (Map.Entry<String, byte[]> image :
                images.entrySet()) {
            retVal.put(image.getKey(), Base64.getEncoder().encodeToString(image.getValue()));
        }
        return retVal;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(product -> {
            var retVal = mapper.map(product, ProductDto.class);
            retVal.setImages(imagesToBase64(product.getImages()));
            return retVal;
        }).collect(Collectors.toList());
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

            Product product = Product.builder().key(awsKeyManagementService.GenerateDataKey().getCiphertext()).name(productDto.getName()).category(productDto.getCategory()).images(null).price(productDto.getPrice()).quantity(0L).features(productDto.getFeatures()).build();
            return mapper.map(productRepository.save(product), ProductDto.class);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Long changeQuantity(String id, long quantity) {
        Product product = getProductModelById(id);
        product.setId(new ObjectId(id));
        product.setQuantity(product.getQuantity() + quantity);
        if (product.getQuantity() < 0) {
            throw new BadRequestException("Product quantity can't be lower than 0");
        }
        productRepository.save(product);
        return product.getQuantity();
    }

    @Override
    public ProductDto orderProduct(String id, long quantity) {
        Product product = getProductModelById(id);
        changeQuantity(id, -quantity);
        Product changedProduct = getProductModelById(id);
        if (product.getQuantity() == changedProduct.getQuantity()) {
            throw new BadRequestException("Not changed");
        }
        changedProduct.setQuantity(quantity);
        ProductDto retVal = mapper.map(changedProduct, ProductDto.class);
        retVal.setId(id);
        return retVal;
    }

    @Override
    public List<ProductDto> orderProducts(List<ProductDto> products) {
        List<ProductDto> retVal = new ArrayList<>();

        products.forEach((productDto -> {
            retVal.forEach(p -> {
                if (p.getId().equals(productDto.getId())) {
                    throw new BadRequestException("There is 2 products with same id");
                }
            });
            retVal.add(orderProduct(productDto.getId(), productDto.getQuantity()));
        }));
        return retVal;
    }

    @SneakyThrows
    @Override
    public String uploadImage(MultipartFile image, String productId) {
        if (ObjectId.isValid(productId)) {
            ObjectId objectId = new ObjectId(productId);
            Product product = productRepository.findById(objectId).orElseThrow(() -> new BadRequestException("Product is not found"));
            String base64Image = product.AddImage(UUID.randomUUID().toString(), image.getBytes());

            product.setId(new ObjectId(productId));
            productRepository.save(product);
            return base64Image;
        }
        throw new BadRequestException("Product id is invalid");
    }

    @Override
    public List<ProductDto> getProductsByCategoryName(String categoryName) {
        return getAllProducts().stream().filter(product -> product.getCategory().equals(categoryName)).collect(Collectors.toList());
    }
}
