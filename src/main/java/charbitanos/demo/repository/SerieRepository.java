package charbitanos.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import charbitanos.demo.models.Definitivo.Serie;

public interface SerieRepository extends JpaRepository<Serie,Long> {
        
}
