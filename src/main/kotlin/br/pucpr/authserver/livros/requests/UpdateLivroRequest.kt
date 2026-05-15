package br.pucpr.authserver.livros.requests

import jakarta.validation.constraints.Min

data class UpdateLivroRequest(
    val titulo: String?,
    val autor: String?,

    @Min(0)
    val ano: Int?,

    val categoriaId: Long?
)
