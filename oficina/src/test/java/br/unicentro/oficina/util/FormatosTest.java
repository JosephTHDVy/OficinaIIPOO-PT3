package br.unicentro.oficina.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class FormatosTest {

    // Cada linha: texto digitado pelo usuário, valor esperado. NULO = o método deve devolver null (entrada inválida).
    @ParameterizedTest
    @CsvSource(value = {
            "250, 250",                // inteiro simples
            "250.50, 250.50",          // decimal com ponto
            "'250,50', 250.50",        // decimal com vírgula (padrão brasileiro)
            "'1.250,50', 1250.50",     // milhar com ponto + decimal com vírgula
            "1.500, 1500",             // ponto seguido de 3 dígitos, sem vírgula = separador de milhar
            "1.50, 1.50",              // ponto seguido de 2 dígitos = casa decimal, não milhar
            "'R$ 80,00', 80.00",       // com símbolo da moeda e espaço
            "'  120  ', 120",          // espaços nas pontas
            "abc, NULO",               // texto sem número
            "'1,2,3', NULO",           // número malformado
            "'', NULO",                // vazio
            "'   ', NULO",             // só espaços
            "NULO, NULO"               // null
    }, nullValues = "NULO")
    void deveInterpretarValoresMonetarios(String entrada, String esperado) {
        // act: chama o método que está sendo testado
        BigDecimal resultado = Formatos.lerValor(entrada);

        // assert: entradas inválidas devolvem null; as válidas devolvem o mesmo valor numérico
        if (esperado == null) {
            assertNull(resultado, "A entrada inválida \"" + entrada + "\" deveria resultar em null, mas resultou em " + resultado);
        } else {
            assertNotNull(resultado, "A entrada válida \"" + entrada + "\" não deveria resultar em null");
            // compareTo, e não equals, porque o equals do BigDecimal considera a escala (250 é diferente de 250.00)
            assertEquals(0, new BigDecimal(esperado).compareTo(resultado),
                    "A entrada \"" + entrada + "\" deveria valer " + esperado + ", mas valeu " + resultado);
        }
    }
}
