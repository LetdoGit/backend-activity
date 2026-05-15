package br.pucpr.authserver.livros.responses

import br.pucpr.authserver.categorias.responses.CategoriaResponse
import br.pucpr.authserver.livros.Livro

data class LivroResponse(
    val id: Long,
    val titulo: String,
    val autor: String,
    val ano: Int,
    val categoria: CategoriaResponse,
    val favoritos: Int
) {
    constructor(livro: Livro) : this(
        id = livro.id!!,
        titulo = livro.titulo,
        autor = livro.autor,
        ano = livro.ano,
        categoria = CategoriaResponse(livro.categoria),
        favoritos = livro.usuariosFavoritos.size
    )
}
