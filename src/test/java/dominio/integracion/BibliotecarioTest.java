package dominio.integracion;

import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.Prestamo;
import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.LibroTestDataBuilder;

public class BibliotecarioTest {

	private static final String CRONICA_DE_UNA_MUERTA_ANUNCIADA = "Cronica de una muerta anunciada";
	
	private SistemaDePersistencia sistemaPersistencia;
	
	private RepositorioLibro repositorioLibros;
	private RepositorioPrestamo repositorioPrestamo;

	@Before
	public void setUp() {
		
		sistemaPersistencia = new SistemaDePersistencia();
		
		repositorioLibros = sistemaPersistencia.obtenerRepositorioLibros();
		repositorioPrestamo = sistemaPersistencia.obtenerRepositorioPrestamos();
		
		sistemaPersistencia.iniciar();
	}
	

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void prestarLibroTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		repositorioLibros.agregar(libro);
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		String nombreUsuario = "Jhon Doe";
		
		// act
		blibliotecario.prestar(libro.getIsbn(), nombreUsuario);
		
		Prestamo prestamo = repositorioPrestamo.obtener(libro.getIsbn());
		
		// assert
		Assert.assertTrue(blibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertNotNull(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn()));
		Assert.assertEquals(nombreUsuario, prestamo.getNombreUsuario());

	}

	@Test
	public void prestarLibroNoDisponibleTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		
		repositorioLibros.agregar(libro);
		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		String nombreUsuario = "Jhon Doe";

		// act
		blibliotecario.prestar(libro.getIsbn(), nombreUsuario);
		try {
			
			blibliotecario.prestar(libro.getIsbn(), nombreUsuario);
			fail();
			
		} catch (PrestamoException e) {
			// assert
			Assert.assertEquals(Bibliotecario.EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE, e.getMessage());
		}
	}
	
	@Test
	public void prestarLibroConIsbnVacioTest() {
		// arrange
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		String isbn = "";
		String nombreUsuario = "Jhon Doe";
		try {
			//act
			blibliotecario.prestar(isbn, nombreUsuario);
			fail();
			
		} catch (PrestamoException e) {
			// assert
			Assert.assertEquals(Bibliotecario.ISBN_NO_DEBE_SER_VACIO, e.getMessage());
		}
	}
	
	@Test
	public void prestarLibroConUsuarioVacioTest() {
		// arrange
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		String isbn = "1234";
		String nombreUsuario = "";
		try {
			//act
			blibliotecario.prestar(isbn, nombreUsuario);
			fail();
			
		} catch (PrestamoException e) {
			// assert
			Assert.assertEquals(Bibliotecario.USUARIO_NO_DEBE_SER_VACIO, e.getMessage());
		}
	}
	
	@Test
	public void prestarLibroNoExistenteTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().build();
		
		repositorioLibros.agregar(libro);
		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		String isbnNoExistente = "1235";
		String nombreUsuario = "Jhon Doe";
		
		try {			
			blibliotecario.prestar(isbnNoExistente, nombreUsuario);
			fail();
			
		} catch (PrestamoException e) {
			// assert
			Assert.assertEquals(Bibliotecario.EL_LIBRO_NO_EXISTE, e.getMessage());
		}
	}
	
	@Test
	public void prestarLibroConIsbnPalindromoTest() {
		// arrange
		String isbnPalindromo = "1ABA1";
		Libro libro = new LibroTestDataBuilder().conIsbn(isbnPalindromo).build();
				
		repositorioLibros.agregar(libro);
		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		String nombreUsuario = "Jhon Doe";
		
		try {
			//act
			blibliotecario.prestar(libro.getIsbn(), nombreUsuario);
			fail();
			
		} catch (PrestamoException e) {
			// assert
			Assert.assertEquals(Bibliotecario.EL_ISBN_ES_PALINDROMO, e.getMessage());
		}
	}
	
	@Test
  public void prestarLibroConFechaEntregaTest() {
    // arrange
    String isbn = "9A9B9B9";
    Libro libro = new LibroTestDataBuilder().conIsbn(isbn).build();
        
    repositorioLibros.agregar(libro);
    
    Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
    
    String nombreUsuario = "Jhon Doe";
    
    //act
    blibliotecario.prestar(libro.getIsbn(), nombreUsuario);
    
    Prestamo prestamo = repositorioPrestamo.obtener(libro.getIsbn());
    
    // assert
    Assert.assertNotNull(prestamo.getFechaEntregaMaxima());
  }
	
	@Test
  public void prestarLibroSinFechaEntregaTest() {
    // arrange
    String isbn = "9A9B9A1";
    Libro libro = new LibroTestDataBuilder().conIsbn(isbn).build();
        
    repositorioLibros.agregar(libro);
    
    Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
    
    String nombreUsuario = "Jhon Doe";
    
    //act
    blibliotecario.prestar(libro.getIsbn(), nombreUsuario);
    
    Prestamo prestamo = repositorioPrestamo.obtener(libro.getIsbn());
    
    // assert
    Assert.assertNull(prestamo.getFechaEntregaMaxima());
  }
	
	@Test
	public void hasExpirationDateTest() {
		// arrange
		String isbn = "9A5B9C9D";
		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		Assert.assertTrue(blibliotecario.hasExpirationDate(isbn));
	}
	
	@Test
	public void hasNotExpirationDateTest() {
		// arrange
		String isbn = "1A5B9C9D";
		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		Assert.assertFalse(blibliotecario.hasExpirationDate(isbn));
	}
	
	@Test
	public void calculateExpirationDateWithLastDaySundayTest() {
		// arrange
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		Libro libro = new LibroTestDataBuilder().build();
		String nombreUsuario = "John Doe";
		
		Date fechaSolicitud = null;
		try {
			fechaSolicitud = formatter.parse("2017-05-26");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Prestamo prestamo = new Prestamo(fechaSolicitud, libro, null, nombreUsuario);
		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		Assert.assertEquals("2017-06-12", formatter.format(blibliotecario.calculateExpirationDate(prestamo)));
	}
	
	@Test
	public void calculateExpirationDateWithoutLastDaySundayTest() {
		// arrange
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		Libro libro = new LibroTestDataBuilder().build();
		String nombreUsuario = "John Doe";
		
		Date fechaSolicitud = null;
		try {
			fechaSolicitud = formatter.parse("2017-05-24");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Prestamo prestamo = new Prestamo(fechaSolicitud, libro, null, nombreUsuario);
		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		Assert.assertEquals("2017-06-09", formatter.format(blibliotecario.calculateExpirationDate(prestamo)));
	}
	
	
}
