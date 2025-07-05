package charbitanos.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import charbitanos.demo.models.Definitivo.Serie;

public interface SerieRepository extends JpaRepository<Serie,Long> {
    
    @Query("""
        SELECT s FROM Serie s
        LEFT JOIN FETCH s.temporadas t
        LEFT JOIN FETCH t.episodios
        WHERE s.id = :id
    """)
    Optional<Serie> findWithTemporadaAndEpisodio(Long id);

}
