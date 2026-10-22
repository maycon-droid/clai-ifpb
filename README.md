## Decisões Arquiteturais

### 1. Arquitetura em Camadas (Layered Architecture)

Foi escolhida a Arquitetura em Camadas para organizar a aplicação em diferentes responsabilidades, facilitando a manutenção, a compreensão e a evolução do sistema. Essa abordagem permite separar as responsabilidades da aplicação, como a apresentação, as regras de negócio e o acesso aos dados.

A escolha também contribui para uma organização mais clara do código, tornando mais simples identificar onde cada funcionalidade deve ser implementada.

### 2. Organização de Pacotes por Funcionalidade (Package by Feature)

Foi adotada a organização de pacotes por funcionalidade (*Package by Feature*) para agrupar os componentes relacionados a cada funcionalidade do sistema. Dessa forma, classes como controllers, services e modelos podem ser organizadas de acordo com o recurso ao qual pertencem.

Essa abordagem facilita a localização dos arquivos, melhora a organização do projeto e favorece a manutenção e a evolução de cada funcionalidade.

### 3. Modelo de Frontend Acoplado

Foi escolhido o modelo de frontend acoplado, no qual a interface e o backend são desenvolvidos de forma integrada, em vez de serem mantidos como aplicações completamente independentes.

Essa decisão busca aproveitar as tecnologias e os conhecimentos já adquiridos pela equipe, reduzindo a necessidade de aprender novas ferramentas e tecnologias neste momento. Assim, o grupo pode concentrar seus esforços na implementação das funcionalidades e no cumprimento dos objetivos do projeto.

### 4. SGBD MongoDB

O MongoDB foi escolhido por solicitação do professor responsável pela disciplina de Banco de Dados, que definiu sua utilização no projeto.

Por ser um banco de dados NoSQL orientado a documentos, o MongoDB armazena os dados em documentos BSON, permitindo trabalhar com estruturas de dados flexíveis. Sua utilização também proporciona à equipe a oportunidade de aplicar os conceitos estudados na disciplina em um projeto prático.