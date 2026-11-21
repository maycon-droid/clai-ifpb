# SF-04 — Autenticação e controle de acesso

| Perfil | Permissões previstas |
| --- | --- |
| EDICAO | Consultar, cadastrar, editar e excluir registros nas operações disponíveis. |
| CONSULTA | Listar registros, pesquisar e visualizar detalhes. |

As permissões serão aplicadas no servidor quando o login for integrado.
Apenas contas ativas poderão autenticar.

O MongoDB armazena as contas na coleção `usuarios`.
O serviço deverá guardar em `senhaHash` apenas o hash BCrypt e usar
`PasswordEncoder.matches` para validar a senha no login.
O índice único de `login` deve ser criado no banco antes do cadastro das contas.

Nesta etapa, a configuração automática de segurança web e do usuário padrão
está temporariamente desativada para preservar o acesso às telas existentes.
Na integração do login, essas exclusões serão removidas e as permissões
serão aplicadas nas rotas.