package prog3.springboot.td5.controller;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prog3.springboot.td5.entity.Dish;
import prog3.springboot.td5.entity.DishIngredient;
import prog3.springboot.td5.service.DishService;

@RestController
@RequestMapping("/dishes")
public class DishController {
  
    private final DishService service;

    public DishController(DishService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Dish>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateIngredients(
            @PathVariable int id,
            @RequestBody(required = false) List<DishIngredient> ingredients) {

        if (ingredients == null) {
            return ResponseEntity.status(400)
                .body("Request body is required.");
        }

       if (service.getById(id).isEmpty()) {
    return ResponseEntity.status(404)
        .body("Dish.id=" + id + " is not found");
}
return ResponseEntity.ok(service.updateIngredients(id, ingredients));
    }
}
