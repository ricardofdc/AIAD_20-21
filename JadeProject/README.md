# Tema Projeto 1 AIAD



## Cenário de aplicação, agentes e suas interações, protocolos, objetivos individuals/globais, estratégias, …
- Gerir a biblioteca da FEUP;
- Agente cliente que faz um pedido ao agente receção (pedido de livro ou marcação de mesa);
- Agente receção envia o pedido do cliente aos agentes dos vários pisos. No caso de ser procura de mesa, recebe a informação do grau de satisfação dos agentes dos pisos e toma uma decisão. No caso de ser um pedido de livro, apresenta ao agente cliente a lista de livros disponíveis;
- Agentes de piso recebem pedidos da receção. Devolvem o seu grau de satisfação ou a lista de livros relacionadas com o tema;
- Cliente no fim pode decidir se aceita a decisão tomada.

## Variáveis independentes: que variações se podem introduzir em diferentes execuções do SMA?
 - Número de mesas total por piso;
 - Número de (potenciais) clientes por piso por dia;
 - Número de pisos;
 - Percentagem de barulho dos clientes;
 - Tolerância do agente do piso ao barulho e à ocupação da sala;
 - Livros (quantidade, nome, tema, curso, etc).

## Variáveis dependentes: o que se pretende avaliar com execuções do SMA?
 - Grau de satisfação de cada piso ao final do dia;
 - Grau de satisfação dos clientes na requisição de livros;
 - Grau de interesse da biblioteca em requisitar o livro ao cliente (se houver poucos livros o interesse diminui; se o cliente não for do curso relacionado com o livro o interesse diminui);
 - Quantos clientes foram expulsos;
 - Ocupação média da biblioteca.

## AGENTES:
### Clientes:
* Tipo de pedido: bool --> true: livro; false:sala
* Curso: string
* Livro: string
* Barulho: int (0 a 5)

### Secretario

### Andar:
* Número de mesas livres: int
* Curso: string
* Tolerância ao Barulho: int (0 a 5)
* Lista de livros

#### Livros
* Título
* Curso
