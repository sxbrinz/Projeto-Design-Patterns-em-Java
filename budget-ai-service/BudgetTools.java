package dio.budgeting.infrastructure.ai;

import dio.budgeting.application.CreateTransactionUseCase;
import dio.budgeting.application.GetExpenseByCategoryUseCase;
import dio.budgeting.domain.Transaction;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

// Componente do Spring que mapeia os métodos que a IA pode decidir executar (Tool Calling)
@Component
public class BudgetTools {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetExpenseByCategoryUseCase getExpenseByCategoryUseCase;

    public BudgetTools(CreateTransactionUseCase createTransactionUseCase, 
                       GetExpenseByCategoryUseCase getExpenseByCategoryUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.getExpenseByCategoryUseCase = getExpenseByCategoryUseCase;
    }

    // Marca o método como uma ferramenta legível pela IA.
    // A descrição orienta o LLM sobre quando utilizar esta função.
    @Tool(description = "Registra uma nova transação financeira no sistema de orçamento")
    public String createTransaction(String description, BigDecimal amount, String category) {
        Transaction transaction = createTransactionUseCase.execute(description, amount, category);
        return String.format("Transação '%s' no valor de R$ %.2f cadastrada com sucesso!", 
                transaction.getDescription(), transaction.getAmount());
    }

    // Ferramenta que a IA aciona quando a pessoa pergunta sobre gastos em uma categoria
    @Tool(description = "Consulta o total de gastos em uma categoria especificada pelo usuário")
    public String getExpenseByCategory(String category) {
        BigDecimal total = getExpenseByCategoryUseCase.execute(category);
        return String.format("O total gasto na categoria '%s' até agora é de R$ %.2f.", category, total);
    }
}