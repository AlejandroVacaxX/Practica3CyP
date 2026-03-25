package org.alejandro.vaca.Biblioteca;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alejandro.vaca.Biblioteca.model.LibroModel;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LibroIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private Firestore firestore;

	private CollectionReference collectionReference;
	private DocumentReference documentReference;
	private ApiFuture<QuerySnapshot> querySnapshotFuture;
	private QuerySnapshot querySnapshot;

	@BeforeEach
	void setUp() {
		collectionReference = Mockito.mock(CollectionReference.class);
		documentReference = Mockito.mock(DocumentReference.class);
		querySnapshotFuture = Mockito.mock(ApiFuture.class);
		querySnapshot = Mockito.mock(QuerySnapshot.class);

		when(firestore.collection(anyString())).thenReturn(collectionReference);
		when(collectionReference.document()).thenReturn(documentReference);
		when(collectionReference.document(anyString())).thenReturn(documentReference);
		when(collectionReference.get()).thenReturn(querySnapshotFuture);
		when(collectionReference.whereEqualTo(anyString(), anyString())).thenReturn(Mockito.mock(Query.class));

		when(querySnapshot.getDocuments()).thenReturn(Collections.emptyList());
		try {
			when(querySnapshotFuture.get()).thenReturn(querySnapshot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 8 pruebas de tipo @Test
	@Test
	@DisplayName("1. GET: Devuelve un libro real por su ID")
	void obtenerLibroPorIdTest() throws Exception {
		String id = "id-123";
		LibroModel libro = new LibroModel(id, "Cien años de soledad", "Gabriel Garcia Marquez", 1, "Salamandra", 1990);

		DocumentSnapshot doc = Mockito.mock(DocumentSnapshot.class);
		when(doc.exists()).thenReturn(true);
		when(doc.getId()).thenReturn(id);
		when(doc.toObject(LibroModel.class)).thenReturn(libro);
		when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(doc));

		mockMvc.perform(get("/biblioteca/libros/id/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.titulo").value("Cien años de soledad"));
	}

	@Test
	@DisplayName("2. GET: Listar todos los libros")
	void obtenerTodosLosLibrosTest() throws Exception {
		mockMvc.perform(get("/biblioteca/libros"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	@DisplayName("3. GET: Buscar libros por titulo")
	void obtenerLibrosPorTituloTest() throws Exception {
		String titulo = "Rayuela";
		LibroModel libro = new LibroModel("id-rayuela", titulo, "Julio Cortazar", 1, "Sudamericana", 1963);

		Query query = Mockito.mock(Query.class);
		when(collectionReference.whereEqualTo("titulo", titulo)).thenReturn(query);

		QuerySnapshot qs = Mockito.mock(QuerySnapshot.class);
		QueryDocumentSnapshot qds = Mockito.mock(QueryDocumentSnapshot.class);
		when(qs.getDocuments()).thenReturn(List.of(qds));
		when(qds.getId()).thenReturn("id-rayuela");
		when(qds.toObject(LibroModel.class)).thenReturn(libro);
		when(query.get()).thenReturn(ApiFutures.immediateFuture(qs));

		mockMvc.perform(get("/biblioteca/libros/titulo/" + titulo))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].titulo").value(titulo));
	}

	@Test
	@DisplayName("4. GET: Buscar libros por autor")
	void obtenerLibrosPorAutorTest() throws Exception {
		String autor = "Borges";
		LibroModel libro = new LibroModel("id-borges", "El Aleph", autor, 1, "Emecé", 1949);

		Query query = Mockito.mock(Query.class);
		when(collectionReference.whereEqualTo("autor", autor)).thenReturn(query);

		QuerySnapshot qs = Mockito.mock(QuerySnapshot.class);
		QueryDocumentSnapshot qds = Mockito.mock(QueryDocumentSnapshot.class);
		when(qs.getDocuments()).thenReturn(List.of(qds));
		when(qds.getId()).thenReturn("id-borges");
		when(qds.toObject(LibroModel.class)).thenReturn(libro);
		when(query.get()).thenReturn(ApiFutures.immediateFuture(qs));

		mockMvc.perform(get("/biblioteca/libros/autor/" + autor))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].autor").value(autor));
	}

	@Test
	@DisplayName("5. POST: Guardar un libro válido")
	void guardarLibroValidoTest() throws Exception {
		String json = "{\"titulo\": \"Ficciones\", \"autor\": \"Jorge Luis Borges\", \"edicion\": 1, \"editorial\": \"Alianza\", \"anio\": 1944}";

		when(documentReference.getId()).thenReturn("id-ficciones");
		when(documentReference.set(anyMap())).thenReturn(ApiFutures.immediateFuture(null));

		mockMvc.perform(post("/biblioteca/libros")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.titulo").value("Ficciones"));
	}

	@Test
	@DisplayName("6. GET: Error al buscar un ID que no existe")
	void obtenerLibroInexistenteTest() throws Exception {
		String id = "no-existe";
		DocumentSnapshot doc = Mockito.mock(DocumentSnapshot.class);
		when(doc.exists()).thenReturn(false);
		when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(doc));

		mockMvc.perform(get("/biblioteca/libros/id/" + id))
				.andExpect(status().isBadRequest()); // IllegalArgumentException -> 400
	}

	@Test
	@DisplayName("7. POST: Error por edición fuera de rango (max 20)")
	void guardarLibroEdicionFueraDeRangoTest() throws Exception {
		String json = "{\"titulo\": \"Test\", \"autor\": \"Autor\", \"edicion\": 25, \"editorial\": \"Edit\", \"anio\": 2000}";

		mockMvc.perform(post("/biblioteca/libros")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("8. POST: Error por año menor a 1899")
	void guardarLibroAnioFueraDeRangoTest() throws Exception {
		String json = "{\"titulo\": \"Test\", \"autor\": \"Autor\", \"edicion\": 1, \"editorial\": \"Edit\", \"anio\": 1800}";

		mockMvc.perform(post("/biblioteca/libros")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isBadRequest());
	}

	// 2 PRUEBAS DE TIPO @RepeatedTest

	@RepeatedTest(10)
	@Timeout(value = 2, unit = TimeUnit.SECONDS)
	@DisplayName("9. Repeated GET: Listar libros")
	void obtenerTodosLosLibrosRepetidoTest() throws Exception {
		mockMvc.perform(get("/biblioteca/libros"))
				.andExpect(status().isOk());
	}

	@RepeatedTest(2)
	@DisplayName("10. Repeated POST: Guardar libro válido")
	void guardarLibroValidoRepetidoTest() throws Exception {
		String json = "{\"titulo\": \"Repetido\", \"autor\": \"Autor\", \"edicion\": 1, \"editorial\": \"Edit\", \"anio\": 2020}";
		when(documentReference.getId()).thenReturn("id-repetido");
		when(documentReference.set(anyMap())).thenReturn(ApiFutures.immediateFuture(null));

		mockMvc.perform(post("/biblioteca/libros")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isOk());
	}

	// 4 PRUEBAS DE TIPO @ParameterizedTest

	@ParameterizedTest
	@ValueSource(strings = {
			"{\"titulo\": \"\", \"autor\": \"Autor X\", \"edicion\": 1, \"editorial\": \"Edit\", \"anio\": 2020}",
			"{\"titulo\": \"Libro Y\", \"autor\": \"\", \"edicion\": 1, \"editorial\": \"Edit\", \"anio\": 2020}"
	})
	@DisplayName("11. Parameterized POST: Títulos o autores vacíos fallan")
	void crearLibroInvalidoTest(String jsonInvalido) throws Exception {
		mockMvc.perform(post("/biblioteca/libros")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonInvalido))
				.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@ValueSource(ints = { 1800, 2030, 0 })
	@DisplayName("12. Parameterized POST: Años inválidos fallan")
	void crearLibroInvalidoAnioTest(int anioInvalido) throws Exception {
		String json = String.format(
				"{\"titulo\": \"Test\", \"autor\": \"Autor\", \"edicion\": 1, \"editorial\": \"Edit\", \"anio\": %d}",
				anioInvalido);
		mockMvc.perform(post("/biblioteca/libros")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@ValueSource(ints = { -1, 0, 21 })
	@DisplayName("13. Parameterized POST: Ediciones inválidas fallan")
	void crearLibroInvalidoPorEdicionTest(int valores) throws Exception {
		String json = String.format(
				"{\"titulo\": \"Test\", \"autor\": \"Autor\", \"edicion\": %d, \"editorial\": \"Edit\", \"anio\": 2020}",
				valores);
		mockMvc.perform(post("/biblioteca/libros")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isBadRequest());

	}

	@ParameterizedTest
	@ValueSource(ints = { -1, 0, 21 })
	@DisplayName("13. Parameterized POST: Ediciones inválidas fallan")
	void crearLibroInvalidoPorEdicionTestConAssertTrue(int edicionInvalida) throws Exception {

		String json = String.format(
				"{\"titulo\": \"Test\", \"autor\": \"Autor\", \"edicion\": %d, \"editorial\": \"Edit\", \"anio\": 2020}",
				edicionInvalida);

		// 1. Ejecutamos la petición y GUARDAMOS el resultado en una variable
		MvcResult resultado = mockMvc.perform(post("/biblioteca/libros")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andReturn(); // <-- Esto es clave para poder extraer la respuesta

		// 2. Extraemos el código de estado HTTP (Debería ser 400)
		int codigoDeEstado = resultado.getResponse().getStatus();

		// 3. Evaluamos con assertTrue
		assertTrue(codigoDeEstado == 400, "El estado HTTP debió ser 400 Bad Request");
	}
}
