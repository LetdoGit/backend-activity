package br.pucpr.authserver.categorias.requests

import br.pucpr.authserver.categorias.Categoria
import jakarta.validation.constraints.NotBlank

data class CreateCategoriaRequest(
    @NotBlank
    val nome: String?,

    val descricao: String? = ""
) {
    fun toCategoria() = Categoria(nome = nome!!, descricao = descricao ?: "")
}
