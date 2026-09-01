# MesaEstudo 📚

**MesaEstudo** é um aplicativo Android desenvolvido para ajudar estudantes a gerenciarem seu tempo de estudo de forma eficiente, utilizando técnicas de foco (Timer) e acompanhamento detalhado de progresso.

## Funcionalidades

- **Timer de Estudo (Foco/Pausa):** Cronômetro personalizável para sessões de estudo intenso e intervalos de descanso.
- **Registro de Sessões:** Histórico detalhado de todas as sessões realizadas, com data, disciplina e tempo decorrido.
- **Estatísticas em Tempo Real:** Visualização de progresso diário, semanal e total, incluindo contagem de sessões e horas acumuladas.
- **Gestão de Disciplinas:** Organização dos estudos por matérias específicas.
- **Sincronização Automática:** As abas de Registro e Estatísticas são atualizadas instantaneamente ao encerrar uma sessão.

## Tecnologias e Ferramentas

- **Linguagem:** Java
- **UI:** XML Layouts com Material Design 3
- **Componentes Jetpack:**
    - `RecyclerView` para listagem de sessões.
    - `ViewPager2` e `TabLayout` para navegação entre Timer, Registro e Estatísticas.
    - `Fragment Result API` para comunicação entre componentes.
- **Persistência:** Banco de Dados MySQL (conectado via `mysql-connector-java`).
- **Segurança:** Criptografia de senhas com `jBCrypt`.

## Orientação de Compilação

### Pré-requisitos
- Android Studio Ladybug (ou superior).
- JDK 11.
- Banco de Dados MySQL configurado.

### Configuração do Ambiente
1. Clone o repositório.
2. Crie um arquivo `.env` na raiz do projeto com as credenciais do banco de dados:
   ```env
   URL=jdbc:mysql://SEU_HOST:PORTA/NOME_DO_BANCO
   USER=seu_usuario
   PASSWORD=sua_senha
   ```
3. O projeto utiliza o arquivo `.env` para preencher as `buildConfigField` no `build.gradle.kts`.

### Execução
1. Sincronize o projeto com o Gradle.
2. Selecione um dispositivo Android (Físico ou Emulador) com API 24+.
3. Clique em **Run**

## Como Usar

1. **Login/Cadastro:** Acesse o app com sua conta.
2. **Timer:** Escolha uma disciplina, descreva o que está estudando e inicie o timer.
3. **Pausa:** Utilize os intervalos para descansar e manter a produtividade.
4. **Encerrar:** Se precisar parar antes do tempo, clique em "Encerrar" para salvar o progresso parcial.
5. **Dashboard:** Acompanhe sua evolução nas abas de **Registro** e **Estatísticas**.

---
Desenvolvido como uma ferramenta de apoio ao estudante.
