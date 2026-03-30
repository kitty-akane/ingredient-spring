package prog3.springboot.td5.service;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import prog3.springboot.td5.entity.Ingredient;
import prog3.springboot.td5.repository.IngredientRepository;

@Service
public class IngredientService {
  private final IngredientRepository repository;

    public IngredientService(IngredientRepository repository) {
        this.repository = repository;
    }

    public List<Ingredient> getAll() {
        return repository.findAll();
    }

    public Optional<Ingredient> getById(int id) {
        return repository.findById(id);
    }

    public Double getStockAt(int id, Instant at, String unit) {
        return repository.getStockAt(id, at, unit);
    }
}
