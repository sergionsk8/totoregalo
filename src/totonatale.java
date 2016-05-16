/*	TOTONATALE

	Legge la lista dei partecipanti da file e ne calcola una permutazione valida, assegnando ad ogni partecipante
	il nome della persona a cui questo dovrà fare il regalo. Infine invia ad ogni giocatore una email con il risultato della propria estrazione.
	Utilizza il protocollo smtp per l'invio di email (per il momento solo caselle Gmail),
	algoritmo di Fisher-Yates per il calcolo della permutazione.
	Novembre 2013
*/

package sergionsk8.totonatale;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class totonatale {
	private List<Partecipante> partecipanti = new LinkedList<>();
	private List<String> riceventi = new LinkedList<>();
	private String filename = "res/provonatale";
	private static int maxTry = 10;
	
	public static void main(String[] args) {
		try {
			Partecipante.setInvalidPair("Gian", "Carla");
			Partecipante.setInvalidPair("Sergio", "Alberto");
			Partecipante.setInvalidPair("Alberto", "Germano");
			totonatale totonat = new totonatale();
			totonat.leggiDaFile();
			int i = 1;
			do {
				System.out.println("Tentativo di estrazione numero " + i);
				i++;
			} while (!totonat.estrai() || !totonat.check() || i >= maxTry);
			if (i >= maxTry) {
				System.out.println("Numero massimo di tentativi raggiunto. Risolvere eventuali inconsistenze negli invalidPair");
			}
				
			totonat.stampa();
			totonat.invia();
			System.out.println("Operazione conclusa!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean check() {
		for (Partecipante p : partecipanti) {
			if (!p.check())
				return false;
		}
		return true;
	}

	// legge i partecipanti ed i rispettivi indirizzi email da file
	private void leggiDaFile() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line = in.readLine();
			while (line != null) {
				String[] split = line.split("\t");
				partecipanti.add(new Partecipante(split[0], split[1]));
				riceventi.add(split[0]);
				line = in.readLine();
			}
			in.close();
			Collections.shuffle(partecipanti);
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean estrai() {
		Random rnd = new Random();
		List<String> tempRec = new LinkedList<>(riceventi);
		Collections.shuffle(tempRec, rnd);
		for (int i = 0; i < this.partecipanti.size(); i++) {//Partecipante part : this.partecipanti) {
			Partecipante p = this.partecipanti.get(i);
			Iterator<String> ricIt = tempRec.iterator();
			while(ricIt.hasNext()) {
				String ricevente = ricIt.next();
				if (p.setRicevente(ricevente)) {
					ricIt.remove();
					break;
				}
				if (tempRec.size() == 0)
					return false;
			}
		}
		return true;
	}
	
	private void stampa() {
		for (Partecipante partecipante : partecipanti) {
			System.out.println(partecipante.getNome() + "\t---->\t" + partecipante.getRicevente());
		}
	}

	private void invia() {
		try{
			// host, username e password dell'account da cui vengono inviate le email (solo account gmail per ora)
			// Console per poter usare readPassword, che non mostra il testo inserito (restituisce un char[])
			Console cons = System.console();
			String host = "smtp.gmail.com";
			System.out.println("Inserisci le tue credenziali Gmail");
			System.out.print("Username: ");
			String username = cons.readLine();
			System.out.print("Password: ");
			String password = new String(cons.readPassword());

			Properties props = new Properties();
			// imposta le proprietà della sessione smtp da aprire
			// set any needed mail.smtps.* properties here
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			
			// apre una sessione smtp
			Session session = Session.getInstance(props);
			
			//crea un nuovo oggetto email
			MimeMessage msg = new MimeMessage(session);
			
			// da qui in poi, si possono inviare più email nella stessa sessione
			try {
				Transport t = session.getTransport("smtps");
				t.connect(host, username, password);
				System.out.println("Invio email in corso...");
//				for (int i=0; i<N; i++) {
				for (Partecipante p : this.partecipanti) {
					try {
						// set the message content here
						msg.setFrom(new InternetAddress(username));
						msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(p.getEmail()));
						msg.setSubject("TotoNatale 2015");
						msg.setText("Ciao " + p.getNome() + ",\nquesto Natale, fai un regalo a " + p.getRicevente() + "!\n\nUn saluto,\nSergio" + 
										"\n\n\n\ntotonatale 2015");
//						msg.setText("Ciao " + p[i] + ",\nil Natale si avvicina e, a grande richiesta, torna l'estrazione più attesa dell'anno! Preparati :)\n\nUn saluto,\nSergio\n\n\nEmail di prova generata automaticamente, non rispondere.\ntotonatale v1.0 Seabhac Adh");
					} catch(AddressException ae) {
						ae.printStackTrace();
					}
					t.sendMessage(msg, msg.getAllRecipients());
					System.out.println("Email a " + p.getNome() + " inviata correttamente");
				}
				t.close();
			} catch(NoSuchProviderException nspe) {
				nspe.printStackTrace();
			}	
		} catch(MessagingException me) {
			me.printStackTrace();
		}
	}
}