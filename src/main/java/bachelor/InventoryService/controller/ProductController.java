package bachelor.InventoryService.controller;

import bachelor.InventoryService.api.ProductDto;
import bachelor.InventoryService.service.AwsKeyManagementService;
import bachelor.InventoryService.service.ProductService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping(value = "/product", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final AwsKeyManagementService awsKeyManagementService;
    private final Gson gson;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping("")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.createProduct(productDto));
    }

    @PutMapping("/quantity")
    public ResponseEntity<Long> changeQuantity(@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.changeQuantity(productDto.getId(), productDto.getQuantity()));
    }

    @PutMapping("/order")
    public ResponseEntity<byte[]> orderProduct(@RequestParam byte[] product) {

        System.out.println("Method orderProduct - received paramameter: " + Arrays.toString(product));
        String decrypted = awsKeyManagementService.DecryptText(product);
        ProductDto productDto = gson.fromJson(decrypted, ProductDto.class);
        System.out.println("Method orderProduct - decrypted paramameter: " + decrypted);

        ProductDto orderedProduct = productService.orderProduct(productDto.getId(), productDto.getQuantity());

        String json = gson.toJson(orderedProduct);
        String keyId = awsKeyManagementService.GetKeyByAlias("bachelor-order");
        byte[] encrypted = awsKeyManagementService.EncryptText(json, keyId);

        System.out.println("Method orderProduct - sended response: " + Arrays.toString(encrypted));

        return ResponseEntity.ok(encrypted);
    }

    @PutMapping("/order/more")
    public ResponseEntity<byte[]> orderProducts(@RequestParam byte[] products) {
        System.out.println("Method orderProducts - received paramameter: " + Arrays.toString(products));
        Type listType = new TypeToken<ArrayList<ProductDto>>() {}.getType();
        String decrypted = awsKeyManagementService.DecryptText(products);
        List<ProductDto> productDtos = gson.fromJson(decrypted, listType);
        System.out.println("Method orderProducts - decrypted paramameter: " + decrypted);

        List<ProductDto> orderedProducts = productService.orderProducts(productDtos);

        String json = gson.toJson(orderedProducts);
        String keyId = awsKeyManagementService.GetKeyByAlias("bachelor-order");
        byte[] encrypted = awsKeyManagementService.EncryptText(json, keyId);

        System.out.println("Method orderProducts - sended response: " + Arrays.toString(encrypted));

        return ResponseEntity.ok(encrypted);
    }

}
