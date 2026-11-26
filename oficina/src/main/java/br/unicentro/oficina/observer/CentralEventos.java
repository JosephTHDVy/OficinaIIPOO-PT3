package br.unicentro.oficina.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Observer (GoF) - papel de SUBJECT: guarda os ouvintes e os avisa quando os dados mudam.
 * Há uma única instância compartilhada, porque cada Controller cria a sua própria Service.
 */
public final class CentralEventos {

    private static final CentralEventos INSTANCIA = new CentralEventos();

    private final List<OuvinteDados> ouvintes = new CopyOnWriteArrayList<>();

    private CentralEventos() { }

    public static CentralEventos getInstance() {
        return INSTANCIA;
    }

    public void adicionar(OuvinteDados ouvinte) {
        if (ouvinte != null && !ouvintes.contains(ouvinte)) {
            ouvintes.add(ouvinte);
        }
    }

    public void remover(OuvinteDados ouvinte) {
        ouvintes.remove(ouvinte);
    }

    public void notificar(TipoDado tipo) {
        System.out.println("[Observer] " + tipo + " alterado -> " + ouvintes.size() + " ouvinte(s) avisado(s)");
        for (OuvinteDados ouvinte : ouvintes) {
            try {
                ouvinte.dadosAlterados(tipo);
            } catch (RuntimeException e) {
                // Falha em uma tela não pode impedir as demais nem desfazer o que já foi salvo.
                System.err.println("Erro ao notificar " + ouvinte.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}
