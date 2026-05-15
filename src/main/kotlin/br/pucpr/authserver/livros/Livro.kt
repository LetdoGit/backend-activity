package br.pucpr.authserver.livros

import br.pucpr.authserver.categorias.Categoria
import br.pucpr.authserver.users.User
import jakarta.persistence.*

@Entity
class Livro(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var titulo: String,

    @Column(nullable = false)
    var autor: String,

    @Column(nullable = false)
    var ano: Int,

    @ManyToOne(optional = false)
    @JoinColumn(name = "idCategoria", nullable = false)
    var categoria: Categoria,

    @ManyToMany
    @JoinTable(
        name = "LivroFavorito",
        joinColumns = [JoinColumn(name = "idLivro")],
        inverseJoinColumns = [JoinColumn(name = "idUser")]
    )
    var usuariosFavoritos: MutableSet<User> = mutableSetOf()
)
