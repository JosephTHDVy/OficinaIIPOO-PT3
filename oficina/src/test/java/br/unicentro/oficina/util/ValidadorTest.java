package br.unicentro.oficina.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidadorTest {

    @ParameterizedTest //roda esse método uma vez para cada linha do csvsource
    @CsvSource({ //cada linha tem o cpf testado e o resultado que é esperado
            "529.982.247-25, true",    // válido, com máscara
            "52998224725, true",       // válido, sem máscara
            "529.982.247-24, false",   // dígito verificador errado
            "111.111.111-11, false",   // todos os dígitos iguais
            "123.456, false",          // menos de 11 dígitos
            "abc, false",              // sem dígitos
            "'', false"                // vazio
    })
    void deveValidarCpf(String cpf, boolean esperado) {
        // act chama o médodo que está sendo testado
        boolean resultado = Validador.cpfValido(cpf);

        // assert compara o esperado com o obtido, a mensagem aparece se o teste falhar e diz qual cpf que causou o erro
        assertEquals(esperado, resultado, "O CPF \"" + cpf + "\" deveria ser considerado válido = " + esperado);
    }
}
