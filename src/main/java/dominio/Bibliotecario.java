package dominio;

import java.util.Calendar;
import java.util.Date;

import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import utils.StringUtils;

public class Bibliotecario {

	public static final String EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE = "El libro no se encuentra disponible";
	public static final String EL_LIBRO_NO_EXISTE = "El libro no existe en el repositorio";
	public static final String ISBN_NO_DEBE_SER_VACIO = "El ISBN no debe ser vacío";
	public static final String USUARIO_NO_DEBE_SER_VACIO = "El nombre del usuario no debe ser vacío";
	public static final String EL_ISBN_ES_PALINDROMO = "los libros palíndromos solo se pueden utilizar en la biblioteca";
	
	public static final int DIAS_PRESTAMO = 15;

	private RepositorioLibro repositorioLibro;
	private RepositorioPrestamo repositorioPrestamo;

	public Bibliotecario(RepositorioLibro repositorioLibro, RepositorioPrestamo repositorioPrestamo) {
		this.repositorioLibro = repositorioLibro;
		this.repositorioPrestamo = repositorioPrestamo;

	}

	public void prestar(String isbn, String nombreUsuario) {
		if(isbn != null && !isbn.isEmpty() && nombreUsuario != null && !nombreUsuario.isEmpty()){
			if(esPrestado(isbn)){
				throw new PrestamoException(EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE);
			}else{
				Libro libro = this.repositorioLibro.obtenerPorIsbn(isbn);
				
				if(libro != null){
					if(!StringUtils.isPalindrome(libro.getIsbn())){
						Prestamo prestamo = new Prestamo(new Date(), libro, null, nombreUsuario);
						
						if(hasExpirationDate(libro.getIsbn())){
							Date expirationDate = calculateExpirationDate(prestamo);
							prestamo.setFechaEntregaMaxima(expirationDate);
						}
						
						this.repositorioPrestamo.agregar(prestamo);
						
					}else{
						throw new PrestamoException(EL_ISBN_ES_PALINDROMO);
					}
				}else{
					throw new PrestamoException(EL_LIBRO_NO_EXISTE);
				}
			}
		}else if(isbn == null || (isbn != null && isbn.isEmpty())){
			throw new PrestamoException(ISBN_NO_DEBE_SER_VACIO);
		}else{
			throw new PrestamoException(USUARIO_NO_DEBE_SER_VACIO);
		}
		
	}

	public Date calculateExpirationDate(Prestamo prestamo) {
		
		Calendar cal = Calendar.getInstance();
		Date fechaSolicitud = prestamo.getFechaSolicitud();
		cal.setTime(fechaSolicitud);
		
		int sumOfDays = 0;
		
		while (sumOfDays < 15){
			if(cal.get(Calendar.DAY_OF_WEEK) != 1){
				sumOfDays++;
			}
			
			cal.add(Calendar.DATE, 1);
		}
		
		//Se resta 1 día debido que el ciclo siempre va a contar un dia de mas
		cal.add(Calendar.DATE, -1);
		fechaSolicitud = cal.getTime();
		
		return fechaSolicitud;
	}

	public boolean esPrestado(String isbn) {
		Libro libro = this.repositorioPrestamo.obtenerLibroPrestadoPorIsbn(isbn);
		return libro != null;
	}
	
	public boolean hasExpirationDate(String isbn){
		String numbers = StringUtils.getNumbersFromString(isbn);
		int sum = 0;
		if(numbers != null && !numbers.isEmpty()){
			for(int i=0; i < numbers.length(); i++){
				sum += Integer.parseInt(numbers.substring(i, i+1));
			}
			return sum > 30;
		}else{
			return false;
		}
	}

}
