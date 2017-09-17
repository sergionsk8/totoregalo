package sergionsk8.totonatale;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class Cilindro {
	private List<Partecipante> partecipanti = new LinkedList<>();
	private List<String> riceventi = new LinkedList<>();
	private String filename = "example/totoexample";
	private int maxTry = 10;

	public Cilindro() {
		//this.readSettings();
		this.leggiDaFile();
		Collections.shuffle(this.partecipanti);
	}

	public Cilindro(String filename) {
		//this.readSettings();
		this.filename = filename;
		this.leggiDaFile();
		Collections.shuffle(this.partecipanti);
	}

	public void shuffle() {
		Collections.shuffle(this.partecipanti);
	}

	// legge i partecipanti ed i rispettivi indirizzi email da file
	private void leggiDaFile() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line = in.readLine();
			while (line != null) {
				String[] split = line.split(";");
				partecipanti.add(new Partecipante(split[0].trim(), split[1].trim()));
				riceventi.add(split[0].trim());
				line = in.readLine();
			}
			in.close();
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getRiceventi() {
		return this.riceventi;
	}

	public List<Partecipante> getPartecipanti() {
		return this.partecipanti;
	}

	// TODO: Se la logica di estrazione è corretta, serve quest'altro check?
	public boolean check() {
		for (Partecipante p : partecipanti) {
			if (!p.check())
				return false;
		}
		return true;
	}

	// Estrae una combinazione corretta.
	// Ritorna false dopo maxTry tentativi non andati a buon fine.
	public boolean estrai() {
		int i = 1;
		do {
			System.out.println("Tentativo di estrazione numero " + i + " di " + this.maxTry + "...");
			i++;
		} while ((!this.provaEstrazione() || !this.check()) && i <= this.maxTry);
		if (i >= this.maxTry) {
			System.out.println("Numero massimo di tentativi raggiunto. Risolvere eventuali inconsistenze negli invalidPair");
			return false;
		}
		return this.finalCheck();
	}

	// Esegue una singola estrazione tenendo conto di tutti i vincoli
	public boolean provaEstrazione() {
		Random rnd = new Random();
		List<String> tempRec = new LinkedList<>(riceventi);
		Collections.shuffle(tempRec, rnd);
		for (int i = 0; i < this.partecipanti.size(); i++) {//Partecipante part : this.partecipanti) {
			Partecipante p = this.partecipanti.get(i);
			Iterator<String> ricIt = tempRec.iterator();
			boolean found = false;
			while(ricIt.hasNext()) {
				String ricevente = ricIt.next();
				if (p.setRicevente(ricevente)) {
					ricIt.remove();
					found = true;
					break;
				}
				if (tempRec.size() == 0)
					return false;
			}
			if (!found) {
				System.out.println("Nessun ricevente adatto per " + p.getNome());
				return false;
			}
		}
		return true;
	}

	// Stampa i risultati
	public void stampa() {
		for (Partecipante partecipante : partecipanti) {
			System.out.println(partecipante.getNome() + "\t---->\t" + partecipante.getRicevente());
		}
	}

	// Check finale di consistenza dei risultati.
	// Controlla che tutti facciano e ricevano uno ed un solo regalo.
	public boolean finalCheck() {
		List<String> finalRec = new LinkedList<>();
		List<String> finalPart = new LinkedList<>();

		for (Partecipante p : this.partecipanti) {
			for (String tPart : finalPart) {
				if (p.getNome().equals(tPart)) {
					System.out.println(tPart + " fa due regali!");
					return false;
				}
			}
			finalPart.add(p.getNome());
			for (String tRec : finalRec) {
				if (p.getRicevente().equals(tRec)) {
					System.out.println(tRec + " riceve due regali!");
					return false;
				}
			}
			finalRec.add(p.getRicevente());
		}

		if (finalRec.size() != finalPart.size())
			return false;

		return true;
	}

	public void inviaEmail() {
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

				for (Partecipante p : this.partecipanti) {
					try {
						// set the message content here
						msg.setFrom(new InternetAddress(username));
						msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(p.getEmail()));
						msg.setSubject("TotoNatale 2016");
						msg.setText("Ciao " + p.getNome() + ",\nquesto Natale, fai un regalo a " + p.getRicevente() + "!\n\nUn saluto,\nSergio" +
										"\n\n\n\ntotonatale 2016");
						t.sendMessage(msg, msg.getAllRecipients());
						System.out.println("Email a " + p.getNome() + " inviata correttamente");
					} catch(AddressException ae) {
						ae.printStackTrace();
					}
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
