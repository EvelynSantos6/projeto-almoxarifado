package com.projeto.almoxarifado.enums;

public enum TipoItem {
    CONSUMIVEL, // Ex: Parafusos, Lixas, Colas
    FERRAMENTA, // Ex: Martelo, Alicate (precisa devolver no fim do dia)
    RESTRITO    // Ex: Arduino, Motores (precisa de autorização)
}
