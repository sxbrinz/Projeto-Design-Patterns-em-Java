package dio.budgeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Anotação principal que habilita a autoconfiguração do Spring Boot
@SpringBootApplication
public class Application {

    // Ponto de entrada que inicializa o servidor web e o contexto da aplicação
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}