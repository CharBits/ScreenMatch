package charbitanos.demo.models.Definitivo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import charbitanos.demo.models.Dados.DadosEpisodio;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "episodios")
public class Episodio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Temporada temporada;

    private Integer numeroTemporada;
    private Integer numero;
    private String titulo;
    private LocalDate lancamento;
    private Double nota;

    public Episodio() {}

    public Episodio(Integer numeroTemporada, DadosEpisodio episodio) {
        
        this.numeroTemporada = numeroTemporada;
        numero = episodio.numero();
        titulo = episodio.titulo();

        try{
            lancamento = LocalDate.parse(episodio.lancamento());
        } catch (DateTimeParseException e) {
            lancamento = LocalDate.parse("0000-01-01");
        }

        try {
            nota = Double.parseDouble(episodio.nota());
        } catch (NumberFormatException e) {
            nota = 0.0;
        }
    }

    public Long getId() {
        return id;
    }
     
    public Temporada getTemporada() {
        return temporada;
    }

    public Integer getNumero() {
        return numero;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getLancamento() {
        return lancamento;
    }

    public Double getNota() {
        return nota;
    }

    @Override
    public String toString() {
        return String.format("Temporada: %d\nTitulo: %s\nEpisodio: %d\nLancamento: %s\nNota: %s",numeroTemporada, titulo, numero, lancamento, nota);
    }

}
