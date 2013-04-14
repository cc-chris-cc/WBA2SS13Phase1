package aufgabe4;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import aufgabe4.Rezept.Kommentare.Eintrag;


public class Main {

	private static String xmlPath;
	private static Rezept rezept;
	private static JAXBContext jaxc;
	private static SimpleDateFormat df;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws JAXBException, IOException {
		// Hilfsvariable wird für das Menue benötigt, immer wenn sie ungeleich 0 ist war die Eingabe erfolgreich
		//ist die Hilfsvariable gleich 0 hat man die Mögllichkeit eine Eingabe zu machen
		int hilfsvariable = 0;
		// Durch SimpleDateFormat wird eine "deutsche" Datums- und Zeitangabe erzeugt, da 
		// der XML-Schema Typ nur die amerikanische Variante (yy-mm-dd T hh:mm:ss) bietet
		df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Scanner in = new Scanner(System.in);
		
		// XML Datei auslesen
		File path = new File ("src/aufgabe3");
		
		
		System.out.print("XML Datei auswaehlen (Aufgabe3.xml):");
		// per Konsole wird aus gewaehlte XML Datei zusammengesetzt 
		String xmlFile = in.nextLine();
		xmlPath = path+ "/" +xmlFile;
		System.out.println("ausgewählte Datei:" + xmlPath);
		
		// neue Instanz von JAXBContext wird angelegt
		jaxc = JAXBContext.newInstance(Rezept.class);
		
		// aus der XML Datei (Aufgabe3.xml) werden mittels der Mehthode unmarshal() Java-Objekte erzeugt
		unmarshal();
		
		// Mittels While-Schleife wird garantiert, dass der Nutzer bei der Auswahl der Optionen 
		// auch Fehleingaben machen kann, diese werden dem Nutzer angezeigt, ohne dass das Programm beendet wird.
		// Bei richtiger Eingabe wird der Nutzer zur nächsten Option geführt. 
		while( hilfsvariable == 0 ){
		
			switch (menue()) {
		case 1:
			ausgabe();
			hilfsvariable++;
			break;
		case 2:
			kommentarVerfassen();
			hilfsvariable++;
			break;
		default:
			System.out.println("Ungültige Eingabe");
			System.out.println("Bitte erneut eingeben: ");
			hilfsvariable = 0;
		}
		}
		
		
		}
	
	// Methode zum verfassen von Kommentaren. Es kuennen Autorname und der Kommentar eingegeben werden. Das Datum wird mitels
	// GregorianCalendar dem jeweiligen Kommentar (automatisch) hinzugeduegt
	private static void kommentarVerfassen() throws JAXBException, IOException {
		Eintrag eintrag = new Eintrag();
		System.out.println("Kommentar verfassen: ");
		// Eingabe für den Namen des Autors
		System.out.println("Autorname:");
		Scanner in = new Scanner(System.in);
		String autor = in.nextLine(); 
		eintrag.setAutor(autor);
	
		
		
		
		
		// Datum und Uhrzeit für den Kommentar
		
		 GregorianCalendar gCalendar = new GregorianCalendar();
		 Date currentDate = new Date();
		 gCalendar.setTime(currentDate);
		 XMLGregorianCalendar xmlCalendar = null;
		 try {
		 xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
		 	} catch (DatatypeConfigurationException ex) {
		 	}
		 
		eintrag.setDatum(xmlCalendar);
		
		// Eingabe des Kommentars
		
		System.out.println("Text eingeben: ");
		String text = in.nextLine();
		eintrag.setText(text);
		
		// Neuer Eintrag wird den vorhandenen Eintraegen mittels ".add"-Methode hinzugefuegt.
		rezept.getKommentare().getEintrag().add(eintrag);
		
		// Methode, die es ermeglicht aus Java-Objekten ein XML Dokument zu generieren 
		marshal();
		
	}
	
	// Methode, die es ermoeglicht es aus Java-Objekten ein XML Dokument zu generieren
	private static void marshal() throws JAXBException{
		Marshaller marshaller = jaxc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("jaxb.schemaLocation", "http://example.org/Rezepte Rezepte.xsd");
		marshaller.marshal(rezept, new File(xmlPath));
		
	}
	
	// Menue ermoeglicht eine simple Navigation und Auswahl von Optionen
	private static int menue() {
		
		System.out.println("Willkommen in Menue, bitte iene Auswahl treffen: ");
		System.out.println("1: Rezept ausgeben: ");
		System.out.println("2: Kommentar verfassen: ");
		
		Scanner in = new Scanner(System.in);
		int auswahl = in.nextInt();
		in.nextLine();
		System.out.println("Eingabe war: " +auswahl);
		return auswahl;
		
		
	}
	
	
	// Ausgabe der XML Datei (Aufgabe3.xml) für Lechen´s Schokoladenkuchen
	private static void ausgabe() {
		System.out.println(rezept.getTitel());
		System.out.println("");
		
		
		if (!rezept.getFotos().isEmpty()){
			
				System.out.println(rezept.getFotos());
			} else
				System.out.println("Kein Foto vorhanden.");
		System.out.println("");
	
		for (int i = 0 ; i < rezept.getZutaten().getZutat().size() ; i++){
			System.out.println(rezept.getZutaten().getZutat().get(i).getMenge() + " " +
							rezept.getZutaten().getZutat().get(i).getEinheit() + " " +
							rezept.getZutaten().getZutat().get(i).getBezeichnung());
		}
		System.out.println("");
		System.out.println("Portionen: " + rezept.getPortionen());
		System.out.println("");
		
		System.out.println("Zubereitung: ");
		System.out.println("Arbeitsziet: " +rezept.getZubereitung().getArbeitszeit() +" Minuten" + "   Schwierigkeitsgrad: "
							+ rezept.getZubereitung().getSchwierigkeitsgrad()+ "  Brennwert: " 
							+rezept.getZubereitung().getBrennwertAnzeige().getMenge2() + " " 
							+rezept.getZubereitung().getBrennwertAnzeige().getBrennwert());
		System.out.println("");
		System.out.println(rezept.getZubereitung().getText());
		System.out.println("");
		
		System.out.println("Kommentare: ");
		for (int i = 0 ; i < rezept.getKommentare().getEintrag().size() ; i++){
			Date d = rezept.getKommentare().getEintrag().get(i).getDatum().toGregorianCalendar().getTime();
			System.out.println("Autor: " +rezept.getKommentare().getEintrag().get(i).getAutor() + "    Datum: "
					+ df.format(d) + " Uhr");
			System.out.println(rezept.getKommentare().getEintrag().get(i).getText());
		}
	}
	
		

	// Methode um XML Datei einzulesen und als JAVA-Objekt(e) auszugeben 
	private static void unmarshal() throws JAXBException, IOException {
		Unmarshaller unmarshaller = jaxc.createUnmarshaller();
		
		// Verbindung zu einer Datei, sollte diese Verbindung nicht 
		// hergestellt werden, schließt der Vorgang sich.
		
		InputStream inputStream = new FileInputStream(xmlPath);
		Reader reader = new InputStreamReader(inputStream, "UTF-8");
		
		try {
			rezept = (Rezept) unmarshaller.unmarshal(reader);
		} finally {
			reader.close();
		}
		
	}
	
	

}
