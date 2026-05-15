package br.pucpr.authserver.categorias.requests

import jakarta.validation.constraints.NotBlank

data class UpdateCategoriaRequest(
    @NotBlank
    val nome: String?,

    val descricao: String?
)
