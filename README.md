Passo a passo para replicação do trabalho

1. Rode a API localmente
1.1 Abra o docker desktop caso esteja desligado
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
5. utilizar do k6 para insersão em massa de pedidos docker exec -it k6 k6 run /scripts/load-test.js
6. Criar alerta em alert rules que verifique de 1 em 1 minuto e configure seu email como destino
7. Utilize da rota get para ver todos os pedidos
8. utilize do codigo no powershell, ele irá simular erros cancelar pedidos e conclui-los, aumentando a latencia, burning rates e ligando os alertas
$baseUrl = "http://localhost:8085/api/pedidos"

# 1. Concluir 300 pedidos
for ($i = 1; $i -le 300; $i++) {
    $id = Get-Random -Minimum 0 -Maximum 1501
    $url = "$baseUrl/$id/concluir"

    try {
        Invoke-RestMethod -Uri $url -Method Put
        Write-Host "[$i/300] Pedido $id concluido" -ForegroundColor Green
    }
    catch {
        Write-Host "[$i/300] Erro ao concluir pedido $id : $($_.Exception.Message)" -ForegroundColor Red
    }

    Start-Sleep -Milliseconds 200
}

# 2. Cancelar 300 pedidos
for ($i = 1; $i -le 300; $i++) {
    $id = Get-Random -Minimum 0 -Maximum 1501
    $url = "$baseUrl/$id/cancelar"

    try {
        Invoke-RestMethod -Uri $url -Method Put
        Write-Host "[$i/300] Pedido $id cancelado" -ForegroundColor Green
    }
    catch {
        Write-Host "[$i/300] Erro ao cancelar pedido $id : $($_.Exception.Message)" -ForegroundColor Red
    }

    Start-Sleep -Milliseconds 200
}
