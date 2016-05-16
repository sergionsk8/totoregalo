/*	TOTONATALE
	Legge la lista dei partecipanti da file e ne calcola una permutazione valida, assegnando ad ogni partecipante
	il nome della persona a cui questo dovrà fare il regalo. Infine invia ad ogni giocatore una email con il risultato della propria estrazione.
	Utilizza il protocollo smtp per l'invio di email (per il momento solo caselle Gmail),
	algoritmo di Fisher-Yates per il calcolo della permutazione.
	Novembre 2013
*/

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class totonatale {
	private static String[] p = new String[10];			//vettore partecipanti
	private static String[] e = new String[10];			//vettore email
	private static String[] r = new String[10];			//vettore riceventi
	private static int N;								//numero di partecipanti
	
	public static void main(String[] args) {
		leggiDaFile();
		estrai();
		invia();
		System.out.println("Operazione conclusa!");
	}
	
	// legge i partecipanti ed i rispettivi indirizzi email da file
	public static void leggiDaFile() {
		try {
			try {
				BufferedReader in = new BufferedReader(new FileReader("provonatale"));
				String line = in.readLine();
				N = 0;
				while (line != null) {
					StringTokenizer tok = new StringTokenizer(line);
					p[N] = tok.nextToken("\t");
					r[N] = p[N];
					e[N] = tok.nextToken("\t");
					line = in.readLine();
					N++;
				}
			} catch (FileNotFoundException fe) {
				fe.printStackTrace();
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	// così com'è, questo algoritmo produce una qualsiasi permutazione degli elementi. puo' capitare anche che uno stesso elemento alla fine
	// si trovi allo stesso posto
	public static void fisherYates() {
		String swap;
		int extr;
		for (int i=N-1; i>0; i--) {
			Random rnd = new Random();				//imposta automaticamente il seed attraverso il timestamp
			extr = rnd.nextInt(i + 1);
			swap = r[i];
			r[i] = r[extr];
			r[extr] = swap;
		}
// visualizzo sono in fase di test delle combinazioni, commentare nella versione finale
//		for (int i=0; i<N; i++) {
//			System.out.println(p[i] + "\t----->\t" + r[i]);
//		}
//		System.out.println();
// fin qui
	}
	
	// estrae correttamente (in caso di permutazione non consentita, ne elabora una nuova)
	public static void estrai() {
		boolean flag;
		System.out.println("Estrazione...\n");
		do {
			fisherYates();
			flag = true;
			for (int i=0; i<N; i++) {
				// caso in cui una persona estrae il proprio nome
				if (p[i].equals(r[i])) {
					flag = false;
				//	System.out.println(p[i] + " regala a se stesso. Permutazione non valida, si ripete\n");
					break;
				}
				// caso in cui Gian estrae Carla o viceversa
				if ((p[i].equals("Gian") && r[i].equals("Carla")) || (p[i].equals("Carla") && r[i].equals("Gian"))) {
					flag = false;
					break;
				}
			}
		} while (flag == false);
		System.out.println("Estrazione effettuata correttamente.");
	}

	public static void invia() {
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
				for (int i=0; i<N; i++) {
					try {
						// set the message content here
						msg.setFrom(new InternetAddress(username));
						msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(e[i]));
						msg.setSubject("TotoNatale 2015");
						msg.setText("Ciao " + p[i] + ",\nquesto Natale, fai un regalo a " + r[i] + "!\n\nUn saluto,\nSergio" + 
										"\n\n\n\ntotonatale 2015");
//						msg.setText("Ciao " + p[i] + ",\nil Natale si avvicina e, a grande richiesta, torna l'estrazione più attesa dell'anno! Preparati :)\n\nUn saluto,\nSergio\n\n\nEmail di prova generata automaticamente, non rispondere.\ntotonatale v1.0 Seabhac Adh");
					} catch(AddressException ae) {
						ae.printStackTrace();
					}
					t.sendMessage(msg, msg.getAllRecipients());
					System.out.println("Email a " + p[i] + " inviata correttamente");
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