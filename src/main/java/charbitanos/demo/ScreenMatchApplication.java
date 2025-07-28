package charbitanos.demo;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.env.YamlPropertySourceLoader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import charbitanos.demo.models.Categoria;
import charbitanos.demo.models.Dados.DadosSerie;
import charbitanos.demo.models.Definitivo.Episodio;
import charbitanos.demo.models.Definitivo.Serie;
import charbitanos.demo.repository.SerieRepository;
import charbitanos.demo.services.ApiConf;
import charbitanos.demo.services.ApiConsumer;
import charbitanos.demo.services.SerieService;

@SpringBootApplication
public class ScreenMatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenMatchApplication.class, args);
	}

	private final int VALID_COMMANDS[] = {1,2,3,4,5,6,7,0};
	private Scanner scanner = new Scanner(System.in);

	private String json;
 
    @Autowired
    private SerieRepository repository;

    @Autowired
    private SerieService serieService;
    
	private List<Serie> series;

	@Override
	public void run(String... args) throws Exception {

		boolean sair = false;

		// Programa inteiro dentro de um while!
		while(!sair) {

		    // Atualizar o repositorio do programa
		    series = serieService.atualizarRepositorio();
			
            System.out.println("""
                =--------------------------------------=
                |   1 - Adicionar um Titulo            |
                |   2 - Remover um Titulo              |
                |   3 - Listar Titulos Adicionados     |
                |   4 - Informacoes de um Titulo       |
                |   5 - Encontrar um Titulo            |
                |   6 - Encontrar varios Titulos       |
                |   7 - Encontrar varios Episodios     |
                |   0 - Sair                           |
                =--------------------------------------=""");
			
			Integer comandoValido = checkCommand();

			switch (comandoValido) {
				case 1:
					adicionarSerie();
					break;
				case 2:
				    if(temSerie(series))removerSerie();
					break;
				case 3:
                    if(temSerie(series))listarTitulosAdicionados();
                    break;
                case 4:
					if(temSerie(series))informacoesSerie();
                    break;
                case 5:
                    if(temSerie(series))encontrarTitulo();
                    break;
                case 6:
                    if(temSerie(series))encontrarVariosTitulos();
                    break;
                case 7:
                    if(temSerie(series))encontrarVariosEpisodios();
                    break;
                default:
					System.out.println("Saindo do programa...");
					sair = true;
					return;
			}
		}
	}
    
    //Metodos para encontrar varios Episodios
    private void encontrarEpisodiosPeloLancamento() {
        
        System.out.println("""
                =---------------------------------------------=
                | Encontrar Episodios definindo um Ano minimo |
                =---------------------------------------------=
                """);

        System.out.print("Serie: ");
        String nomeSerie = scanner.nextLine();

        System.out.print("Ano: ");
        int anoLancamento = scanner.nextInt();
        scanner.nextLine();

        List<Episodio> episodiosAno = repository.episodiosPorSerieEAno(nomeSerie,anoLancamento);
        
        System.out.print("\n");

        episodiosAno.forEach(e -> {
            
            int numTemporada = e.getTemporada().getNumero();
            
            System.out.printf("Episodio: %d, Temporada: %d, Titulo: %s, DataDeLancamento: %s\n", e.getNumero(), numTemporada, e.getTitulo(), e.getLancamento());
        });
    
        System.out.print("\n");
    }

    private void episodiosTop5() {
        
        System.out.println("""
                =-----------------=
                | Episodios Top 5 |
                =-----------------=
                """);
        
        System.out.print("Qual serie voce quer ver o top 5 episodios: ");
        String nomeSerie = scanner.nextLine();
        
        List<Episodio> episodiosTop5 = repository.topEpisodiosPorSerie(nomeSerie);
        
        System.out.print("\n");

        episodiosTop5.forEach(e -> {

            String tituloSerie = e.getTemporada().getSerie().getTitulo(); 
            int numTemporada = e.getTemporada().getNumero();
            
            System.out.printf("Serie: %s, Episodio: %d, Temporada: %d, Titulo: %s, Nota: %.2f\n", tituloSerie, e.getNumero(), numTemporada, e.getTitulo(), e.getNota());
        });

        System.out.print("\n");
    }

    private void encontrarEpisodiosPeloTrecho() {
        
        System.out.print("\nDigite o trecho: ");
        String trechoEpisodio = scanner.nextLine();

        List<Episodio> episodiosEncontrados = repository.episodiosPorTrecho(trechoEpisodio);
        
        System.out.print("\n");

        episodiosEncontrados.forEach(e -> {

            String tituloSerie = e.getTemporada().getSerie().getTitulo(); 
            int numTemporada = e.getTemporada().getNumero();
            
            System.out.printf("Serie: %s, Episodio: %d, Temporada: %d, Titulo: %s\n", tituloSerie, e.getNumero(), numTemporada, e.getTitulo());
        });
        
        System.out.print("\n");
    }

    private void encontrarVariosEpisodios() {
        
        System.out.println("""
                =----------------------------=
                | Encontrar varios Episodios |
                =----------------------------=
                """);
        
        System.out.println("Encontrar Episodios com:\n");

        System.out.println("""
                =-------------------------=
                | 1 - Trecho              |
                | 2 - Top 5               |
                | 3 - Ano de Lancamento   |
                =-------------------------=
                """);
        
        System.out.print("Digite: ");
        int command = scanner.nextInt();
        scanner.nextLine();

        switch (command) {
            case 1:
                encontrarEpisodiosPeloTrecho();     
                break;
            case 2:
                episodiosTop5();
                break;
            case 3:
                encontrarEpisodiosPeloLancamento();
                break;
        }
    }


    //Metodos para encontrar varios titulo
    private void encontrarTitulosApartirDaNotaETemporada() {
        
        System.out.println("""

                =-----------------------------------------------=
                | Encontrar Titulos apartir da Nota e Temporada |
                =-----------------------------------------------=
                """);

        System.out.print("Nota Minima: ");
        var notaMinima = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Temporada Maxima: ");
        var temporadaMaxima = scanner.nextInt();
        scanner.nextLine();

        List<Serie> seriesNotaMinETemporadasMax = repository.seriesPorTemporadaEAvaliacao(notaMinima, temporadaMaxima);
        
        System.out.print("\n");

        seriesNotaMinETemporadasMax.forEach(s -> System.out.printf("%s (Nota %.2f, Temporada(s) %d)\n", s.getTitulo(), s.getNota(), s.getTotalTemporadas()));
    }

    private void encontrarTitulosApartirDaTemporada() {
        
        System.out.println(""" 
                =----------------------------------------------------------------=
                | Encontrar Titulos apartir da definicao de maximo de Temporadas |
                =----------------------------------------------------------------=
                """);

        System.out.print("Maximo de Temporadas: ");
        var temporadasMaxima = scanner.nextInt();
        scanner.nextLine();

        List<Serie> seriesTemporadasMaxima = repository.findByTotalTemporadasLessThanEqual(temporadasMaxima);
        
        System.out.print("\n");

        seriesTemporadasMaxima.forEach(s -> System.out.printf("%s (Temporada(s) %d)\n", s.getTitulo(), s.getTotalTemporadas()));
    }
    
    private void encontrarTitulosApartirDaNota() {
        
        System.out.println("""
                
                =-------------------------------------------------------=
                | Encontrar Titulos apartir da definicao da Nota minima |
                =-------------------------------------------------------=
                """);
        
        System.out.print("Nota Minima: ");
        var notaMinima = scanner.nextDouble();
        scanner.nextLine();
        
        List<Serie> seriesNotaMinima = repository.findByNotaGreaterThanEqual(notaMinima);
        
        System.out.print("\n");

        seriesNotaMinima.forEach(s -> System.out.printf("%s (Nota %.2f)\n", s.getTitulo(), s.getNota()));
    }

    private void encontrarTitulosPorGenero() {
        
        System.out.println(""" 
                
                =-----------------------------=
                | Encontrar Titulo por Genero |
                =-----------------------------=
                """);
        
        System.out.print("Qual genero de Serie voce procura?: ");
        var nomeGenero = scanner.nextLine();

        Categoria categoria = Categoria.fromPortugues(nomeGenero);

        List<Serie> seriesPorGenero = repository.findByGenero(categoria);

        seriesPorGenero.forEach(s -> System.out.println(String.format("%s (%s)", s.getTitulo(), categoria))); 
    }

    private void titulosTop5() {

        
        System.out.println(""" 

                =---------------=
                | Top 5 Titulos |
                =---------------=
                """);

        List<Serie> seriesTop5 = repository.findTop5ByOrderByNotaDesc();
        
        for(int i = 0; i < seriesTop5.size(); i++) {
            System.out.println(String.format("%d. %s (%.2f)", i+1, seriesTop5.get(i).getTitulo(), seriesTop5.get(i).getNota()));
        }

        System.out.print("\n");
    }

    private void encontrarVariosTitulos() {
        
        System.out.println("""
               
                =--------------------------=
                | Encontrar varios Titulos |
                =--------------------------=
                """);

        System.out.println("""
                =----------------------------------=
                | Encontrar esses Titulos pelo(a): |
                |                                  |
                | 1- Top 5                         |
                | 2- Genero                        |
                | 3- Nota                          |
                | 4- Temporadas                    |
                | 5- Nota e Temporadas             |   
                =----------------------------------=
                """);

        try {
        System.out.print("Digite: ");
        int command = scanner.nextInt();
        scanner.nextLine();

        switch (command) {
            case 1:
                titulosTop5();
                break;
            case 2:
                encontrarTitulosPorGenero();
                break;
            case 3:
                encontrarTitulosApartirDaNota(); 
                break;
            case 4:
                encontrarTitulosApartirDaTemporada();                
                break;
            case 5:
                encontrarTitulosApartirDaNotaETemporada(); 
                break;
        }

        } catch (InputMismatchException e) {
            System.out.println("Comando invalido! O comando tem que ser um numero!");
        }
    }


    // Metodos para encontrar um Titulo
    private void encontrarTituloPeloNome() {

        System.out.print("Qual Titulo voce quer encontrar?: ");
        String titulo = scanner.nextLine();

        Optional<Serie> serieEncontrada = repository.findFirstByTituloStartingWithIgnoreCase(titulo); 
        
        if(serieEncontrada.isPresent()) {
            System.out.println("\nSerie encontrada com sucesso!:\n");
            System.out.println(serieEncontrada.get()+"\n");
        } else {
            System.out.println("\nSerie nao encontrada...\n");
        }
    }
    
    private void encontrarTituloPeloAutor() {
        
        System.out.print("Digite o nome do Autor: ");
        String nome = scanner.nextLine();

        List<Serie> seriesEncontradas = repository.findByAtoresContainingIgnoreCase(nome);
         
        if(seriesEncontradas.size() > 0) {
            
            System.out.println("\nTitulos que ele trabalhou:\n");
            seriesEncontradas.forEach(s -> System.out.println(s.getTitulo()));
            System.out.print("\n");

        } else {
            System.out.println("Ator nao foi encontrado nos Titulos Disponiveis");
        }
    }

    private void encontrarTitulo() {
        
        System.out.println("""
            
            =---------------------=
            | Encontrar um Titulo |
            =---------------------=
            """);
        
        System.out.println("Encontrar um titulo pelo seu: ");
        System.out.println("1- Nome\n2- Autor\n");

        try {
            
            System.out.print("Digite: ");
            int command = scanner.nextInt();
            scanner.nextLine();

            switch (command) {
                case 1:
                    encontrarTituloPeloNome();
                    break;
                case 2:
                    encontrarTituloPeloAutor();
                    break;
            }

        } catch (InputMismatchException e) {
            System.out.println("Comando invalido! O comando tem que ser um numero!");
        }       
   }


   // Outros
    private void listarTitulosAdicionados() {
        
        System.out.println("""

            =----------------------------=
            | Listar Titulos Adicionados |
            =----------------------------=
            """);

        for(var serie : series) {
            System.out.println(serie.getTitulo().toUpperCase());
        }

        System.out.println("\nQuantidade: " + series.size()+"\n");
    }

	private boolean temSerie(List<Serie> series) {
		
		if(!series.isEmpty()) {
			return true;
		}

		System.out.println("\nA Lista de Titulos esta vazia, operacao invalida!\n");

		return false;
	}

	private int escolherSerie(Runnable question) {
		
        listagemSerie();
        
        System.out.println("\nAviso: O comando tem que ser um numero (de 1 a "+series.size()+")");

		boolean valido = false;

		while(!valido) {
			
			question.run();
			
            int command = 0;

            try {
                command = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Comando invalido! O comando tem que ser um numero!");
            }

			if(command != 0 && command >= 1 && command <= series.size()) {
				valido = true;
				scanner.nextLine(); // Para limpar o buffer (\n esta existindo ainda)
				return command;
			}
			scanner.nextLine(); // Para limpar o buffer (\n esta existindo ainda)
		}

		return 0;
	} 
	
	// Responsavel por listar e atualizar o repositorio do programa!
	private void listagemSerie() {

		for(int i = 0;i < series.size();i++) {
			System.out.println((i + 1) + " - " + series.get(i).getTitulo());
		}
	}

	private void informacoesSerie() {
		
        System.out.println(""" 

            =--------------------------=
            | Informacoes de um Titulo |
            =--------------------------=
            """);

		int command = escolherSerie(() -> System.out.print("\nQual Titulo voce quer informacoes?: "));
        
        System.out.println(""" 
            
            =----------------------=
            | Informacoes da Serie |
            =----------------------=""");

		System.out.println("\n"+serieService.informarDetalhes(series.get(command - 1).getId())+"\n"); 
    }

	private void removerSerie() {
        
        System.out.println("""

            =-------------------=
            | Remover um Titulo |
            =-------------------=
            """);
	
		int command = escolherSerie(() -> System.out.print("\nQual Titulo voce quer remover?: "));

		System.out.println("\nRemovendo...");
        serieService.removeDB(series.get(command - 1)); // Remover Serie do Banco de Dados 
		series.remove(command - 1); // Remover do repositorio do programa
		System.out.println("\nSerie removida com sucesso!\n");
	}		

	private void adicionarSerie() throws JsonMappingException, JsonProcessingException {
	    
        System.out.println(""" 
            
            =---------------------=
            | Adicionar um Titulo |
            =---------------------=""");
        
		String nome;
        
        do {
		System.out.print("\nQual Serie voce quer adicionar?: ");
		nome = (scanner.nextLine()).replace(" ","+");
		
		final String FULL_URL = ApiConf.URL + nome + ApiConf.API_KEY;

		json = ApiConsumer.request(FULL_URL);

        if(json.toUpperCase().contains("ERROR")) {
            System.out.println("\nEssa Serie nao existente!\n");
        }
        
        } while(json.toUpperCase().contains("ERROR"));

		var dadosSerie = ApiConf.MAPPER.readValue(json, DadosSerie.class);

		// Cria a Serie e salva no banco de dados
		serieService.createSerie(dadosSerie);
        
		System.out.println("\nSerie adicionada com sucesso!\n");
	}

	// Para vericar se a entrada do usuario esta valida
	private int checkCommand() {
		
		Integer command = null;

		while (command == null) {

			System.out.print("\nComando: ");

			command = scanner.nextInt(); // Capturar o comando
			scanner.nextLine(); // Para limpar o buffer

			if(command != null && !contains(VALID_COMMANDS, command)) {
				System.out.println("Esse Comando eh invalido!");
				System.out.println("Tente Novamente...");
				command = null;
			}
		}
		
		return command;
	}
	
	// Um Metodo para verificar se um numero esta em um array
	private boolean contains(int[] array,int num) {

		for(int number : array ) {
			if(number == num) {
				return true;
			}
		}

		return false;
	}
}
