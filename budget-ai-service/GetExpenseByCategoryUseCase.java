package dio.budgeting.application;

import dio.budgeting.domain.TransactionRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

// Regra de negócio para calcular o total de gastos de uma categoria
@Service
public class GetExpenseByCategoryUseCase {

    private final TransactionRepository repository;

    public GetExpenseByCategoryUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    // Executa a busca e retorna zero caso nenhuma transação tenha sido encontrada na categoria
    public BigDecimal execute(String category) {
        return repository.findTotalByCategory(category).orElse(BigDecimal.ZERO);
    }
}