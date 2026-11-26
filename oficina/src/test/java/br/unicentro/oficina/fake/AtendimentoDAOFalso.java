package br.unicentro.oficina.fake;

import br.unicentro.oficina.model.Atendimento;
import br.unicentro.oficina.model.dao.AtendimentoDAO;

import java.util.ArrayList;
import java.util.List;

/** dao em memória usado só nos testes: devolve os atendimentos que o teste colocar nele. */
public class AtendimentoDAOFalso implements AtendimentoDAO {

    private final List<Atendimento> atendimentos = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public void inserir(Atendimento a) {
        a.setId(proximoId++);
        atendimentos.add(a);
    }

    @Override
    public void atualizar(Atendimento a) {
        // não usado nos testes
    }

    @Override
    public void excluir(Integer id) {
        atendimentos.removeIf(a -> a.getId().equals(id));
    }

    @Override
    public Atendimento buscarPorId(Integer id) {
        return atendimentos.stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Atendimento> listarTodos() {
        return new ArrayList<>(atendimentos);
    }

    @Override
    public List<Atendimento> listarPorCliente(int clienteId) {
        return atendimentos.stream().filter(a -> a.getCliente().getId() == clienteId).toList();
    }

    @Override
    public List<Atendimento> buscarPorPlaca(String placa) {
        return atendimentos.stream().filter(a -> a.getVeiculoPlaca().contains(placa)).toList();
    }

    @Override
    public List<Atendimento> listarEmAndamento() {
        return atendimentos.stream().filter(Atendimento::isEmAndamento).toList();
    }
}
