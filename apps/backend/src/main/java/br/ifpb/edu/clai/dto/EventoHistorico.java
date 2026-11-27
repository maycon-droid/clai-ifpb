package br.ifpb.edu.clai.dto;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventoHistorico {
    private LocalDate data;
    private String tipo;
    private String titulo;
    private String descricao;
    private String origemId;
    private String responsavel;
}
