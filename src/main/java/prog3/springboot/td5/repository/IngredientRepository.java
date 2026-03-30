package prog3.springboot.td5.repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import prog3.springboot.td5.entity.CategoryEnum;
import prog3.springboot.td5.entity.Ingredient;
import prog3.springboot.td5.entity.StockMovement;

@Repository
public class IngredientRepository {
 private final JdbcTemplate jdbc;
    private final StockMovementRepository stockMovementRepository;

    public IngredientRepository(JdbcTemplate jdbc, StockMovementRepository stockMovementRepository) {
        this.jdbc = jdbc;
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<Ingredient> findAll() {
        return jdbc.query(
            "SELECT id, name, category, price FROM Ingredient",
            (rs, row) -> new Ingredient(
                rs.getInt("id"),
                rs.getString("name"),
                CategoryEnum.valueOf(rs.getString("category")),
                rs.getDouble("price")
            )
        );
    }

    public Optional<Ingredient> findById(int id) {
        List<Ingredient> result = jdbc.query(
            "SELECT id, name, category, price FROM Ingredient WHERE id = ?",
            (rs, row) -> new Ingredient(
                rs.getInt("id"),
                rs.getString("name"),
                CategoryEnum.valueOf(rs.getString("category")),
                rs.getDouble("price")
            ),
            id
        );
        if (result.isEmpty()) return Optional.empty();
        Ingredient ingredient = result.get(0);
        ingredient.setStockMovementList((List<StockMovement>) stockMovementRepository.findByIngredientId(id));
        return Optional.of(ingredient);
    }

    public Double getStockAt(int id, Instant at, String unit) {
        return findById(id)
            .map(ingredient -> ingredient.getStockValueAt(at))
            .orElse(null);
    }
}
