package br.pucpr.authserver

import br.pucpr.authserver.categorias.Categoria
import br.pucpr.authserver.categorias.CategoriaRepository
import br.pucpr.authserver.livros.Livro
import br.pucpr.authserver.livros.LivroRepository
import br.pucpr.authserver.roles.Role
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserRepository
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class Bootstrapper(
    val rolesRepository: RoleRepository,
    val userRepository: UserRepository,
    val categoriaRepository: CategoriaRepository,
    val livroRepository: LivroRepository
) : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        //Cria os papéis ADMIN e PREMIUM USER, se não existirem
        val adminRole =
            rolesRepository.findByName("ADMIN") ?: rolesRepository
                .save(Role(name = "ADMIN", description = "System Administrator"))
        rolesRepository.findByName("PREMIUM") ?: rolesRepository
            .save(Role(name = "PREMIUM", description = "Premium user"))

        //Cria um admin se não existir nenhum
        if (userRepository.findByRole("ADMIN").isEmpty()) {
            val admin = User(
                email = "admin@authserver.com",
                password = "admin",
                name = "Auth Server Administrator",
            )
            admin.roles.add(adminRole)
            userRepository.save(admin)
        }

        //Cria categorias e livros de exemplo
        if (categoriaRepository.count() == 0L) {
            val ficcao = categoriaRepository.save(
                Categoria(nome = "Ficção", descricao = "Livros de ficção em geral")
            )
            val tecnologia = categoriaRepository.save(
                Categoria(nome = "Tecnologia", descricao = "Programação e computação")
            )
            categoriaRepository.save(
                Categoria(nome = "Romance", descricao = "Romances clássicos e contemporâneos")
            )

            if (livroRepository.count() == 0L) {
                livroRepository.save(
                    Livro(titulo = "1984", autor = "George Orwell", ano = 1949, categoria = ficcao)
                )
                livroRepository.save(
                    Livro(titulo = "Clean Code", autor = "Robert C. Martin", ano = 2008, categoria = tecnologia)
                )
                livroRepository.save(
                    Livro(titulo = "Kotlin in Action", autor = "Dmitry Jemerov", ano = 2017, categoria = tecnologia)
                )
            }
        }
    }
}
