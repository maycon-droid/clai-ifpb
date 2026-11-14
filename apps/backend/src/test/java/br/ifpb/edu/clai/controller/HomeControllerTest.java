package br.ifpb.edu.clai.controller;

import br.ifpb.edu.clai.repository.AlunoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class HomeControllerTest {

    private AlunoRepository alunoRepository;
    private Model model;
    private HomeController controller;

    @BeforeEach
    void setUp() {
        alunoRepository = mock(AlunoRepository.class);
        model = mock(Model.class);
        controller = new HomeController(alunoRepository);
    }

    @Test
    void deveExibirDashboardComTotalDeAlunos() {
        when(alunoRepository.count()).thenReturn(5L);

        String view = controller.inicio(model);

        assertEquals("home", view);
        verify(model).addAttribute("totalAlunos", 5L);
        verify(alunoRepository).count();
    }

    @Test
    void deveExibirZeroQuandoNaoExistiremAlunos() {
        when(alunoRepository.count()).thenReturn(0L);

        String view = controller.inicio(model);

        assertEquals("home", view);
        verify(model).addAttribute("totalAlunos", 0L);
    }
}