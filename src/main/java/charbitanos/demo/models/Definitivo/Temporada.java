package charbitanos.demo.models.Definitivo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import charbitanos.demo.models.Dados.DadosTemporada;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "temporadas")
public class Temporada {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Serie serie;

    private Integer numero;
    private LocalDate lancamento;

    @OneToMany(mappedBy = "temporada")
    private List<Episodio> episodios = new ArrayList<>();

    private Integer totalEpisodio;

    private Double notaMaxEp;
    private Double notaMinEp;

    public Temporada(DadosTemporada temporada) {
        
        numero = temporada.numero();

        // Adicionando os episodios pertencentes
        for(var dadosEpisodio : temporada.episodios()) {
            var episodio = new Episodio(numero, dadosEpisodio);
            episodios.add(episodio);
        }

        lancamento = episodios.get(0).getLancamento();

        DoubleSummaryStatistics stats = episodios.stream()
            .mapToDouble(Episodio::getNota)
            .summaryStatistics();

        notaMaxEp = stats.getMax();
        notaMinEp = stats.getMin();

        totalEpisodio = episodios.size();
    }

    public Long getId() {
        return id;
    }
    
    public Serie getSerie() {
        return serie;
    }

    public Integer getNumero() {
        return numero;
    }

    public LocalDate getLancamento() {
        return lancamento;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public Integer getTotalEpisodio() {
        return totalEpisodio;
    }

    public Double getNotaMaxEp() {
        return notaMaxEp;
    }

    public Double getNotaMinEp() {
        return notaMinEp;
    }
}
