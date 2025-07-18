package charbitanos.demo.models.Dados;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie

(

@JsonAlias("Title")
    String titulo,

@JsonAlias("Released") 
    String lancamento,

@JsonAlias("Genre") 
    String genero,
    
@JsonAlias("Plot") 
    String sinopse,

@JsonAlias("imdbRating") 
    Double nota,

@JsonAlias("totalSeasons") 
    Integer totalTemporada,

@JsonAlias("Actors")
    String atores
) 

{
    @Override
    public final String toString() {
        return String.format("Titulo: %s\nLancamento: %s\nGenero: %s\nSinopse: %s\nNota: %.1f",titulo,lancamento,genero,sinopse,nota);
    }
}
