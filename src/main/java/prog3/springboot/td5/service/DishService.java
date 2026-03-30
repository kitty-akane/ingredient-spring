package prog3.springboot.td5.service;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import prog3.springboot.td5.entity.Dish;
import prog3.springboot.td5.entity.DishIngredient;
import prog3.springboot.td5.repository.DishRepository;

@Service
public class DishService {
  private final DishRepository repository;

    public DishService(DishRepository repository) {
        this.repository = repository;
    }

    public List<Dish> getAll() {
        return (List<Dish>) repository.findAll();
    }

    public Optional<Dish> getById(int id) {
        return (Optional<Dish>) repository.findById(id);
    }

    public Dish updateIngredients(int id, List<DishIngredient> ingredients) {
        return (Dish) repository.updateIngredients(id, ingredients);
    }
}
