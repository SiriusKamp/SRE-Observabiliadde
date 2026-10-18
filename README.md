Passo a passo para replicação do trabalho

1. Rode a API localmente
2. Utilize o endpoint prometheus na porta 8085 ou caso queira mudar a porta necessário no yaml proprietes e nos apontamentos em observabilidade 
3. Com a api respondendo corretamente utilize o endpoint POST na api/pedidos com um json de objetos com propriedades de : 
itens : lista string 
descricao : string 
valortotal : double 

exemplo 
{
  "itens": ["item 1","item 2"],
  "descricao": "Teste 1",
  "valortotal" : 40.5
}

4. iniciar os dash do grafana conforme trabalhado em aula, as metricas estão descritas no service 
5. utilizar do k6 para insersão em massa de pedidos 
