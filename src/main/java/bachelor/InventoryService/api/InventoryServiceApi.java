package bachelor.InventoryService.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service")
public interface InventoryServiceApi {
    @GetMapping(value = "/inventory")
    String get(@RequestParam String param);
}
