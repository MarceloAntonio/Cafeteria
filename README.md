
## Organização do Projeto

O projeto está organizado da seguinte forma:

- `./bin`: diretório onde os arquivos compilados Java são armazenados e atualizados
- `./config`: diretório onde os arquivos de configuração (ex. senhas e usuários)
- `./docs`: arquivos de documentação do projeto
- `./lib`: diretório que contém as dependências (ex. bibliotecas)
- `./sql`: scripts de banco para criar e popular dados ao iniciar a base de dados
- `./src`: código-fonte do projeto Java

## Configuração do banco de dados

- Baixe o drive `postgressql.jar` em • https://jdbc.postgresql.org/download/ e baixando a versão mais recente
- Coloque o drive na biblioteca externa 
(Caso esteja pelo Intellij https://www.notion.so/Tutorial-de-botar-drive-14261d6482058042868cc735933ab077?pvs=4)
- Após inserir o drive entre em `DatabaseConfig` para ajustar as variaveis url(Endereço), user(Usuario), passoword(Senha) alterando para as respectivas informações do seu banco de dados
- Após os passos acima teste se o banco de dados está funcionando rodando o arquivo `TestDatabaseConfig`