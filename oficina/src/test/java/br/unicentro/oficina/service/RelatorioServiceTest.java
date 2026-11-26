package br.unicentro.oficina.service;

import br.unicentro.oficina.exception.ServiceException;
import br.unicentro.oficina.fake.AtendimentoDAOFalso;
import br.unicentro.oficina.model.Atendimento;
import br.unicentro.oficina.model.Cliente;
import br.unicentro.oficina.model.Servico;
import br.unicentro.oficina.service.RelatorioService.Resumo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RelatorioServiceTest {

    private RelatorioService relatorioService;

    @BeforeEach
    void preparar() {
        // dao em memória: o teste não depende do PostgreSQL, guarda os atendimentos numa lista, mantendo o teste isolado
        AtendimentoDAOFalso dao = new AtendimentoDAOFalso();
        Cliente ana = new Cliente(1, "Ana", "529.982.247-25", null, null);
        Cliente bruno = new Cliente(2, "Bruno", "111.444.777-35", null, null);

        //3 atendimentos em datas diferentes
        dao.inserir(atendimento(ana, LocalDateTime.of(2026, 11, 1, 9, 0), "100.00", "50.00"));
        dao.inserir(atendimento(ana, LocalDateTime.of(2026, 11, 10, 9, 0), "200.00"));
        dao.inserir(atendimento(bruno, LocalDateTime.of(2026, 11, 20, 9, 0), "100.00"));
        //injeção de dependência o service recebe o DAO falso pelo construtor
        relatorioService = new RelatorioService(new AtendimentoService(dao, new FotoService()));
    }

    //método auxiliar de depência, monta um atendimento com os valores dos serviços pelo construtor
    private Atendimento atendimento(Cliente cliente, LocalDateTime entrada, String... valores) {
        Atendimento a = new Atendimento();
        a.setCliente(cliente);
        a.setVeiculoModelo("Gol");
        a.setVeiculoPlaca("ABC-1234");
        a.setDataEntrada(entrada);
        for (String valor : valores) {
            a.getServicos().add(new Servico(null, null, "Serviço", new BigDecimal(valor)));
        }
        return a;
    }

    @Test
    void deveGerarResumoDoPeriodoERejeitarPeriodoInvalido() throws ServiceException {
        // act: período de 10/11 a 20/11 (inclusivo) pega o 2º e o 3º atendimento e o do dia primeiro fica de fora
        Resumo resumo = relatorioService.gerar(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 20));

        // assert, entrada válida
        assertEquals(2, resumo.totalAtendimentos(), "O período deve conter 2 atendimentos");
        assertEquals(0, new BigDecimal("300.00").compareTo(resumo.faturamento()),
                "O faturamento deve ser 200,00 + 100,00 = 300,00, mas foi " + resumo.faturamento());
        assertEquals(0, new BigDecimal("150.00").compareTo(resumo.ticketMedio()),
                "O ticket médio deve ser 300,00 / 2 = 150,00, mas foi " + resumo.ticketMedio());

        // entrada inválida: data final anterior à inicial e assetThrows passa o código lançar a exceção esperada, se não lançar o teste falha 
        assertThrows(ServiceException.class,
                () -> relatorioService.gerar(LocalDate.of(2026, 11, 20), LocalDate.of(2026, 11, 10)),
                "Data final anterior à inicial deve lançar ServiceException");
    }
}
