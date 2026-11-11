package br.ifpb.edu.clai.model;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlunoCpfValidationTest {

    @Test
    void deveRejeitarCpfComNumerosRepetidos() {
        Aluno aluno = new Aluno();
        aluno.setCpf("11111111111");

        try (ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory()) {

            var erros = factory.getValidator()
                    .validateProperty(aluno, "cpf");

            assertFalse(erros.isEmpty());
        }
    }

    @Test
    void deveRejeitarCpfComDigitosInvalidos() {
        Aluno aluno = new Aluno();
        aluno.setCpf("12345678901");

        try (ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory()) {

            var erros = factory.getValidator()
                    .validateProperty(aluno, "cpf");

            assertFalse(erros.isEmpty());
        }
    }

    @Test
    void deveAceitarCpfValido() {
        Aluno aluno = new Aluno();
        aluno.setCpf("52998224725");

        try (ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory()) {

            var erros = factory.getValidator()
                    .validateProperty(aluno, "cpf");

            assertTrue(erros.isEmpty());
        }
    }
}