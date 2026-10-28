package br.ifpb.edu.clai.model;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlunoTelefoneValidationTest {

    @Test
    void deveAceitarTelefoneFixoComDDD() {
        assertFalse(telefonePossuiErros("8332345678"));
    }

    @Test
    void deveAceitarCelularComDDD() {
        assertFalse(telefonePossuiErros("83999998888"));
    }

    @Test
    void deveRejeitarTelefoneCurto() {
        assertTrue(telefonePossuiErros("123"));
    }

    @Test
    void deveRejeitarTelefoneComLetras() {
        assertTrue(telefonePossuiErros("abcdefghij"));
    }

    @Test
    void deveValidarTelefonesDoAlunoEDoResponsavel() {
        Aluno aluno = new Aluno();

        Aluno.Contato contatoAluno = new Aluno.Contato();
        contatoAluno.setTelefone("123");
        aluno.setContatoAluno(contatoAluno);

        Aluno.Contato contatoResponsavel = new Aluno.Contato();
        contatoResponsavel.setTelefone("abc");

        Aluno.DadosResponsavel responsavel =
                new Aluno.DadosResponsavel();
        responsavel.setContatoDoResponsavel(contatoResponsavel);
        aluno.setDadosResponsavel(responsavel);

        try (ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory()) {

            var camposComErro = factory.getValidator()
                    .validate(aluno)
                    .stream()
                    .map(erro -> erro.getPropertyPath().toString())
                    .toList();

            assertTrue(camposComErro.contains("contatoAluno.telefone"));
            assertTrue(camposComErro.contains(
                    "dadosResponsavel.contatoDoResponsavel.telefone"));
        }
    }

    private boolean telefonePossuiErros(String numero) {
        Aluno.Contato contato = new Aluno.Contato();
        contato.setTelefone(numero);

        try (ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory()) {

            return !factory.getValidator()
                    .validateProperty(contato, "telefone")
                    .isEmpty();
        }
    }
}