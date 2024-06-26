package com.practica.ems.covid;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.practica.excecption.EmsDuplicateLocationException;
import com.practica.excecption.EmsDuplicatePersonException;
import com.practica.excecption.EmsInvalidNumberOfDataException;
import com.practica.excecption.EmsInvalidTypeException;
import com.practica.excecption.EmsLocalizationNotFoundException;
import com.practica.excecption.EmsPersonNotFoundException;
import com.practica.genericas.Constantes;
import com.practica.genericas.Coordenada;
import com.practica.genericas.FechaHora;
import com.practica.genericas.Persona;
import com.practica.genericas.PosicionPersona;
import com.practica.lista.ListaContactos;

import static java.lang.Integer.parseInt;

public class ContactosCovid {
	private Poblacion poblacion;
	private Localizacion localizacion;
	private ListaContactos listaContactos;

	public ContactosCovid() {
		this.poblacion = new Poblacion();
		this.localizacion = new Localizacion();
		this.listaContactos = new ListaContactos();
	}

	public Poblacion getPoblacion() {
		return poblacion;
	}

	public void setPoblacion(Poblacion poblacion) {
		this.poblacion = poblacion;
	}

	public Localizacion getLocalizacion() {
		return localizacion;
	}

	public void setLocalizacion(Localizacion localizacion) {
		this.localizacion = localizacion;
	}
	
	

	public ListaContactos getListaContactos() {
		return listaContactos;
	}

	public void setListaContactos(ListaContactos listaContactos) {
		this.listaContactos = listaContactos;
	}

	private void readData(String[] datos) throws EmsInvalidTypeException, EmsInvalidNumberOfDataException,
			EmsDuplicatePersonException, EmsDuplicateLocationException {
		if (!datos[0].equals("PERSONA") && !datos[0].equals("LOCALIZACION")) {
			throw new EmsInvalidTypeException();
		}
		if (datos[0].equals("PERSONA")) {
			if (datos.length != Constantes.MAX_DATOS_PERSONA) {
				throw new EmsInvalidNumberOfDataException("El número de datos para PERSONA es menor de 8");
			}
			this.poblacion.addPersona(this.crearPersona(datos));
		}
		if (datos[0].equals("LOCALIZACION")) {
			if (datos.length != Constantes.MAX_DATOS_LOCALIZACION) {
				throw new EmsInvalidNumberOfDataException("El número de datos para LOCALIZACION es menor de 6");
			}
			PosicionPersona pp = this.crearPosicionPersona(datos);
			this.localizacion.addLocalizacion(pp);
			this.listaContactos.insertarNodoTemporal(pp);
		}
	}

	public void loadData(String data, boolean reset) throws EmsInvalidTypeException, EmsInvalidNumberOfDataException,
			EmsDuplicatePersonException, EmsDuplicateLocationException {
		if (reset) {
			this.poblacion = new Poblacion();
			this.localizacion = new Localizacion();
			this.listaContactos = new ListaContactos();
		}
		String[] datas = dividirEntrada(data);
		for (String linea : datas) {
			readData(this.dividirLineaData(linea));
		}
	}

	public void loadDataFile(String fichero, boolean reset) {
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		String[] datas = null;
        String data = null;
        loadDataFile(fichero, reset, archivo, fr, br, datas, data);
	}

	@SuppressWarnings("resource")
	public void loadDataFile(String fichero, boolean reset, File archivo, FileReader fr, BufferedReader br, String[] datas, String data ) {
		try {
			archivo = new File(fichero);
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);
			if (reset) {
				this.poblacion = new Poblacion();
				this.localizacion = new Localizacion();
				this.listaContactos = new ListaContactos();
			}
			while ((data = br.readLine()) != null) {
				datas = dividirEntrada(data.trim());
				for (String linea : datas) {
					readData(this.dividirLineaData(linea));
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	public int findPersona(String documento) throws EmsPersonNotFoundException {
		int pos;
		try {
			pos = this.poblacion.findPersona(documento);
			return pos;
		} catch (EmsPersonNotFoundException e) {
			throw new EmsPersonNotFoundException();
		}
	}

	public int findLocalizacion(String documento, String fecha, String hora) throws EmsLocalizationNotFoundException {

		int pos;
		try {
			pos = localizacion.findLocalizacion(documento, fecha, hora);
			return pos;
		} catch (EmsLocalizationNotFoundException e) {
			throw new EmsLocalizationNotFoundException();
		}
	}

	public List<PosicionPersona> localizacionPersona(String documento) throws EmsPersonNotFoundException {
		int cont = 0;
		List<PosicionPersona> lista = new ArrayList<PosicionPersona>();
        for (PosicionPersona pp : this.localizacion.getLista()) {
            if (pp.getDocumento().equals(documento)) {
                cont++;
                lista.add(pp);
            }
        }
		if (cont == 0)
			throw new EmsPersonNotFoundException();
		else
			return lista;
	}

	public void delPersona(String documento) throws EmsPersonNotFoundException {
		int cont = 0, pos = -1;
        for (Persona persona : this.poblacion.getLista()) {
            if (persona.getDocumento().equals(documento)) {
                pos = cont;
            }
            cont++;
        }
		if (pos == -1) {
			throw new EmsPersonNotFoundException();
		}
		this.poblacion.getLista().remove(pos);
	}

	private String[] dividirEntrada(String input) {
        return input.split("\\n");
	}

	private String[] dividirLineaData(String data) {
        return data.split(";");
	}

	private Persona crearPersona(String[] data) {
		Persona persona = new Persona();
		persona.setDocumento(data[1]);
		persona.setNombre(data[2]);
		persona.setApellidos(data[3]);
		persona.setEmail(data[4]);
		persona.setDireccion(data[5]);
		persona.setCp(data[6]);
		persona.setFechaNacimiento(parsearFecha(data[7]));
		return persona;
	}

	private PosicionPersona crearPosicionPersona(String[] data) {
		PosicionPersona posicionPersona = new PosicionPersona();
		String fecha, hora;
		float latitud, longitud;
		posicionPersona.setDocumento(data[1]);
		fecha = data[2];
		hora = data[3];
		assert fecha != null;
		posicionPersona.setFechaPosicion(parsearFecha(fecha, hora));
		latitud = Float.parseFloat(data[4]);
		longitud = Float.parseFloat(data[5]);
		posicionPersona.setCoordenada(new Coordenada(latitud, longitud));
		return posicionPersona;
	}

	private String[] getSplitString(String s) {
		return s.split("/");
	}

	private int parseIntDia(String fecha) {
		return Integer.parseInt(getSplitString(fecha)[0]);
	}

	private int parseIntMes(String fecha) {
		return Integer.parseInt(getSplitString(fecha)[1]);
	}

	private int parseIntAnio(String fecha) {
		return Integer.parseInt(getSplitString(fecha)[2]);
	}

	private FechaHora parsearFecha (String fecha) {
        return new FechaHora(parseIntDia(fecha), parseIntMes(fecha), parseIntAnio(fecha), 0, 0);
	}

	private FechaHora parsearFecha (String fecha, String hora) {
		int minuto, segundo;
		String[] valores = hora.split(":");
		minuto = Integer.parseInt(valores[0]);
		segundo = Integer.parseInt(valores[1]);
        return new FechaHora(parseIntDia(fecha), parseIntMes(fecha), parseIntAnio(fecha), minuto, segundo);
	}
}
