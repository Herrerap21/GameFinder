### **GameFinder**



Aplicativo Android desenvolvido para a disciplina de Programação para Dispositivos Móveis.



O GameFinder permite pesquisar informações sobre jogos gratuitos (Free-to-Play) utilizando a API pública FreeToGame. O usuário pode pesquisar um jogo pelo nome e visualizar informações como capa, desenvolvedora, publicadora, gênero, plataforma, data de lançamento e descrição.



##### **Funcionalidades**



\* Pesquisa de jogos pelo nome

\* Exibição da capa do jogo

\* Exibição da desenvolvedora

\* Exibição da publicadora

\* Exibição do gênero

\* Exibição da plataforma

\* Exibição da data de lançamento

\* Exibição da descrição do jogo

\* Tratamento de erros para jogos não encontrados



##### **API Utilizada**



FreeToGame API



https://www.freetogame.com/api-doc



##### **Permissão Android Utilizada**



POST\_NOTIFICATIONS



A permissão POST\_NOTIFICATIONS é utilizada para permitir que o aplicativo envie notificações ao usuário.



Quando um jogo é encontrado durante a pesquisa, o aplicativo pode exibir uma notificação informando o nome do jogo localizado.



Fluxo da Permissão

1- O usuário pode ativar as notificações através do botão "Ativar notificações".

2- O sistema Android solicita a permissão em tempo de execução.

3- Caso o usuário conceda a permissão, o aplicativo passa a enviar notificações normalmente.

4- Caso o usuário negue a permissão, o aplicativo continua funcionando normalmente, porém sem enviar notificações.



##### **Tecnologias Utilizadas**



\* Kotlin

\* Android Studio

\* XML

\* OkHttp

\* Glide

\* FreeToGame API



##### **Estrutura do Projeto**



\* MainActivity.kt → Lógica principal da aplicação

\* activity\_main.xml → Interface gráfica

\* AndroidManifest.xml → Configurações e permissões

\* Drawable → Recursos visuais e logo



##### **Como Executar**



1\. Clonar o repositório.

2\. Abrir o projeto no Android Studio.

3\. Sincronizar as dependências do Gradle.

4\. Executar em um dispositivo Android ou emulador.

5\. Pesquisar um jogo disponível na API.



##### **Exemplos de Pesquisa**



\* Overwatch

\* Warframe

\* Valorant

\* Dauntless

\* Paladins

\* Fortnite



Atenção com os nomes dos jogos, eles precisam estar corretamente escritos, como por exemplo de pesquisas que dariam erro: Counter Strike 2 ou CS2, esses dariam erro porque o nome que está correto na API é Counter-Strike 2



##### **Autores**



Pietro Herrera Vasconcellos de Almeida – Matrícula: 2561153



Roger Oliveira de Souza – Matrícula: 2584168



João Miguel Machado Muniz Ferreira – Matrícula: 2565614



