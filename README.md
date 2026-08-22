
# Sistema de Notificações Multifuncional

Olá! Seja muito bem-vindo(a) ao repositório deste projeto.

Este projeto foi desenvolvido como parte do desafio prático sobre Padrões de Projeto (Design Patterns) em Java da plataforma Digital Innovation One (DIO).

---

## A Ideia 

Em projetos do dia a dia, é muito comum um sistema começar enviando apenas e-mails e, com o tempo, surgir a necessidade de integrar SMS, WhatsApp ou notificações push. 
Se o código não for bem planejado desde o início, essa evolução costuma gerar acoplamento excessivo e muitas condicionais repetidas.

Pensando nisso, desenvolvi este sistema de notificações para simular um cenário real de comunicação multicanal. O objetivo principal não é apenas simular o envio das mensagens, mas demonstrar como uma arquitetura bem estruturada em Java permite adicionar novas funcionalidades de forma limpa, segura e sem impactar o código que já está em produção.

## Arquitetura e Padrões de Projeto Aplicados

Para resolver problemas reais de escalabilidade e organização de código, apliquei três padrões essenciais:

### 1. Strategy (Padrão Comportamental)
* **O Problema:** Se amanhã o sistema precisar suportar novos canais (como Push Notification ou Telegram), o código não deveria exigir grandes alterações no fluxo principal.
* **A Solução:** Criei a interface `CanalNotificacao`. Cada meio de comunicação (`NotificacaoEmail`, `NotificacaoSMS`, `NotificacaoWhatsApp`) implementa essa interface com sua própria lógica. Dessa forma, a forma de envio pode ser trocada dinamicamente sem acoplamento rígido.

### 2. Singleton (Padrão de Criação)
* **O Problema:** O sistema precisa gerenciar configurações globais (como nome e versão do sistema) sem recriar objetos desnecessariamente na memória a cada notificação.
* **A Solução:** A classe `ConfiguracaoSistema` utiliza o padrão Singleton com construtor privado e inicialização preguiçosa (Lazy Initialization), garantindo que exista apenas uma única instância acessível globalmente durante toda a execução.

### 3. Facade (Padrão Estrutural)
* **O Problema:** Para quem usa a aplicação, gerenciar manualmente as instâncias de configuração e os canais de notificação tornaria o código cliente confuso e verboso.
* **A Solução:** A classe `NotificadorFacade` funciona como uma fachada simplificada. Ela encapsula a complexidade da integração entre as configurações e o canal escolhido, expondo apenas o método limpo `enviarNotificacao()`.


## Tecnologias e Ferramentas

* **Linguagem:** Java (JDK 17+)
* **Paradigma:** Programação Orientada a Objetos (POO)
* **Ambiente de Desenvolvimento:** Visual Studio Code / Ubuntu Linux
* **Controle de Versão:** Git e GitHub
