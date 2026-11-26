package br.unicentro.oficina.service;

import br.unicentro.oficina.exception.ServiceException;
import br.unicentro.oficina.fake.AtendimentoDAOFalso;
import br.unicentro.oficina.fake.ClienteDAOFalso;
import br.unicentro.oficina.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteServiceTest {

    private ClienteDAOFalso clienteDAO;
    private ClienteService service;

    @BeforeEach
    void preparar() {
        // DAOs em memória: o teste não depende do PostgreSQL. A Service recebe os DAOs pelo construtor (injeção de dependência).
        clienteDAO = new ClienteDAOFalso();
        service = new ClienteService(clienteDAO, new AtendimentoDAOFalso());
    }

    @Test
    void deveSalvarClienteNormalizadoERejeitarDadosInvalidos() throws ServiceException {
        // arrange: dados digitados "sujos" (espaços no nome, CPF sem máscara, e-mail com maiúsculas)
        Cliente maria = new Cliente(null, "  Maria Souza  ", "52998224725", "(42) 99999-0000", "MARIA@Teste.com");

        // act + assert, entrada válida: o cliente é gravado e os dados saem normalizados
        service.salvar(maria);
        assertNotNull(maria.getId(), "Um cliente válido deve ser gravado e receber um id");
        assertEquals("Maria Souza", maria.getNome(), "O nome deve ser salvo sem os espaços das pontas");
        assertEquals("529.982.247-25", maria.getCpf(), "O CPF deve ser formatado como 000.000.000-00");
        assertEquals("maria@teste.com", maria.getEmail(), "O e-mail deve ser salvo em minúsculas");
        assertEquals(1, clienteDAO.listarTodos().size(), "Depois de salvar um cliente válido, deve haver 1 cliente no DAO");

        // entrada inválida 1: CPF com dígitos verificadores errados
        ServiceException cpfInvalido = assertThrows(ServiceException.class,
                () -> service.salvar(new Cliente(null, "Fulano", "111.111.111-11", null, null)),
                "CPF inválido deve lançar ServiceException");
        assertEquals("CPF inválido.", cpfInvalido.getMessage(), "A mensagem deve dizer que o CPF é inválido");

        // entrada inválida 2: CPF já cadastrado para outro cliente
        ServiceException duplicado = assertThrows(ServiceException.class,
                () -> service.salvar(new Cliente(null, "Outra Pessoa", "529.982.247-25", null, null)),
                "CPF já cadastrado deve lançar ServiceException");
        assertTrue(duplicado.getMessage().contains("Já existe"),
                "A mensagem deve avisar que o CPF já existe, mas foi: " + duplicado.getMessage());

        // entrada inválida 3: nome em branco
        ServiceException semNome = assertThrows(ServiceException.class,
                () -> service.salvar(new Cliente(null, "   ", "390.533.447-05", null, null)),
                "Nome em branco deve lançar ServiceException");
        assertEquals("Informe o nome do cliente.", semNome.getMessage(), "A mensagem deve pedir o nome do cliente");

        // nenhuma das tentativas inválidas pode ter chegado ao DAO
        assertEquals(1, clienteDAO.listarTodos().size(), "Clientes inválidos ou duplicados não devem ser gravados");
    }
}
