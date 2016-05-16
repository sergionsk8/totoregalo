package sergionsk8.totonatale;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Partecipante {
	private String nome;
	private String email;
	private String ricevente;
	public static List<Map.Entry<String, String>> invalidPairs = null;
	
	public Partecipante(String name, String mail) {
		this.nome = name;
		this.email = mail;
		this.ricevente = null;
	}
	
	public static void setInvalidPair(String user1, String user2) {
		if (invalidPairs == null)
			invalidPairs = new LinkedList<Map.Entry<String, String>>();
		invalidPairs.add(new AbstractMap.SimpleEntry<>(user1, user2));
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRicevente() {
		return ricevente;
	}
	public boolean setRicevente(String ricevente) {
		if (this.nome.equals(ricevente))
			return false;
		if (invalidPairs != null) {
			for(Map.Entry<String, String> entry : invalidPairs) {
				if (this.nome.equals(entry.getKey()) && ricevente.equals(entry.getValue()))
					return false;
				if (this.nome.equals(entry.getValue()) && ricevente.equals(entry.getKey()))
					return false;
			}
		}
		this.ricevente = ricevente;
		return true;
	}
	public boolean check() {
		if (ricevente == null)
			return false;
		if (nome.equals(ricevente))
			return false;
		if (invalidPairs != null) {
			for(Map.Entry<String, String> entry : invalidPairs) {
				if (nome.equals(entry.getKey()) && ricevente.equals(entry.getValue()))
					return false;
				if (nome.equals(entry.getValue()) && ricevente.equals(entry.getKey()))
					return false;
			}
		}
		return true;
	}
}