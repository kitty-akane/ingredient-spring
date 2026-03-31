package prog3.springboot.td5.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import prog3.springboot.td5.entity.CategoryEnum;
import prog3.springboot.td5.entity.Dish;
import prog3.springboot.td5.entity.DishIngredient;
import prog3.springboot.td5.entity.DishTypeEnum;
import prog3.springboot.td5.entity.Ingredient;
import prog3.springboot.td5.entity.UnitEnum;
@Repository
public class DishRepository {
    private final JdbcTemplate jdbc;

    public DishRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Dish> findAll() {
        List<Dish> dishes = jdbc.query(
            "SELECT id, name, dish_type_enum, selling_price FROM Dish",
            (rs, row) -> {
                Dish d = new Dish();
                d.setId(rs.getInt("id"));
                d.setName(rs.getString("name"));
                d.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type_enum")));
                d.setPrice(rs.getObject("selling_price") != null
                    ? rs.getDouble("selling_price") : null);
                return d;
            }
        );
        dishes.forEach(d -> d.setIngredients(findIngredientsByDishId(d.getId())));
        return dishes;
    }

    public Optional<Dish> findById(int id) {
        List<Dish> result = jdbc.query(
            "SELECT id, name, dish_type_enum, selling_price FROM Dish WHERE id = ?",
            (rs, row) -> {
                Dish d = new Dish();
                d.setId(rs.getInt("id"));
                d.setName(rs.getString("name"));
                d.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type_enum")));
                d.setPrice(rs.getObject("selling_price") != null
                    ? rs.getDouble("selling_price") : null);
                return d;
            },
            id
        );
        if (result.isEmpty()) return Optional.empty();
        Dish dish = result.get(0);
        dish.setIngredients(findIngredientsByDishId(id));
        return Optional.of(dish);
    }

    public List<DishIngredient> findIngredientsByDishId(int dishId) {
        return jdbc.query(
            """
            SELECT i.id, i.name, i.category, i.price,
                   di.quantity_required, di.unit
            FROM DishIngredient di
            JOIN Ingredient i ON di.id_ingredient = i.id
            WHERE di.id_dish = ?
            """,
            (rs, row) -> {
                Ingredient ing = new Ingredient(
                    rs.getInt("id"),
                    rs.getString("name"),
                    CategoryEnum.valueOf(rs.getString("category")),
                    rs.getDouble("price")
                );
                DishIngredient di = new DishIngredient();
                di.setIngredient(ing);
                di.setQuantity(rs.getDouble("quantity_required"));
                di.setUnit(UnitEnum.valueOf(rs.getString("unit")));
                return di;
            },
            dishId
        );
    }

    public Dish updateIngredients(int dishId, List<DishIngredient> ingredients) {
        if (ingredients.isEmpty()) {
            jdbc.update("DELETE FROM DishIngredient WHERE id_dish = ?", dishId);
            return findById(dishId).orElseThrow();
        }

        List<Integer> ids = ingredients.stream()
            .map(di -> di.getIngredient().getId())
            .toList();

        String placeholders = ids.stream()
            .map(i -> "?")
            .collect(java.util.stream.Collectors.joining(", "));

        Object[] params = new Object[ids.size() + 1];
        params[0] = dishId;
        for (int i = 0; i < ids.size(); i++) params[i + 1] = ids.get(i);

        jdbc.update(
            "DELETE FROM DishIngredient WHERE id_dish = ? AND id_ingredient NOT IN (" + placeholders + ")",
            params
        );

        for (DishIngredient di : ingredients) {
            jdbc.update(
                """
                INSERT INTO DishIngredient (id_dish, id_ingredient, quantity_required, unit)
                VALUES (?, ?, ?, ?::unit_type_enum)
                ON CONFLICT (id_dish, id_ingredient)
                DO UPDATE SET quantity_required = EXCLUDED.quantity_required,
                              unit = EXCLUDED.unit
                """,
                dishId,
                di.getIngredient().getId(),
                di.getQuantity(),
                di.getUnit()
            );
        }
        return findById(dishId).orElseThrow();
    }
}
