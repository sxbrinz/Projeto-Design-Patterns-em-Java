package dio.budgeting.application;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

// Indica que esta classe contém a regra de negócio do caso de uso de criação
@Service
public class CreateTransactionUseCase {

    private final TransactionRepository repository;

    // Injeção de dependência via construtor
    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    // Executa o salvamento de uma nova transação no banco de dados
    public Transaction execute(String description, BigDecimal amount, String category) {
        Transaction transaction = new Transaction(description, amount, category);
        return repository.save(transaction);
    }
}