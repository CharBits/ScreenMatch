package charbitanos.demo.models.Definitivo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonProcessingException;

import charbitanos.demo.models.Dados.DadosSerie;
import charbitanos.demo.models.Dados.DadosTemporada;
import charbitanos.demo.services.ApiConf;
import charbitanos.demo.services.ApiConsumer;
import jakarta.persistence.Entity;
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
    private String genero;
    private String sinopse;
    private Double nota;

    private Double notaMaxEp;
    private Double notaMinEp;
    
    private Integer totalTemporadas;
    private Integer totalEpisodios;

    @OneToMany(mappedBy = "serie")
    private final List<Temporada> temporadas = new ArrayList<>();

    // Construtores
    
    public Serie() {}
    
    public Serie(DadosSerie serie) {
        
        titulo = serie.titulo();

        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        lancamento = LocalDate.parse(serie.lancamento(), inputFormat);

        genero = serie.genero();
        sinopse = serie.sinopse();
        nota = serie.nota();
        totalTemporadas = serie.totalTemporada();

        addTemporadas();

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

    // Getters e Setters

    public Long getId() {
         return id; 
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getLancamento() {
        return lancamento;
    }

    public String getGenero() {
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
    
    public List<Temporada> getTemporadas() {
        return temporadas;
    }

    //Metodo para adicionar episodios para a lista da Serie
    private void addTemporadas() {

        for(int i = 0;i < totalTemporadas;i++) {

            String serie = titulo.replace(" ","+").toLowerCase();

            String json = ApiConsumer.request(ApiConf.URL+serie+"&Season="+(i + 1)+ApiConf.API_KEY);
            try {

                var dadosTemporada = ApiConf.MAPPER.readValue(json,DadosTemporada.class);
                var temporada = new Temporada(dadosTemporada);

                temporadas.add(temporada);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    //Metodo para mais informacoes para o Series!
    public String informarDetalhes() {


        String extra = String.format("\nTotal De Temporadas: %d\nTotal De Episodios: %d\nMenor Nota: %.1f\nMaior Nota: %.1f\n", totalTemporadas,totalEpisodios,notaMinEp,notaMaxEp);
        
        String infoEp = "";

         // Adicionar informacoes para a variavel infoEp
        for(var temporada : temporadas) {
            for(var episodio : temporada.getEpisodios()) {
                infoEp += "\n";
                infoEp += episodio.toString();
                infoEp += "\n";
                
            }   
        }

        return toString() + extra + infoEp;
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
