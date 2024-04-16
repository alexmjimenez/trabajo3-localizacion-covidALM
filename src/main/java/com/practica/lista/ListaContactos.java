package com.practica.lista;

import com.practica.genericas.FechaHora;
import com.practica.genericas.PosicionPersona;

public class ListaContactos {
	private NodoTemporal lista;
	private int size;

	private NodoTemporal insertarCoordenadas (PosicionPersona p, NodoTemporal npTem) {
		NodoPosicion npActual = npTem.getListaCoordenadas();
		NodoPosicion npAnt=null;
		boolean npEncontrado = false;
		while (npActual!=null && !npEncontrado) {
			if(npActual.getCoordenada().equals(p.getCoordenada())) {
				npEncontrado=true;
				npActual.setNumPersonas(npActual.getNumPersonas()+1);
			}else {
				npAnt = npActual;
				npActual = npActual.getSiguiente();
			}
		}
		if(!npEncontrado) {
			NodoPosicion npNuevo = new NodoPosicion(p.getCoordenada(),1, null);
			if(npTem.getListaCoordenadas()==null)
				npTem.setListaCoordenadas(npNuevo);
			else
				npAnt.setSiguiente(npNuevo);
		}
		return npTem;
	}

	public void insertarNodoTemporal (PosicionPersona p) {
		NodoTemporal aux = lista, ant=null;
		boolean salir=false,  encontrado = false;
		while (aux!=null && !salir) {
			if(aux.getFecha().compareTo(p.getFechaPosicion())==0) {
				encontrado = true;
				salir = true;
                insertarCoordenadas(p, aux);
            }else if(aux.getFecha().compareTo(p.getFechaPosicion())<0) {
				ant = aux;
				aux=aux.getSiguiente();
			}else if(aux.getFecha().compareTo(p.getFechaPosicion())>0) {
				salir=true;
			}
		}

		if(!encontrado) {
			NodoTemporal nuevo = new NodoTemporal();
			nuevo.setFecha(p.getFechaPosicion());
            insertarCoordenadas(p, nuevo);

            if(ant!=null) {
				nuevo.setSiguiente(aux);
				ant.setSiguiente(nuevo);
			}else {
				nuevo.setSiguiente(lista);
				lista = nuevo;
			}
			this.size++;
			
		}
	}
	
	private boolean buscarPersona (String documento, NodoPersonas nodo) {
		NodoPersonas aux = nodo;
		while(aux!=null) {
			if(aux.getDocumento().equals(documento)) {
				return true;				
			}else {
				aux = aux.getSiguiente();
			}
		}
		return false;
	}
	
	private void insertarPersona (String documento, NodoPersonas nodo) {
		NodoPersonas aux = nodo, nuevo = new NodoPersonas(documento, null);
		while(aux.getSiguiente()!=null) {				
			aux = aux.getSiguiente();				
		}
		aux.setSiguiente(nuevo);		
	}
	
	public int personasEnCoordenadas () {
		NodoPosicion aux = this.lista.getListaCoordenadas();
		if(aux==null)
			return 0;
		else {
			int cont;
			for(cont=0;aux!=null;) {
				cont += aux.getNumPersonas();
				aux=aux.getSiguiente();
			}
			return cont;
		}
	}
	
	public int tamanioLista () {
		return this.size;
	}

	public String getPrimerNodo() {
		NodoTemporal aux = lista;
		String cadena = aux.getFecha().getFecha().toString();
		cadena+= ";" +  aux.getFecha().getHora().toString();
		return cadena;
	}

	private int numEntreDosInstantes(FechaHora inicio, FechaHora fin, boolean esPersona) {
		if(this.size==0)
			return 0;
		NodoTemporal aux = lista;
		int cont = 0;
		while(aux!=null) {
			if(aux.getFecha().compareTo(inicio)>=0 && aux.getFecha().compareTo(fin)<=0) {
				NodoPosicion nodo = aux.getListaCoordenadas();
				while(nodo!=null) {
					if (esPersona) {
						cont += nodo.getNumPersonas();
					} else {
						cont += 1;
					}
					nodo = nodo.getSiguiente();
				}
            }
            aux = aux.getSiguiente();
        }
		return cont;
	}

	public int numPersonasEntreDosInstantes(FechaHora inicio, FechaHora fin) {
		return numEntreDosInstantes(inicio, fin, true);
	}
	
	public int numNodosCoordenadaEntreDosInstantes(FechaHora inicio, FechaHora fin) {
		return numEntreDosInstantes(inicio, fin, false);
	}

	@Override
	public String toString() {
		String cadena="";
		NodoTemporal aux = lista;
		for(int cont=1; cont<size; cont++) {
			cadena += aux.getFecha().getFecha().toString();
			cadena += ";" +  aux.getFecha().getHora().toString() + " ";
			aux=aux.getSiguiente();
		}
		cadena += aux.getFecha().getFecha().toString();
		cadena += ";" +  aux.getFecha().getHora().toString();
		return cadena;
	}

}
