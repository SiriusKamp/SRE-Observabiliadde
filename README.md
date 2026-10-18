# Guia de Replicação do Trabalho — Observabilidade e SRE

## 1. Iniciar a API

1. Abra o Docker Desktop, caso esteja desligado.
2. Execute a API localmente pela IDE ou pelo método utilizado durante o desenvolvimento.
3. Verifique se a aplicação iniciou corretamente e está respondendo na porta `8085`.

> **Atenção:** caso queira alterar a porta, atualize a configuração no arquivo `application.yml` ou `application.properties` e os apontamentos correspondentes na configuração do Prometheus e nos scripts do k6.

## 2. Validar o endpoint do Prometheus

1. Acesse o endpoint de métricas da aplicação:

   `http://localhost:8085/actuator/prometheus`

2. Verifique se as métricas estão sendo exibidas corretamente.
3. Caso o endpoint não responda, confira a configuração do Spring Boot e a integração com o Actuator.

## 3. Criar pedidos pela API

1. Utilize o método **POST** no endpoint:

   `http://localhost:8085/api/pedidos`

2. Envie um corpo JSON com as seguintes propriedades:

   - `itens`: lista de strings.
   - `descricao`: string com a descrição do pedido.
   - `valortotal`: número decimal (`double`).

3. Utilize o seguinte exemplo de JSON:

   ```json
   {
     "itens": ["item 1", "item 2"],
     "descricao": "Teste 1",
     "valortotal": 40.5
   }
   ```

4. Confirme que a API retorna uma resposta de sucesso e que o pedido foi registrado.

## 4. Iniciar o ambiente de observabilidade

1. Acesse a pasta `observabilidade` do projeto.
2. Inicie os serviços definidos no Docker Compose:

   ```powershell
   docker compose up -d
   ```

3. Acesse o Grafana:

   `http://localhost:3000`

4. Abra os dashboards desenvolvidos durante a aula.
5. Confira os painéis de métricas configurados no serviço da aplicação, como latência, taxa de erros e indicadores de SRE.
6. Verifique se o Prometheus está coletando as métricas da API em:

   `http://localhost:9090/targets`

   O alvo da aplicação deve estar com o estado `UP`.

## 5. Inserir pedidos em massa utilizando o k6

1. Certifique-se de que a API está funcionando e que o container do k6 está em execução.
2. Execute o teste de carga:

   ```powershell
   docker exec -it k6 k6 run /scripts/load-test.js
   ```

3. Aguarde a conclusão do teste.
4. Analise os resultados apresentados pelo k6, observando:
   - Quantidade de requisições realizadas.
   - Taxa de sucesso e de falhas.
   - Tempo médio de resposta.
   - Percentis de latência, como P90 e P95.
5. Acesse os dashboards do Grafana para observar o comportamento da aplicação durante a carga.

## 6. Configurar alertas no Grafana

1. Acesse o Grafana e abra a seção **Alerting → Alert rules**.
2. Crie uma nova regra de alerta.
3. Configure a consulta e a condição de disparo conforme o indicador que deseja monitorar, como taxa de erros, latência ou burning rate.
4. Configure a avaliação da regra para ocorrer a cada **1 minuto**.
5. Defina as condições para disparo e recuperação do alerta.
6. Configure o endereço de e-mail de destino na seção de **Contact points**.
7. Associe o contact point à regra de alerta ou à política de notificação correspondente.
8. Salve a configuração e verifique se a regra está sendo avaliada corretamente.

> **Importante:** configurar a avaliação a cada minuto não significa necessariamente que o alerta será enviado imediatamente. O tempo de disparo também depende da condição configurada, do período de avaliação e do tempo de espera antes da notificação.

## 7. Consultar os pedidos cadastrados

1. Utilize o método **GET** no endpoint:

   `http://localhost:8085/api/pedidos`

2. Verifique se os pedidos criados durante os testes estão sendo retornados corretamente.

## 8. Simular erros e alterações no estado dos pedidos

Execute o script abaixo no PowerShell para realizar chamadas aos endpoints de conclusão e cancelamento de pedidos.

O script realiza 300 tentativas de conclusão e 300 tentativas de cancelamento, utilizando IDs aleatórios entre `0` e `1500`, com uma pausa de 200 milissegundos entre as chamadas.

```powershell
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
```

### Resultados esperados

- Chamadas para concluir e cancelar pedidos.
- Possíveis respostas de erro para IDs inexistentes ou operações inválidas.
- Alterações nos indicadores de requisições e taxa de erros, conforme o comportamento da API.
- Possível aumento de latência e de burning rate, caso as condições de carga e as métricas configuradas provoquem esses efeitos.
- Disparo dos alertas do Grafana quando as condições configuradas forem atingidas.

> **Observação:** o script não garante, por si só, aumento de latência, burning rate ou disparo dos alertas. Esses resultados dependem da implementação da API, das métricas instrumentadas e das regras de alerta configuradas.

## 9. Validar os resultados finais

1. Consulte novamente os pedidos pelo endpoint GET.
2. Verifique os dashboards do Grafana.
3. Analise a taxa de erros e os percentis de latência no Prometheus.
4. Confira o estado das regras de alerta no Grafana.
5. Verifique se as notificações foram encaminhadas ao endereço de e-mail configurado.

Com essas etapas, é possível reproduzir o fluxo de criação de pedidos, teste de carga, monitoramento e simulação de falhas utilizado no trabalho de observabilidade e SRE.
