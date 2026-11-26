package br.unicentro.oficina.observer;

/** Observer (GoF): quem quer ser avisado quando os dados mudam. */
public interface OuvinteDados {

    void dadosAlterados(TipoDado tipo);
}
