package bachelor.InventoryService.service.impl;

import bachelor.InventoryService.api.ProductDto;
import bachelor.InventoryService.dto.ImageDto;
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
        if (id != null && ObjectId.isValid(id)) {
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
        Category category = getCategory(productDto);

        Map<String, List<String>> features = getFeatures(productDto);

        Product product = Product.builder().key(awsKeyManagementService.GenerateDataKey().getCiphertext()).name(productDto.getName()).category(category.getName()).images(null).price(productDto.getPrice()).quantity(0L).features(features).build();
        return mapper.map(productRepository.save(product), ProductDto.class);

    }

    private Map<String, List<String>> getFeatures(ProductDto productDto) {
        try {
            Map<String, List<String>> features = new HashMap<>();

            productDto.getFeatures().forEach((key, value) -> {
                features.put(key.toLowerCase(), value);
                if (featureNameRepository.findByName(key.toLowerCase()) == null) {
                    //throw new BadRequestException("Not recognize feature");
                    featureNameRepository.save(FeatureName.builder().name(key.toLowerCase()).build());
                }
            });
            return features;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Category getCategory(ProductDto productDto) {
        try {
            Category category = categoryRepository.findByName(productDto.getCategory().toLowerCase());
            if (category == null) {
                //throw new BadRequestException("Not recognize category");
                category = categoryRepository.save(Category.builder().name(productDto.getCategory().toLowerCase()).build());
            }
            return category;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ProductDto editProduct(ProductDto productDto) {
        Product product = getProductModelById(productDto.getId());

        Category category = getCategory(productDto);
        Map<String, List<String>> features = getFeatures(productDto);

        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setCategory(category.getName());
        product.setFeatures(features);
        return mapper.map(productRepository.save(product), ProductDto.class);

    }

    @Override
    public List<ProductDto> filter(String searchParam, String categoryName, List<String> screenDiagonals, List<String> screenResolutions, List<String> processorProducer, List<String> ram, List<String> hdd) {
        List<ProductDto> products;
        List<ProductDto> retVal = new ArrayList<>();
        if(searchParam!=null && !searchParam.equals("")){
            products = search(searchParam);
        }
        else if (categoryName.equals("all")) {
            products = getAllProducts();
        } else {
            products = getProductsByCategoryName(categoryName);
        }
        for (ProductDto product :
                products) {
            if (isExists(product.getFeatures().get("dijagonala ekrana"), screenDiagonals) &&
                    isExists(product.getFeatures().get("rezolucija ekrana"), screenResolutions) &&
                    isExists(product.getFeatures().get("procesor"), processorProducer) &&
                    isExists(product.getFeatures().get("ram memorija"), ram) &&
                    isExists(product.getFeatures().get("hard disk"), hdd))
                retVal.add(product);
        }
        return retVal;
    }

    @Override
    public List<ProductDto> search(String searchParam) {
        List<ProductDto> products = getAllProducts();
        List<ProductDto> retVal = new ArrayList<>();

        for (ProductDto productDto :
                products) {
            if (productDto.getName().toLowerCase().equals(searchParam.toLowerCase()) || productDto.getName().toLowerCase().contains(searchParam.toLowerCase()) ||
                    productDto.getCategory().toLowerCase().equals(searchParam.toLowerCase()) || productDto.getCategory().toLowerCase().contains(searchParam.toLowerCase())) {
                retVal.add(productDto);
            }
        }
        return retVal;
    }

    private boolean isExists(List<String> features, List<String> filters) {
        if (filters.size() == 0) {
            return true;
        }

        if (features == null && filters.size() != 0) {
            return false;
        }

        for (String feature :
                features) {
            for (String filter :
                    filters) {
                if (feature.toLowerCase().equals(filter.toLowerCase()) || feature.toLowerCase().contains(filter.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
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
    public ImageDto uploadImage(MultipartFile image, String productId) {
        if (ObjectId.isValid(productId)) {
            ObjectId objectId = new ObjectId(productId);
            Product product = productRepository.findById(objectId).orElseThrow(() -> new BadRequestException("Product is not found"));
            String imageId = UUID.randomUUID().toString();
            String base64Image = product.AddImage(imageId, image.getBytes());

            product.setId(new ObjectId(productId));
            productRepository.save(product);
            return ImageDto.builder().id(imageId).base64Image(base64Image).build();
        }
        throw new BadRequestException("Product id is invalid");
    }

    @Override
    public List<ProductDto> getProductsByCategoryName(String categoryName) {
        return getAllProducts().stream().filter(product -> product.getCategory().equals(categoryName)).collect(Collectors.toList());
    }

    @Override
    public void removeImage(String imageId, String productId) {
        var product = getProductModelById(productId);
        product.getImages().remove(imageId);
        product.setId(new ObjectId(productId));
        productRepository.save(product);
    }
}
