package dev.gscordeiro.facesmotors.startup;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.gscordeiro.facesmotors.entities.Automovel;
import dev.gscordeiro.facesmotors.entities.Cor;
import dev.gscordeiro.facesmotors.entities.Foto;
import dev.gscordeiro.facesmotors.entities.Marca;
import dev.gscordeiro.facesmotors.entities.Modelo;

@ApplicationScoped
public class DataPopulator {

	private static final Logger logger = LoggerFactory.getLogger(DataPopulator.class);

	@PersistenceContext
	private EntityManager em;

	private final Map<String, Cor> cores = new LinkedHashMap<>();
	private final Map<String, Modelo> modelos = new LinkedHashMap<>();
	private final Map<String, String> imagensPorModelo = new LinkedHashMap<>();

	@Transactional
	public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object init) {
		Long total = em.createQuery("select count(m) from Marca m", Long.class).getSingleResult();
		if (total > 0) {
			logger.info("Dados iniciais já presentes ({} marcas), pulando carga.", total);
			backfillCodigos();
			return;
		}
		logger.info("Iniciando carga de dados de demonstração...");

		criarCores();
		criarMarcasEModelos();
		criarVeiculos();

		logger.info("Carga concluída: {} marcas, {} modelos, {} veículos.",
			em.createQuery("select count(m) from Marca m", Long.class).getSingleResult(),
			em.createQuery("select count(m) from Modelo m", Long.class).getSingleResult(),
			em.createQuery("select count(a) from Automovel a", Long.class).getSingleResult());
	}

	private void criarCores() {
		for (String nome : List.of("Branco", "Preto", "Prata", "Cinza", "Vermelho", "Azul", "Verde", "Amarelo")) {
			Cor c = new Cor();
			c.setDescricao(nome);
			em.persist(c);
			cores.put(nome, c);
		}
	}

	private void criarMarcasEModelos() {
		criarMarca("Toyota",
			modelo("Corolla", 177, "sedan"),
			modelo("Hilux", 204, "pickup"),
			modelo("Yaris", 111, "hatch"),
			modelo("Corolla Cross", 169, "suv"),
			modelo("RAV4", 222, "suv"),
			modelo("Etios", 98, "hatch"));

		criarMarca("Honda",
			modelo("Civic", 192, "sedan"),
			modelo("City", 126, "sedan"),
			modelo("HR-V", 126, "suv"),
			modelo("Fit", 116, "hatch"),
			modelo("CR-V", 190, "suv"));

		criarMarca("Volkswagen",
			modelo("Gol", 84, "hatch"),
			modelo("Polo", 116, "hatch"),
			modelo("T-Cross", 150, "suv"),
			modelo("Nivus", 128, "suv"),
			modelo("Virtus", 116, "sedan"),
			modelo("Jetta", 231, "sedan"),
			modelo("Amarok", 204, "pickup"));

		criarMarca("Chevrolet",
			modelo("Onix", 116, "hatch"),
			modelo("Onix Plus", 116, "sedan"),
			modelo("Tracker", 133, "suv"),
			modelo("S10", 200, "pickup"),
			modelo("Cruze", 153, "sedan"),
			modelo("Camaro", 461, "sport"));

		criarMarca("Fiat",
			modelo("Argo", 109, "hatch"),
			modelo("Mobi", 75, "hatch"),
			modelo("Toro", 185, "pickup"),
			modelo("Strada", 108, "pickup"),
			modelo("Pulse", 130, "suv"),
			modelo("Cronos", 109, "sedan"));

		criarMarca("Hyundai",
			modelo("HB20", 130, "hatch"),
			modelo("HB20S", 130, "sedan"),
			modelo("Creta", 130, "suv"),
			modelo("Tucson", 185, "suv"));

		criarMarca("Ford",
			modelo("Ranger", 200, "pickup"),
			modelo("Bronco Sport", 170, "suv"),
			modelo("Maverick", 253, "pickup"),
			modelo("Ka", 105, "hatch"));

		criarMarca("Renault",
			modelo("Kwid", 70, "hatch"),
			modelo("Sandero", 82, "hatch"),
			modelo("Duster", 120, "suv"),
			modelo("Kardian", 120, "suv"));

		criarMarca("Jeep",
			modelo("Renegade", 185, "suv"),
			modelo("Compass", 185, "suv"),
			modelo("Commander", 185, "suv"));

		criarMarca("Nissan",
			modelo("Versa", 114, "sedan"),
			modelo("Kicks", 114, "suv"),
			modelo("Frontier", 190, "pickup"));
	}

	private ModeloDef modelo(String nome, int potencia, String tipo) {
		return new ModeloDef(nome, potencia, tipo);
	}

	private void criarMarca(String nomeMarca, ModeloDef... defs) {
		Marca marca = new Marca(nomeMarca);
		em.persist(marca);
		for (ModeloDef def : defs) {
			Modelo modelo = new Modelo();
			modelo.setDescricao(def.nome);
			modelo.setPotencia(def.potencia);
			modelo.setMarca(marca);
			em.persist(modelo);
			String chave = nomeMarca + "|" + def.nome;
			modelos.put(chave, modelo);
			imagensPorModelo.put(chave, "placeholder-" + def.tipo + ".svg");
		}
	}

	private void criarVeiculos() {
		// Compactos populares
		veiculo("Volkswagen", "Gol", "Branco", 2018, 2019, 48900f, 76500f, "Único dono, ar-condicionado, direção hidráulica.");
		veiculo("Volkswagen", "Polo", "Prata", 2021, 2022, 89900f, 32100f, "Versão Highline, central multimídia.");
		veiculo("Chevrolet", "Onix", "Vermelho", 2020, 2021, 74500f, 41250f, "Câmbio automático, baixa kilometragem.");
		veiculo("Hyundai", "HB20", "Azul", 2022, 2023, 88990f, 18400f, "Comfort Plus, garantia de fábrica.");
		veiculo("Fiat", "Argo", "Cinza", 2019, 2020, 62000f, 58300f, "Drive 1.3, IPVA pago, revisões em dia.");
		veiculo("Fiat", "Mobi", "Branco", 2020, 2020, 49900f, 47800f, "Like, ideal para uso urbano.");
		veiculo("Renault", "Kwid", "Verde", 2021, 2022, 56500f, 29700f, "Outsider, multimídia com Android Auto.");
		veiculo("Renault", "Sandero", "Preto", 2019, 2020, 58800f, 64500f, "Expression 1.0, financiamento facilitado.");
		veiculo("Honda", "Fit", "Prata", 2018, 2019, 75900f, 71200f, "EX automático, segundo dono, manual e chave reserva.");
		veiculo("Ford", "Ka", "Vermelho", 2019, 2020, 54900f, 68900f, "SE 1.0, ar gelando, revisado.");

		// Sedans
		veiculo("Toyota", "Corolla", "Prata", 2020, 2021, 124900f, 47800f, "XEi 2.0, bancos em couro, único dono.");
		veiculo("Honda", "Civic", "Preto", 2019, 2020, 119900f, 58200f, "EXL automático, teto solar.");
		veiculo("Volkswagen", "Jetta", "Branco", 2021, 2022, 168900f, 28400f, "GLI 350 TSI, 4 portas, completo.");
		veiculo("Honda", "City", "Cinza", 2022, 2023, 109900f, 21300f, "Touring CVT, central multimídia 8”.");
		veiculo("Volkswagen", "Virtus", "Azul", 2020, 2021, 92900f, 49800f, "Highline 200 TSI, automatizado.");
		veiculo("Chevrolet", "Cruze", "Preto", 2019, 2020, 96900f, 64200f, "LT Turbo, banco de couro.");
		veiculo("Hyundai", "HB20S", "Branco", 2021, 2022, 86900f, 36800f, "Sedã com porta-malas amplo, ideal para Uber.");
		veiculo("Nissan", "Versa", "Prata", 2022, 2023, 99900f, 18900f, "Advance CVT, garantia até 2026.");
		veiculo("Fiat", "Cronos", "Cinza", 2020, 2021, 75900f, 51400f, "Drive 1.3 GSR, multimídia integrada.");
		veiculo("Chevrolet", "Onix Plus", "Vermelho", 2021, 2022, 84900f, 39500f, "LT 1.0 Turbo, troca aceita.");

		// SUVs
		veiculo("Jeep", "Renegade", "Verde", 2020, 2021, 109900f, 47800f, "Sport 1.8, faróis de LED.");
		veiculo("Jeep", "Compass", "Branco", 2021, 2022, 154900f, 38400f, "Longitude T270, teto solar panorâmico.");
		veiculo("Jeep", "Commander", "Preto", 2022, 2023, 219900f, 24500f, "Overland 4x4 Turbo Diesel, 7 lugares.");
		veiculo("Toyota", "Corolla Cross", "Prata", 2022, 2023, 159900f, 21800f, "XRE 2.0, sensor de estacionamento.");
		veiculo("Toyota", "RAV4", "Cinza", 2021, 2022, 209900f, 38900f, "Hybrid SX, tração integral.");
		veiculo("Honda", "HR-V", "Preto", 2020, 2021, 119900f, 49800f, "EXL CVT, multimídia 8”.");
		veiculo("Honda", "CR-V", "Branco", 2019, 2020, 159900f, 67400f, "Touring 4WD, segundo dono.");
		veiculo("Volkswagen", "T-Cross", "Vermelho", 2021, 2022, 119900f, 31200f, "Comfortline 200 TSI, kit multimídia.");
		veiculo("Volkswagen", "Nivus", "Azul", 2022, 2023, 124900f, 19400f, "Highline 200 TSI, VW Play.");
		veiculo("Hyundai", "Creta", "Verde", 2022, 2023, 134900f, 22800f, "Limited 1.0 Turbo, automático.");
		veiculo("Hyundai", "Tucson", "Prata", 2019, 2020, 109900f, 71500f, "GLS 1.6 Turbo, revisões em concessionária.");
		veiculo("Chevrolet", "Tracker", "Branco", 2021, 2022, 119900f, 38600f, "Premier 1.2 Turbo, teto panorâmico.");
		veiculo("Ford", "Bronco Sport", "Cinza", 2022, 2023, 219900f, 18900f, "Wildtrak 4x4, uso recreativo.");
		veiculo("Renault", "Duster", "Amarelo", 2020, 2021, 89900f, 56400f, "Iconic 1.6, baixa quilometragem.");
		veiculo("Renault", "Kardian", "Vermelho", 2024, 2024, 112900f, 4800f, "Iconic 1.0 Turbo, semi-novo de fábrica.");
		veiculo("Nissan", "Kicks", "Azul", 2021, 2022, 99900f, 42100f, "Advance Xtronic, central multimídia.");
		veiculo("Fiat", "Pulse", "Vermelho", 2022, 2023, 104900f, 26400f, "Impetus 1.0 Turbo, design esportivo.");

		// Pickups
		veiculo("Toyota", "Hilux", "Branco", 2020, 2021, 239900f, 78500f, "SRX 2.8 Diesel 4x4, único dono.");
		veiculo("Volkswagen", "Amarok", "Prata", 2019, 2020, 209900f, 92400f, "Highline 3.0 V6 4x4 automática.");
		veiculo("Chevrolet", "S10", "Preto", 2021, 2022, 219900f, 56800f, "High Country 2.8 Diesel 4x4 automática.");
		veiculo("Ford", "Ranger", "Vermelho", 2022, 2023, 279900f, 31200f, "Limited 3.2 Diesel 4x4 automática.");
		veiculo("Ford", "Maverick", "Azul", 2023, 2024, 249900f, 14800f, "Lariat FX4, motor 2.0 Ecoboost.");
		veiculo("Nissan", "Frontier", "Cinza", 2020, 2021, 219900f, 81300f, "XE 2.3 Bi-Turbo Diesel 4x4.");
		veiculo("Fiat", "Toro", "Verde", 2021, 2022, 144900f, 49600f, "Freedom 1.8, completa.");
		veiculo("Fiat", "Strada", "Branco", 2022, 2023, 109900f, 28700f, "Volcano CD 1.3, controle de estabilidade.");

		// Esportivos / destaques
		veiculo("Chevrolet", "Camaro", "Amarelo", 2018, 2019, 289900f, 31400f, "SS 6.2 V8, edição especial Bumblebee.");
	}

	private void veiculo(String marca, String modeloNome, String cor, int anoFab, int anoModelo, float preco, float km, String observacoes) {
		String chave = marca + "|" + modeloNome;
		Modelo modelo = modelos.get(chave);
		if (modelo == null) {
			logger.warn("Modelo não cadastrado, pulando: {}", chave);
			return;
		}
		Cor corEntity = cores.get(cor);
		if (corEntity == null) {
			logger.warn("Cor não cadastrada, pulando veículo: {} {}", chave, cor);
			return;
		}

		Automovel auto = new Automovel();
		auto.setModelo(modelo);
		auto.setCor(corEntity);
		auto.setAnoFabricacao(anoFab);
		auto.setAnoModelo(anoModelo);
		auto.setPreco(preco);
		auto.setKilometragem(km);
		auto.setObservacoes(observacoes);
		em.persist(auto);

		Foto foto = new Foto();
		foto.setNome(imagensPorModelo.get(chave));
		foto.setAutomovel(auto);
		em.persist(foto);
		auto.getFotos().add(foto);
	}

	private void backfillCodigos() {
		List<Automovel> sem = em.createQuery(
				"select a from Automovel a where a.codigo is null",
				Automovel.class).getResultList();
		if (sem.isEmpty()) return;
		logger.info("Atribuindo códigos a {} veículos legados.", sem.size());
		for (Automovel a : sem) {
			a.setCodigo(java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
		}
	}

	private record ModeloDef(String nome, int potencia, String tipo) {}
}
