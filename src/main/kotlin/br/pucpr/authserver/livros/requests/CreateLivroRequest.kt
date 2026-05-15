package br.pucpr.authserver.livros.requests

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateLivroRequest(
    @NotBlank
    val titulo: String?,

    @NotBlank
    val autor: String?,

    @NotNull
    @Min(0)
    val ano: Int?,

    @NotNull
    val categoriaId: Long?
)
