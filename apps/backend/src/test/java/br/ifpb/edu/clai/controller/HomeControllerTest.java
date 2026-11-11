package br.ifpb.edu.clai.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

class HomeControllerTest {

    @Test
    void deveRedirecionarRaizParaAlunos() throws Exception {

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new HomeController())
                .build();

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/alunos"));
    }
}