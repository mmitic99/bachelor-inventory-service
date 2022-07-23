package bachelor.InventoryService.controller;


import bachelor.InventoryService.service.CategoryService;
import bachelor.InventoryService.service.FeatureNameService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/feature-name", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class FeatureNameController {
    private final FeatureNameService featureNameService;

    @GetMapping("")
    public ResponseEntity<List<String>> getAllFeatureNames() {
        return ResponseEntity.ok(featureNameService.GetAllFeatureNames());
    }

}