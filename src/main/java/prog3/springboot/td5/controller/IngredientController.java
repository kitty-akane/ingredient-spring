package prog3.springboot.td5.controller;

import java.time.Instant;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prog3.springboot.td5.entity.Ingredient;
import prog3.springboot.td5.service.IngredientService;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService service;

    public IngredientController(IngredientService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Ingredient>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        if (service.getById(id).isEmpty()) {
            return ResponseEntity.status(404)
                .body("Ingredient.id=" + id + " is not found");
        }
        return ResponseEntity.ok(service.getById(id).get());
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getStock(
            @PathVariable int id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit) {
        if (at == null || unit == null) {
            return ResponseEntity.status(400)
                .body("Either mandatory query parameter `at` or `unit` is not provided.");
        }
        if (service.getById(id).isEmpty()) {
            return ResponseEntity.status(404)
                .body("Ingredient.id=" + id + " is not found");
        }
        Double stock = service.getStockAt(id, Instant.parse(at), unit);
        return ResponseEntity.ok(stock);
    }
}
