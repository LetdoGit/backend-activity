package br.pucpr.authserver.categorias

import br.pucpr.authserver.categorias.requests.UpdateCategoriaRequest
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.exceptions.BadRequestException
import br.pucpr.authserver.livros.LivroRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CategoriaService(
    val repository: CategoriaRepository,
    val livroRepository: LivroRepository
) {
    fun findAll(): List<Categoria> =
        repository.findAll(Sort.by("nome").ascending())

    fun findById(id: Long): Categoria =
        repository.findByIdOrNull(id) ?: throw NotFoundException(id)

    fun insert(categoria: Categoria): Categoria {
        if (repository.findByNomeIgnoreCase(categoria.nome) != null) {
            throw BadRequestException("Categoria '${categoria.nome}' já existe")
        }
        val saved = repository.save(categoria)
        log.info("Categoria ${saved.id} ('${saved.nome}') criada")
        return saved
    }

    fun update(id: Long, request: UpdateCategoriaRequest): Categoria {
        val categoria = findById(id)
        val novoNome = request.nome!!
        if (!categoria.nome.equals(novoNome, ignoreCase = true)) {
            repository.findByNomeIgnoreCase(novoNome)?.let {
                throw BadRequestException("Categoria '$novoNome' já existe")
            }
        }
        categoria.nome = novoNome
        request.descricao?.let { categoria.descricao = it }
        val saved = repository.save(categoria)
        log.info("Categoria $id atualizada")
        return saved
    }

    fun delete(id: Long) {
        val categoria = findById(id)
        if (livroRepository.existsByCategoriaId(id)) {
            throw BadRequestException("Não é possível remover: a categoria possui livros vinculados")
        }
        repository.delete(categoria)
        log.info("Categoria $id ('${categoria.nome}') removida")
    }

    companion object {
        val log = LoggerFactory.getLogger(CategoriaService::class.java)
    }
}
