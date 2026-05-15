package br.pucpr.authserver.livros

import br.pucpr.authserver.exception.ForbiddenException
import br.pucpr.authserver.livros.requests.CreateLivroRequest
import br.pucpr.authserver.livros.requests.UpdateLivroRequest
import br.pucpr.authserver.livros.responses.LivroResponse
import br.pucpr.authserver.security.UserToken
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/livros")
class LivroController(val service: LivroService) {

    @GetMapping
    fun search(
        @RequestParam categoriaId: Long? = null,
        @RequestParam autor: String? = null,
        @RequestParam anoMin: Int? = null,
        @RequestParam anoMax: Int? = null,
        @RequestParam sortBy: String? = null,
        @RequestParam sortDir: String? = null
    ): ResponseEntity<List<LivroResponse>> =
        service.search(categoriaId, autor, anoMin, anoMax, sortBy, sortDir)
            .map { LivroResponse(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<LivroResponse> =
        service.findById(id)
            .let { LivroResponse(it) }
            .let { ResponseEntity.ok(it) }

    @SecurityRequirement(name = "jwt-auth")
    @PostMapping
    fun insert(
        @Valid @RequestBody request: CreateLivroRequest
    ): ResponseEntity<LivroResponse> =
        service.insert(request)
            .let { LivroResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "jwt-auth")
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateLivroRequest
    ): ResponseEntity<LivroResponse> =
        service.update(id, request)
            .let { LivroResponse(it) }
            .let { ResponseEntity.ok(it) }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "jwt-auth")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    @SecurityRequirement(name = "jwt-auth")
    @PutMapping("/{id}/favoritos/{userId}")
    fun adicionarFavorito(
        @PathVariable id: Long,
        @PathVariable userId: Long,
        auth: Authentication
    ): ResponseEntity<Void> {
        checkSelfOrAdmin(auth, userId)
        return if (service.adicionarFavorito(id, userId)) ResponseEntity.ok().build()
        else ResponseEntity.noContent().build()
    }

    @SecurityRequirement(name = "jwt-auth")
    @DeleteMapping("/{id}/favoritos/{userId}")
    fun removerFavorito(
        @PathVariable id: Long,
        @PathVariable userId: Long,
        auth: Authentication
    ): ResponseEntity<Void> {
        checkSelfOrAdmin(auth, userId)
        return if (service.removerFavorito(id, userId)) ResponseEntity.ok().build()
        else ResponseEntity.noContent().build()
    }

    private fun checkSelfOrAdmin(auth: Authentication, userId: Long) {
        val token = auth.principal as? UserToken ?: throw ForbiddenException()
        if (token.id != userId && !token.isAdmin) {
            throw ForbiddenException("Operação permitida apenas para o próprio usuário ou ADMIN")
        }
    }
}
