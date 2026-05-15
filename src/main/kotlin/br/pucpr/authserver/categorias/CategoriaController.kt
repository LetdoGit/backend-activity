package br.pucpr.authserver.categorias

import br.pucpr.authserver.categorias.requests.CreateCategoriaRequest
import br.pucpr.authserver.categorias.requests.UpdateCategoriaRequest
import br.pucpr.authserver.categorias.responses.CategoriaResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/categorias")
class CategoriaController(val service: CategoriaService) {

    @GetMapping
    fun list(): ResponseEntity<List<CategoriaResponse>> =
        service.findAll()
            .map { CategoriaResponse(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<CategoriaResponse> =
        service.findById(id)
            .let { CategoriaResponse(it) }
            .let { ResponseEntity.ok(it) }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "jwt-auth")
    @PostMapping
    fun insert(
        @Valid @RequestBody request: CreateCategoriaRequest
    ): ResponseEntity<CategoriaResponse> =
        service.insert(request.toCategoria())
            .let { CategoriaResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "jwt-auth")
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateCategoriaRequest
    ): ResponseEntity<CategoriaResponse> =
        service.update(id, request)
            .let { CategoriaResponse(it) }
            .let { ResponseEntity.ok(it) }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "jwt-auth")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
