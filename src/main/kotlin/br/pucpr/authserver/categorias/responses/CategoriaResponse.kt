package br.pucpr.authserver.categorias.responses

import br.pucpr.authserver.categorias.Categoria

data class CategoriaResponse(
    val id: Long,
    val nome: String,
    val descricao: String
) {
    constructor(categoria: Categoria) : this(
        id = categoria.id!!,
        nome = categoria.nome,
        descricao = categoria.descricao
    )
}
