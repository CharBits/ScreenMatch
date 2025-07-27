package charbitanos.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import charbitanos.demo.models.Categoria;
import charbitanos.demo.models.Definitivo.Episodio;
import charbitanos.demo.models.Definitivo.Serie;

public interface SerieRepository extends JpaRepository<Serie,Long> {
   
    Optional<Serie> findFirstByTituloStartingWithIgnoreCase(String nomeSerie);

	List<Serie> findByAtoresContainingIgnoreCase(String nome);

    List<Serie> findTop5ByOrderByNotaDesc();

	List<Serie> findByGenero(Categoria categoria);

	List<Serie> findByNotaGreaterThanEqual(double notaMinima);

	List<Serie> findByTotalTemporadasLessThanEqual(int temporadasMaxima);

	List<Serie> findByNotaGreaterThanEqualAndNotaGreaterThanEqual(double notaMinima, int temporadaMaxima);
    
    @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :temporadaMaxima AND s.nota >= :notaMinima")
    List<Serie> seriesPorTemporadaEAvaliacao(double notaMinima, int temporadaMaxima);
    
    @Query("SELECT e FROM Serie s JOIN s.temporadas t JOIN t.episodios e WHERE e.titulo ILIKE %:trechoEpisodio%")
	List<Episodio> episodiosPorTrecho(String trechoEpisodio);
    
    @Query("SELECT e FROM Serie s JOIN s.temporadas t JOIN t.episodios e WHERE s.titulo ILIKE :nomeSerie ORDER BY e.nota DESC LIMIT 5")
	List<Episodio> topEpisodiosPorSerie(String nomeSerie);
}
