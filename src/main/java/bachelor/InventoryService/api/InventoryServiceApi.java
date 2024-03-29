package bachelor.InventoryService.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "inventory-service")
public interface InventoryServiceApi {
    @GetMapping(value = "/inventory")
    String get(@RequestParam String param);

    @PutMapping("/product/order/more")
    ResponseEntity<byte[]> orderProducts(@RequestParam byte[] products);

    @PutMapping("/product/order")
    ResponseEntity<byte[]> orderProduct(@RequestParam byte[] product);
}
