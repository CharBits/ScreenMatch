package charbitanos.demo.models.Definitivo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import charbitanos.demo.models.Categoria;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "series")
public class Serie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titulo;
    private LocalDate lancamento;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String sinopse;
    private Double nota;
    
    private Integer totalTemporadas;
    private Integer totalEpisodios;
    
    private String atores;
    
    private Double notaMaxEp;
    private Double notaMinEp;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL)
    private final List<Temporada> temporadas = new ArrayList<>();

    // Construtores
   
    public Serie() {}
    
    public Serie(String titulo, String lancamento, String genero, String sinopse, Double nota, Integer totalTemporadas, String atores) {

        this.titulo = titulo;

        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        this.lancamento = LocalDate.parse(lancamento, inputFormat);

        this.genero = Categoria.fromString(genero.split(",")[0].trim());
        this.sinopse = sinopse;
        this.nota = nota;
        this.totalTemporadas = totalTemporadas;

        this.atores = atores;
    }

    // Getters e Setters

    public Long getId() {
         return id; 
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getLancamento() {
        return lancamento;
    }

    public Categoria getGenero() {
        return genero;
    }

    public String getSinopse() {
        return sinopse;
    }

    public Double getNota() {
        return nota;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }
    
    public Integer getTotalEpisodios() {
        return totalEpisodios;
    }

    public Double getNotaMinEp() {
        return notaMinEp;
    }

    public Double getNotaMaxEp() {
        return notaMaxEp;
    }

    public List<Temporada> getTemporadas() {
        return temporadas;
    } 
    
    public String getAtores() {
        return atores;
    }

    // Metodos para Informacoes Adicionais

    public void calcularEstatisticas() {
        
        int total = 0;

        for(var temporada : temporadas)
            total += temporada.getTotalEpisodio();
        totalEpisodios = total;
        
        DoubleSummaryStatistics stats = temporadas.stream()
            .flatMap(t -> t.getEpisodios().stream())
            .mapToDouble(Episodio::getNota)
            .summaryStatistics();

        notaMaxEp = stats.getMax();
        notaMinEp = stats.getMin();
    }

    public String informarDetalhes() {

        var informacoesExtras = String.format("Total de Temporadas: %d\nTotal de Episodios: %d\nNota Minima de Episodio: %.1f\nNota Maxima de Episodio: %.1f", totalTemporadas, totalEpisodios, notaMinEp, notaMaxEp);
        
        String infoEps = temporadas.stream()
                                        .flatMap(t -> t.getEpisodios().stream())
                                        .map(e -> e.toString())
                                        .collect(Collectors.joining("\n\n"));

        return toString() +"\n"+ informacoesExtras +"\n\n\n"+ infoEps;
    } 

    @Override
    public final String toString() {
        return String.format("Titulo: %s\nLancamento: %s\nGenero: %s\nSinopse: %s\nNota: %.1f",titulo,lancamento,genero,sinopse,nota);
    }

    // Método auxiliar reutilizável
    public static boolean isValidDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
