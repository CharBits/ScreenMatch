package charbitanos.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import charbitanos.demo.models.Categoria;
import charbitanos.demo.models.Definitivo.Serie;

public interface SerieRepository extends JpaRepository<Serie,Long> {
   
    Optional<Serie> findFirstByTituloStartingWithIgnoreCase(String nomeSerie);

	List<Serie> findByAtoresContainingIgnoreCase(String nome);

    List<Serie> findTop5ByOrderByNotaDesc();

	List<Serie> findByGenero(Categoria categoria);

	List<Serie> findByNotaGreaterThanEqual(double notaMinima);

	List<Serie> findByTotalTemporadasLessThanEqual(int temporadasMaxima);

	List<Serie> findByNotaGreaterThanEqualAndNotaGreaterThanEqual(double notaMinima, int temporadaMaxima);
    
    @Query("select s from Serie s WHERE s.totalTemporadas <= :temporadaMaxima AND s.nota >= :notaMinima")
    List<Serie> seriesPorTemporadaEAvaliacao(double notaMinima, int temporadaMaxima);
}
