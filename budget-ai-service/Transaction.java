package dio.budgeting.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// Define que esta classe é uma entidade gerenciada pelo banco de dados JPA
@Entity
@Table(name = "tb_transactions")
public class Transaction {

    @Id
    private UUID id;            // Identificador único universal da transação
    private String description; // Descrição (ex: "Compra de supermercado")
    private BigDecimal amount;   // Valor financeiro da transação
    private String category;    // Categoria (ex: "Alimentação")
    private LocalDate date;     // Data de registro

    // Construtor padrão exigido pelo JPA
    public Transaction() {
        this.id = UUID.randomUUID();
        this.date = LocalDate.now();
    }

    // Construtor utilitário para criação de novos registros
    public Transaction(String description, BigDecimal amount, String category) {
        this();
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    // Métodos Getters para acesso aos atributos do domínio
    public UUID getId() { return id; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public String getCategory() { return category; }
    public LocalDate getDate() { return date; }
}