package dio.budgeting.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

// Interface de repositório que herda métodos do Spring Data JPA (save, findAll, etc.)
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Consulta customizada em JPQL para somar os valores de transações por categoria
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE LOWER(t.category) = LOWER(:category)")
    Optional<BigDecimal> findTotalByCategory(String category);
}