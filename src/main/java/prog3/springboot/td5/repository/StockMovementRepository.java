package prog3.springboot.td5.repository;

import java.util.List;
import java.time.Instant
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import prog3.springboot.td5.entity.StockMovement;
import prog3.springboot.td5.entity.UnitEnum;
import prog3.springboot.td5.dto.StockMovemenetRequest;

@Repository
public class StockMovementRepository {

    private final JdbcTemplate jdbc;

    public StockMovementRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<StockMovement> findByIngredientId(int ingredientId) {
        return jdbc.query(
                """
            SELECT id, quantity, unit, movement_type, movement_date
            FROM stock_movement
            WHERE id_ingredient = ?
            ORDER BY movement_date
            """,
                (rs, row) -> {
                    StockMovement sm = new StockMovement();
                    sm.setId(rs.getInt("id"));
                    double quantity = rs.getDouble("quantity");
                    if ("OUT".equals(rs.getString("movement_type"))) {
                        quantity = -quantity;
                    }
                    sm.setQuantity(quantity);
                    sm.setUnit(UnitEnum.valueOf(rs.getString("unit")));
                    sm.setMovementDate(rs.getTimestamp("movement_date").toInstant());
                    return sm;
                },
                ingredientId
        );
    }

    public List<StockMovement> findByIngredientIdAndDateRange(int ingredientId, java.time.Instant from, java.time.Instant to) {
        return jdbc.query(
                """
            SELECT id, quantity, unit, movement_type, movement_date
            FROM stock_movement
            WHERE id_ingredient = ?
              AND movement_date >= ?
              AND movement_date <= ?
            ORDER BY movement_date
            """,
                (rs, row) -> {
                    StockMovement sm = new StockMovement();
                    sm.setId(rs.getInt("id"));
                    double quantity = rs.getDouble("quantity");
                    if ("OUT".equals(rs.getString("movement_type"))) {
                        quantity = -quantity;
                    }
                    sm.setQuantity(quantity);
                    sm.setUnit(UnitEnum.valueOf(rs.getString("unit")));
                    sm.setMovementDate(rs.getTimestamp("movement_date").toInstant());
                    return sm;
                },
                ingredientId, java.sql.Timestamp.from(from), java.sql.Timestamp.from(to)
        );
    }

    public StockMovement save(int ingredientId, StockMovementRequest request) {
        return jdbc.query(
                """
            INSERT INTO stock_movement (id_ingredient, quantity, unit, movement_type, movement_date)
            VALUES (?,?, ?::unit_type_enum, ?::movement_type, ?)
            RETURNING id, quantity, unit, movement_type, movement_date
            """,
                (rs, row) -> {
                    StockMovement sm = new StockMovement();
                    sm.setId(rs.getInt("id"));
                    double quantity = rs.getDouble("quantity");
                    if ("OUT".equals(rs.getString("movement_type"))) {
                        quantity = -quantity;
                    }
                    sm.setQuantity(quantity);
                    sm.setUnit(UnitEnum.valueOf(rs.getString("unit")));
                    sm.setMovementDate(rs.getTimestamp("movement_date").toInstant());
                    return sm;
                },
                ingredientId,
        Math.abs(request.getValue()),
        request.getUnit(),
        request.getType(),
        java.sql.Timestamp.from(Instant.now())
        )
    }
}
