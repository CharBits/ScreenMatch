package charbitanos.demo.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import charbitanos.demo.models.Dados.DadosSerie;
import charbitanos.demo.models.Dados.DadosTemporada;
import charbitanos.demo.models.Definitivo.Serie;
import charbitanos.demo.models.Definitivo.Temporada;
import charbitanos.demo.repository.SerieRepository;
import jakarta.transaction.Transactional;

@Service
public class SerieService {

    @Autowired
    private SerieRepository repository;

    @Transactional
    public Serie createSerie(DadosSerie dadosSerie) {
        
        // Fatorando os dados
        var titulo = dadosSerie.titulo();
        var lancamento = dadosSerie.lancamento();
        var genero = dadosSerie.genero();
        var sinopse = dadosSerie.sinopse();
        var nota = dadosSerie.nota();
        var totalTemporadas = dadosSerie.totalTemporada();
        
        
        Serie serie = new Serie(titulo, lancamento, genero, sinopse, nota, totalTemporadas);
        repository.save(serie); // salva primeiro para gerar o ID
                                //
        adicionarTemporadas(serie); // agora pode adicionar temporadas
        serie.calcularEstatisticas();

        return repository.save(serie); // salva de novo com temporadas
    }

    private void adicionarTemporadas(Serie serie) {
        for (int i = 0; i < serie.getTotalTemporadas(); i++) {
            String titulo = serie.getTitulo().replace(" ", "+").toLowerCase();
            String url = ApiConf.URL + titulo + "&Season=" + (i + 1) + ApiConf.API_KEY;

            try {
                String json = ApiConsumer.request(url);
                var dadosTemporada = ApiConf.MAPPER.readValue(json, DadosTemporada.class);
                var temporada = new Temporada(dadosTemporada);
                
                temporada.setSerie(serie); // definir o key estrangeiro para as temporadas
                temporada.getEpisodios().forEach(e -> e.setTemporada(temporada)); // E para os episodios
                serie.getTemporadas().add(temporada);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
   

    @Transactional
    public String informarDetalhes(Long id) {
        
         Serie serie = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Série não encontrada"));

        return serie.informarDetalhes();
    }


    public void removeDB(Serie serie) {
        repository.delete(serie);
    }

    public List<Serie> atualizarRepositorio() {
        return repository.findAll();
    }


}
