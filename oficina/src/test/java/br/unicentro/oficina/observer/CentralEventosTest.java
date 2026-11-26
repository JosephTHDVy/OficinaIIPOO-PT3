package br.unicentro.oficina.observer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CentralEventosTest {

    // CentralEventos é um singleton: o teste precisa remover os ouvintes que registrou, para não afetar outros testes
    private final CentralEventos central = CentralEventos.getInstance();
    private final List<OuvinteDados> registrados = new ArrayList<>();

    private OuvinteDados registrar(OuvinteDados ouvinte) {
        central.adicionar(ouvinte);
        registrados.add(ouvinte);
        return ouvinte;
    }

    @AfterEach
    void limpar() {
        registrados.forEach(central::remover);
    }

    @Test
    void deveNotificarSomenteOuvintesRegistradosIsolandoFalhas() {
        // arrange: A e B guardam o que recebem; "falho" lança exceção; ele está entre os dois, de propósito
        List<TipoDado> recebidosA = new ArrayList<>();
        List<TipoDado> recebidosB = new ArrayList<>();
        OuvinteDados a = registrar(recebidosA::add);
        registrar(tipo -> {
            throw new IllegalStateException("falha simulada");
        });
        OuvinteDados b = registrar(recebidosB::add);

        // entradas inválidas no registro: o mesmo ouvinte duas vezes e null não devem causar erro nem duplicar avisos
        central.adicionar(a);
        assertDoesNotThrow(() -> central.adicionar(null), "Registrar um ouvinte null deve ser ignorado, sem lançar exceção");

        // act 1: notifica um evento
        assertDoesNotThrow(() -> central.notificar(TipoDado.ATENDIMENTO),
                "A falha de um ouvinte não pode interromper a notificação");

        // assert 1: A (registrado duas vezes) é avisado uma única vez; B, que vem depois do ouvinte com falha, também é avisado
        assertEquals(List.of(TipoDado.ATENDIMENTO), recebidosA,
                "O ouvinte registrado duas vezes deve ser avisado uma única vez, mas A recebeu " + recebidosA);
        assertEquals(List.of(TipoDado.ATENDIMENTO), recebidosB,
                "B está depois do ouvinte com falha e mesmo assim deve ser avisado, mas recebeu " + recebidosB);

        // act 2: remove B e notifica outro evento
        central.remover(b);
        central.notificar(TipoDado.CLIENTE);

        // assert 2: B removido não recebe mais; A continua recebendo
        assertEquals(List.of(TipoDado.ATENDIMENTO), recebidosB,
                "Um ouvinte removido não deve receber novos avisos, mas B recebeu " + recebidosB);
        assertEquals(List.of(TipoDado.ATENDIMENTO, TipoDado.CLIENTE), recebidosA,
                "Os ouvintes que continuam registrados devem receber o novo evento, mas A recebeu " + recebidosA);
    }
}
