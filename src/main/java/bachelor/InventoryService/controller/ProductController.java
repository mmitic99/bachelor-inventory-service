package bachelor.InventoryService.controller;

import bachelor.InventoryService.dto.ProductDto;
import bachelor.InventoryService.service.ProductService;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping(value = "/product", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final Gson gson;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("")
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping("")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto){
        return ResponseEntity.ok(productService.createProduct(productDto));
    }

    @PutMapping("/quantity")
    public ResponseEntity<Long> changeQuantity(@RequestBody ProductDto productDto){
        return ResponseEntity.ok(productService.changeQuantity(productDto.getId(), productDto.getQuantity()));
    }

    @PutMapping("/order")
    public ResponseEntity<Boolean> orderProduct(@RequestParam String body){
        //body = aws.kms.decrypt
        ProductDto productDto = gson.fromJson(new String(Base64.getDecoder().decode(body)), ProductDto.class);
        return ResponseEntity.ok(productService.orderProduct(productDto.getId(), productDto.getQuantity()));
    }


}
