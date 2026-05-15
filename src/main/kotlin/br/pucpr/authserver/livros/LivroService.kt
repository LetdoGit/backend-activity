package br.pucpr.authserver.livros

import br.pucpr.authserver.categorias.CategoriaRepository
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.exceptions.BadRequestException
import br.pucpr.authserver.livros.requests.CreateLivroRequest
import br.pucpr.authserver.livros.requests.UpdateLivroRequest
import br.pucpr.authserver.users.SortDir
import br.pucpr.authserver.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class LivroService(
    val repository: LivroRepository,
    val categoriaRepository: CategoriaRepository,
    val userRepository: UserRepository
) {
    fun findById(id: Long): Livro =
        repository.findByIdOrNull(id) ?: throw NotFoundException(id)

    fun search(
        categoriaId: Long?,
        autor: String?,
        anoMin: Int?,
        anoMax: Int?,
        sortBy: String?,
        sortDir: String?
    ): List<Livro> {
        if (anoMin != null && anoMax != null && anoMin > anoMax) {
            throw BadRequestException("anoMin não pode ser maior que anoMax")
        }
        val campo = when ((sortBy ?: "titulo").lowercase()) {
            "titulo" -> "titulo"
            "ano" -> "ano"
            "autor" -> "autor"
            else -> throw BadRequestException("sortBy inválido. Use: titulo, ano ou autor")
        }
        val dir = SortDir.find(sortDir ?: "ASC")
        val sort = if (dir == SortDir.ASC) Sort.by(campo).ascending()
                   else Sort.by(campo).descending()
        return repository.search(categoriaId, autor, anoMin, anoMax, sort)
    }

    fun insert(request: CreateLivroRequest): Livro {
        val categoria = categoriaRepository.findByIdOrNull(request.categoriaId!!)
            ?: throw BadRequestException("Categoria ${request.categoriaId} não encontrada")
        val livro = Livro(
            titulo = request.titulo!!,
            autor = request.autor!!,
            ano = request.ano!!,
            categoria = categoria
        )
        val saved = repository.save(livro)
        log.info("Livro ${saved.id} ('${saved.titulo}') criado na categoria ${categoria.id}")
        return saved
    }

    fun update(id: Long, request: UpdateLivroRequest): Livro {
        val livro = findById(id)
        request.titulo?.let { livro.titulo = it }
        request.autor?.let { livro.autor = it }
        request.ano?.let { livro.ano = it }
        request.categoriaId?.let { novaCategoriaId ->
            val novaCategoria = categoriaRepository.findByIdOrNull(novaCategoriaId)
                ?: throw BadRequestException("Categoria $novaCategoriaId não encontrada")
            livro.categoria = novaCategoria
        }
        val saved = repository.save(livro)
        log.info("Livro $id atualizado")
        return saved
    }

    fun delete(id: Long) {
        val livro = findById(id)
        repository.delete(livro)
        log.info("Livro $id ('${livro.titulo}') removido")
    }

    fun adicionarFavorito(livroId: Long, userId: Long): Boolean {
        val livro = findById(livroId)
        val user = userRepository.findByIdOrNull(userId)
            ?: throw NotFoundException("Usuário $userId não encontrado")
        if (livro.usuariosFavoritos.any { it.id == userId }) return false
        livro.usuariosFavoritos.add(user)
        repository.save(livro)
        log.info("Usuário $userId adicionou o livro $livroId aos favoritos")
        return true
    }

    fun removerFavorito(livroId: Long, userId: Long): Boolean {
        val livro = findById(livroId)
        val user = livro.usuariosFavoritos.find { it.id == userId } ?: return false
        livro.usuariosFavoritos.remove(user)
        repository.save(livro)
        log.info("Usuário $userId removeu o livro $livroId dos favoritos")
        return true
    }

    companion object {
        val log = LoggerFactory.getLogger(LivroService::class.java)
    }
}
