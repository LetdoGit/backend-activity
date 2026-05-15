package br.pucpr.authserver.livros

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LivroRepository : JpaRepository<Livro, Long> {

    @Query(
        """
            select l from Livro l
            where (:categoriaId is null or l.categoria.id = :categoriaId)
              and (:autor is null or lower(l.autor) like lower(concat('%', :autor, '%')))
              and (:anoMin is null or l.ano >= :anoMin)
              and (:anoMax is null or l.ano <= :anoMax)
        """
    )
    fun search(
        categoriaId: Long?,
        autor: String?,
        anoMin: Int?,
        anoMax: Int?,
        sort: Sort
    ): List<Livro>

    fun existsByCategoriaId(categoriaId: Long): Boolean
}
