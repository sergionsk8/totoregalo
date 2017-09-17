/*	TOTONATALE

	Legge la lista dei partecipanti da file e ne calcola una permutazione valida, assegnando ad ogni partecipante
	il nome della persona a cui questo dovrà fare il regalo. Infine invia ad ogni giocatore una email con il risultato della propria estrazione.
	Utilizza il protocollo smtp per l'invio di email (per il momento solo caselle Gmail),
	algoritmo di Fisher-Yates per il calcolo della permutazione.
	Novembre 2013
*/

package sergionsk8.totonatale;

import java.util.LinkedList;
import java.util.List;

public class totonatale {

	public static void main(String[] args) {
		try {
			Cilindro cilindro;
			if (args.length == 1){
				System.out.println(args[0]);
				cilindro = new Cilindro(args[0]);
			}else {
				cilindro = new Cilindro();
				System.out.println("niente");
			}
			//Partecipante.setInvalidPair("Gian", "Carla");
			//Partecipante.setInvalidPair("Sergio", "Miriana");
			//Partecipante.setInvalidPair("Germano", "Lucrezia");
			//Partecipante.setInvalidPair("Raffio", "Serena");

			if (cilindro.estrai()) {
				System.out.println("Estrazione eseguita correttamente!");
				cilindro.stampa();
				cilindro.inviaEmail();
				System.out.println("Email inviate correttamente!");
			} else
				System.out.println("Errore");

			// Test
//			List<String> rec = new LinkedList<>(cilindro.getRiceventi());
//			tototest test = new tototest(rec);
//			for (int j = 0; j < 100000; j++) {
//				cilindro.shuffle();
//				if (cilindro.estrai()) {
//					for (Partecipante p : cilindro.getPartecipanti()) {
//						test.add(p.getNome(), p.getRicevente());
//					}
//				}
//			}
//			test.print();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
