package charbitanos.demo.models.Dados;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpisodio

(

@JsonAlias("Episode") 
    Integer numero,

@JsonAlias("Title") 
    String titulo,

@JsonAlias("Released") 
    String lancamento,

@JsonAlias("imdbRating") 
    String nota
    
) 

{
    @Override
    public final String toString() {
        return String.format("Titulo: %s\nEpisodio: %d\nLancado: %s\nNota: %s",titulo,numero,lancamento,nota);
    }
}
