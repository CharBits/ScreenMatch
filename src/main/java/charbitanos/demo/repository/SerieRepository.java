package charbitanos.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import charbitanos.demo.models.Definitivo.Serie;

public interface SerieRepository extends JpaRepository<Serie,Long> {
   
    Optional<Serie> findFirstByTituloStartingWithIgnoreCase(String nomeSerie);

	List<Serie> findByAtoresContainingIgnoreCase(String nome);

    List<Serie> findTop5ByOrderByNotaDesc();
}
