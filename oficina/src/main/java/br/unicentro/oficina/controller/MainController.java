package br.unicentro.oficina.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;

import java.io.IOException;
import java.io.UncheckedIOException;

/** Carrega as telas de Clientes, Atendimentos e Resumo dentro das abas. */
public class MainController {

    private static final String VIEW = "/br/unicentro/oficina/view/";

    @FXML private Tab abaClientes;
    @FXML private Tab abaAtendimentos;
    @FXML private Tab abaResumo;

    @FXML
    private void initialize() {
        try {
            abaClientes.setContent(carregar("ClienteView.fxml"));
            abaAtendimentos.setContent(carregar("AtendimentoView.fxml"));
            abaResumo.setContent(carregar("RelatorioView.fxml"));
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao carregar as telas.", e);
        }
        // A atualização das telas é feita pelo padrão Observer (ver pacote observer).
    }

    private Node carregar(String arquivo) throws IOException {
        return new FXMLLoader(getClass().getResource(VIEW + arquivo)).load();
    }
}
