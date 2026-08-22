// PADRÃO STRATEGY
// Criei esta interface para definir o contrato de envio das mensagens.
// A ideia é aplicar o princípio da responsabilidade única e permitir que novas
// formas de notificação sejam adicionadas no futuro sem alterar o código existente.

interface CanalNotificacao {
    void enviar(String mensagem, String destinatario);
}

// Para envios por E-mail
class NotificacaoEmail implements CanalNotificacao {
    @Override
    public void enviar(String mensagem, String destinatario) {
        System.out.println("[E-MAIL] Enviando para: " + destinatario + " | Conteúdo: " + mensagem);
    }
}

// Para envios por SMS
class NotificacaoSMS implements CanalNotificacao {
    @Override
    public void enviar(String mensagem, String destinatario) {
        System.out.println("[SMS] Enviando para: " + destinatario + " | Conteúdo: " + mensagem);
    }
}

// Para envios por WhatsApp
class NotificacaoWhatsApp implements CanalNotificacao {
    @Override
    public void enviar(String mensagem, String destinatario) {
        System.out.println("[WHATSAPP] Enviando para: " + destinatario + " | Conteúdo: " + mensagem);
    }
}

// PADRÃO SINGLETON
// Utilizado para garantir que exista apenas uma instância das configurações
// do sistema rodando na aplicação, evitando consumo desnecessário de memória.

class ConfiguracaoSistema {
    private static ConfiguracaoSistema instancia;
    private String nomeSistema;
    private String versao;

    // Construtor privado para impedir a criação de instâncias com 'new' fora desta classe
    private ConfiguracaoSistema() {
        this.nomeSistema = "NotificaHub - Gerenciador de Mensagens";
        this.versao = "v1.0.0";
    }

    // Ponto global de acesso à instância única (thread-safe simples / Lazy Initialization)
    public static ConfiguracaoSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    public String getNomeSistema() {
        return nomeSistema;
    }

    public String getVersao() {
        return versao;
    }
}

// PADRÃO FACADE
// Funciona como uma fachada simplificada para o cliente. 
// Em vez de lidar com a escolha do canal e com a busca das configurações manualmente,
// a Facade centraliza essas chamadas e expõe apenas o que é necessário.

class NotificadorFacade {
    private CanalNotificacao canal;

    // Permite alternar a estratégia de notificação dinamicamente em tempo de execução
    public void setCanal(CanalNotificacao canal) {
        this.canal = canal;
    }

    // Método principal que orquestra o uso do Singleton e da Estratégia selecionada
    public void enviarNotificacao(String mensagem, String destinatario) {
        // Recupera a instância única de configuração
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();
        
        System.out.println("==================================================");
        System.out.println("SISTEMA: " + config.getNomeSistema() + " (" + config.getVersao() + ")");
        System.out.println("==================================================");

        // Validação defensiva para evitar NullPointerException caso o canal não seja definido
        if (this.canal == null) {
            System.out.println("[ERRO] Nenhum canal de notificação foi configurado!");
            return;
        }

        // Executa a estratégia definida
        this.canal.enviar(mensagem, destinatario);
        System.out.println("Status: Notificação processada com sucesso.\n");
    }
}

// CLASSE PRINCIPAL
// Simulação de uso do sistema demonstrando a flexibilidade da arquitetura.

public class Main {
    public static void main(String[] args) {
        // Instancia a fachada do sistema
        NotificadorFacade notificador = new NotificadorFacade();

        // 1. Usando a estratégia de E-mail
        notificador.setCanal(new NotificacaoEmail());
        notificador.enviarNotificacao("Seu cadastro foi concluído!", "usuario@email.com");

        // 2. Alternando dinamicamente para a estratégia de SMS
        notificador.setCanal(new NotificacaoSMS());
        notificador.enviarNotificacao("Seu código de acesso é: 8492", "+5511999998888");

        // 3. Alternando dinamicamente para a estratégia de WhatsApp
        notificador.setCanal(new NotificacaoWhatsApp());
        notificador.enviarNotificacao("Sua encomenda saiu para entrega!", "+5511977776666");
    }
}