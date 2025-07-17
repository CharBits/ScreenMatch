package charbitanos.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import charbitanos.demo.models.Definitivo.Serie;

public interface SerieRepository extends JpaRepository<Serie,Long> {
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);
}
