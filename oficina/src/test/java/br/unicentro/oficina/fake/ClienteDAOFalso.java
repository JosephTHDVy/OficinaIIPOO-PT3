package br.unicentro.oficina.fake;

import br.unicentro.oficina.model.Cliente;
import br.unicentro.oficina.model.dao.ClienteDAO;

import java.util.ArrayList;
import java.util.List;

/** DAO de clientes em memória, usado só nos testes: guarda os clientes numa lista e dispensa o PostgreSQL. */
public class ClienteDAOFalso implements ClienteDAO {

    private final List<Cliente> clientes = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public void inserir(Cliente c) {
        c.setId(proximoId++);   // simula o SERIAL do banco
        clientes.add(c);
    }

    @Override
    public void atualizar(Cliente c) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId().equals(c.getId())) {
                clientes.set(i, c);
                return;
            }
        }
    }

    @Override
    public void excluir(Integer id) {
        clientes.removeIf(c -> c.getId().equals(id));
    }

    @Override
    public Cliente buscarPorId(Integer id) {
        return clientes.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes);
    }

    @Override
    public List<Cliente> buscarPorNome(String nome) {
        return clientes.stream().filter(c -> c.getNome().toLowerCase().contains(nome.toLowerCase())).toList();
    }

    @Override
    public Cliente buscarPorCpf(String cpf) {
        return clientes.stream().filter(c -> c.getCpf().equals(cpf)).findFirst().orElse(null);
    }
}
