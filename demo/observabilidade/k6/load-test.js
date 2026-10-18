
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 10 }, // Ramp-up
    { duration: '1m', target: 10 },  // Carga estável
    { duration: '30s', target: 0 },  // Ramp-down
  ],
};

export default function () {
  // Gerar uma quantidade aleatória de itens entre 1 e 10
  const quantidade = Math.floor(Math.random() * 10) + 1;

  // Gerar o array com itens aleatórios
  const itens = Array.from(
    { length: quantidade },
    () => `Item ${Math.floor(Math.random() * 10) + 1}`
  );

  // Criar o payload do pedido
  const criarPayload = JSON.stringify({
    itens: itens,
    descricao: 'Descricao gerada pelo k6',
    valorTotal: Math.floor(Math.random() * 100) + 1,
  });

  // Criar um pedido
  const criarRes = http.post(
    'http://host.docker.internal:8085/api/pedidos',
    criarPayload,
    {
      headers: {
        'Content-Type': 'application/json',
      },
    }
  );

  check(criarRes, {
    'criar status 201': (r) => r.status === 201,
  });

  // Listar pedidos
  const listarRes = http.get(
    'http://host.docker.internal:8085/api/pedidos'
  );

  check(listarRes, {
    'listar status 200': (r) => r.status === 200,
    'listar response < 500ms': (r) => r.timings.duration < 500,
  });

  // Pausa de 1 segundo entre as iterações
  sleep(1);
}
