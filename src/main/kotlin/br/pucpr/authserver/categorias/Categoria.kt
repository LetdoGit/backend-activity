package br.pucpr.authserver.categorias

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Categoria(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    var nome: String,

    @Column(nullable = false)
    var descricao: String = "",
)
