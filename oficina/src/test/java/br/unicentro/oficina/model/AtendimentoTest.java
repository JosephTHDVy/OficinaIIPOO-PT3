package br.unicentro.oficina.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AtendimentoTest {

    private Atendimento atendimento;
    //roda antes dos testes para garantir um atendimento novo, sem sobra de dados de outro teste, dá independência

    @BeforeEach
    void preparar() {
        atendimento = new Atendimento();
        atendimento.setDataEntrada(LocalDateTime.of(2026, 11, 1, 9, 0));
    }

    @Test
    void deveSomarOsValoresDosServicos() {
        // arrange, adiciona 3 serviços ao atendimento
        atendimento.getServicos().add(new Servico(null, null, "Troca de óleo", new BigDecimal("120.50")));
        atendimento.getServicos().add(new Servico(null, null, "Alinhamento", new BigDecimal("79.50")));
        atendimento.getServicos().add(new Servico(null, null, "Revisão de freios", new BigDecimal("300.00")));

        // act, calcula o total
        BigDecimal total = atendimento.getValorTotal();

        // assert usa compareTo, pq o equals do bigdecimal considera a escala com .0 e não .00, no compare o 0 segnifica mesmo valor numérico
        assertEquals(0, new BigDecimal("500.00").compareTo(total),
                "O total de 120,50 + 79,50 + 300,00 deve ser 500,00, mas foi " + total);
    }
}
