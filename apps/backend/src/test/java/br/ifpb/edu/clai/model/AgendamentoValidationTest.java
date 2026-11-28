package br.ifpb.edu.clai.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class AgendamentoValidationTest {

    private static ValidatorFactory fabrica;
    private static Validator validador;

    @BeforeAll
    static void iniciar() {
        fabrica = Validation.buildDefaultValidatorFactory();
        validador = fabrica.getValidator();
    }

    @AfterAll
    static void encerrar() {
        fabrica.close();
    }

    private Agendamento agendamentoValido() {
        Agendamento agendamento = new Agendamento();
        agendamento.setAlunoId("a1");
        agendamento.setProfissionalResponsavel("Carlos Lima");
        agendamento.setData(LocalDate.now().plusDays(1));
        agendamento.setHoraInicio(LocalTime.of(8, 0));
        agendamento.setHoraFim(LocalTime.of(9, 0));
        agendamento.setModalidade("PRESENCIAL");
        agendamento.setCor("#3498DB");
        return agendamento;
    }

    private boolean temErro(
            Set<ConstraintViolation<Agendamento>> erros,
            String mensagem) {
        return erros.stream()
                .anyMatch(e -> e.getMessage().equals(mensagem));
    }

    @Test
    void agendamentoValidoNaoDeveTerErros() {
        assertTrue(validador.validate(agendamentoValido()).isEmpty());
    }

    @Test
    void deveExigirAlunoVinculado() {
        Agendamento agendamento = agendamentoValido();
        agendamento.setAlunoId("");

        assertTrue(temErro(validador.validate(agendamento),
                "O aluno vinculado é obrigatório"));
    }

    @Test
    void deveExigirProfissionalResponsavel() {
        Agendamento agendamento = agendamentoValido();
        agendamento.setProfissionalResponsavel("");

        assertTrue(temErro(validador.validate(agendamento),
                "O profissional responsável é obrigatório"));
    }

    @Test
    void deveExigirData() {
        Agendamento agendamento = agendamentoValido();
        agendamento.setData(null);

        assertTrue(temErro(validador.validate(agendamento),
                "A data do agendamento é obrigatória"));
    }

    @Test
    void deveExigirHorarioInicial() {
        Agendamento agendamento = agendamentoValido();
        agendamento.setHoraInicio(null);

        assertTrue(temErro(validador.validate(agendamento),
                "O horário inicial é obrigatório"));
    }

    @Test
    void deveExigirHorarioFinal() {
        Agendamento agendamento = agendamentoValido();
        agendamento.setHoraFim(null);

        assertTrue(temErro(validador.validate(agendamento),
                "O horário final é obrigatório"));
    }

    @Test
    void deveExigirModalidade() {
        Agendamento agendamento = agendamentoValido();
        agendamento.setModalidade("");

        assertTrue(temErro(validador.validate(agendamento),
                "A modalidade é obrigatória"));
    }

    @Test
    void deveExigirCor() {
        Agendamento agendamento = agendamentoValido();
        agendamento.setCor("");

        assertTrue(temErro(validador.validate(agendamento),
                "A cor do agendamento é obrigatória"));
    }

    @Test
    void deveRejeitarCorForaDoFormatoHexadecimal() {
        Agendamento agendamento = agendamentoValido();
        agendamento.setCor("azul");

        assertTrue(temErro(validador.validate(agendamento),
                "A cor deve estar no formato hexadecimal, como #3498DB"));
    }

    @Test
    void deveAceitarCorHexadecimalPersonalizada() {
        Agendamento agendamento = agendamentoValido();
        agendamento.setCor("#A1b2C3");

        assertTrue(validador.validate(agendamento).isEmpty());
    }

    @Test
    void deveIniciarComStatusAgendado() {
        assertEquals(StatusAgendamento.AGENDADO,
                agendamentoValido().getStatus());
    }
}
