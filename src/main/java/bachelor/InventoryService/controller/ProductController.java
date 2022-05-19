package bachelor.InventoryService.controller;

import bachelor.InventoryService.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/product", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<String> get(){
        productService.create();
        return ResponseEntity.ok("ok");
    }
}
