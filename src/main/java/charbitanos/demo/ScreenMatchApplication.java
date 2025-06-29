package charbitanos.demo;

import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import charbitanos.demo.models.Dados.DadosSerie;
import charbitanos.demo.models.Definitivo.Serie;
import charbitanos.demo.services.ApiConf;
import charbitanos.demo.services.ApiConsumer;
import charbitanos.demo.services.SerieService;

@SpringBootApplication
public class ScreenMatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenMatchApplication.class, args);
	}

	private final int VALID_COMMANDS[] = {1,2,3,0};
	private Scanner scanner = new Scanner(System.in);

	private String json;

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
					1 - Adicionar um Titulo
					2 - Remover um Titulo
					3 - Informacoes de um Titulo				
					0 - Sair""");
			
			Integer comandoValido = checkCommand();

			switch (comandoValido) {
				case 1:
					adicionarSerie();
					break;
				case 2:
				    if(temSerie(series))removerSerie();
					break;
				case 3:
					if(temSerie(series))informacoesSerie();
					break;
				default:
					System.out.println("Saindo do programa...");
					sair = true;
					return;
			}
		}
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

		boolean valido = false;

		while(!valido) {
			
			question.run();
			int command = scanner.nextInt();
			
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
		
		System.out.println("\nQual titulo voce quer ver as informacoes: ");
		
	
		int command = escolherSerie(() -> System.out.print("\nQual Titulo voce quer informacoes?: "));

		System.out.println("\n"+series.get(command - 1).informarDetalhes()+"\n");
	}

	private void removerSerie() {

		System.out.println("\nSeries que estao diponiveis para a remocao:");
	
		int command = escolherSerie(() -> System.out.print("\nQual Titulo voce quer remover?: "));

		System.out.println("Removendo...");
		series.remove(command - 1);
		System.out.println("Serie removida com sucesso!");
	}

	private void adicionarSerie() throws JsonMappingException, JsonProcessingException {
		
		String nome;

		System.out.print("\nQual Serie voce quer adicionar?: ");
		nome = (scanner.nextLine()).replace(" ","+");
		
		final var FULL_URL = ApiConf.URL + nome + ApiConf.API_KEY;

		json = ApiConsumer.request(FULL_URL);

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
