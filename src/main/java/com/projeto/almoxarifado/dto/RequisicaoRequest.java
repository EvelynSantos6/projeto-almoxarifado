package com.projeto.almoxarifado.dto;

import lombok.Data;

import java.util.List;

@Data
public class RequisicaoRequest {
    private List<ItemRequisicaoDTO> itens;

    @Data
    public static class ItemRequisicaoDTO {
        private Long itemId;
        private Integer quantidade;
    }
}
