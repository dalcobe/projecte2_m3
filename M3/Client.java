import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.sound.sampled.SourceDataLine;

public class Client {
    private String nom;
    private String cognom;
    private Dni DNI;
    private Telefon telefon;
    private Email correu;
    private LocalDate data_naix;
    private String condicioFisica;
    private Iban compteBancari;
    private int edat;

    public Client() {
        this.DNI = new Dni();
        this.telefon = new Telefon();
        this.correu = new Email();
        this.compteBancari = new Iban();
        this.data_naix = LocalDate.now();
    }

    private void calcularEdad() {
        LocalDate ara = LocalDate.now();
        this.edat = Period.between(this.data_naix, ara).getYears();
    }

    // public Client (String dni){
    // this.DNI = new Dni(dni);
    // }

    public void consultaClient() throws SQLException {
        Scanner teclat = new Scanner(System.in);
        System.out.println("*CONSULTA D'UN CLIENT*");
        System.out.println("Introdueix DNI");
        String DNI = teclat.next();
        Client cli = consultaClientBD(DNI);

        if (cli != null) {
            System.out.println(cli);
        } else {
            System.out.println("No existeix el client");
        }

        consultaClientBD(DNI);

    }

    private Client consultaClientBD(String DNI) throws SQLException {
        Connection conn = ConnexioBD.getConnection();

        String consulta = "SELECT * FROM socis WHERE DNI = ?";

        PreparedStatement sentencia = conn.prepareStatement(consulta);
        sentencia.setString(1, DNI);
        ResultSet rs = sentencia.executeQuery();

        if (rs.next()) {
            // mapeig de client BD a client OO
            cargarDadesDeSentenciaEnClient(rs);
            // this.nom = rs.getString("nom");
            // this.DNI = rs.getString("DNI");
            // this.cognoms = rs.getString("cognoms");
            // this.telefon = rs.getString("telefon");
            // this.data_naix = rs.getDate("data_naix").toLocalDate();
            // this.correu = rs.getString("correu");
            // this.condicioFisica = rs.getString("condicio fisica");

            return this;
        }

        return null;

    }

    public void altaClient() {
        Scanner teclat = new Scanner(System.in);
        // solicitem el DNI a donar d'alta fins que sigui correcte
        System.out.println("*ALTA D'UN CLIENT*");
        String dni;
        Dni dniObj = new Dni();
        do {
            System.out.println("Introdueix el DNI del client que vols donar d'alta: ");
            dni = teclat.next();

        } while (!dniObj.validarDNI(dni));
        // fiquem el dni en l'objecte
        dniObj.setDni(dni);
        // un cop validat el dni, l'assignem a l'atribut dni del objecte Client
        setDni(dniObj);

        if (consultaClientBD(dniObj.getDni()) != null) {
            System.out.println("El client ja existeix");
        } else {
            System.out.println("Introdueix el nom: ");
            this.nom = teclat.next();
            System.out.println("Introdueix els cognoms: ");
            this.cognom = teclat.next();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            boolean dataCorrecta;

            do {
                dataCorrecta = true;
                System.out.println("Introdueix data naixement de forma correcta (DD.MM.AAA) ");
                try {
                    this.data_naix = LocalDate.parse(teclat.next(), formatter);
                } catch (Exception ex) {
                    dataCorrecta = false;
                }
            } while (!dataCorrecta);

            Telefon telefon = new Telefon();
            do {
                System.out.println("\nIntrodueix el telefon: ");
            } while (!telefon.setTelefon(teclat.next()));
            setTelefon(telefon);

            Email correu = new Email();
            do {
                System.out.println("Introdueix el correu electronic: ");
            } while (!correu.setcorreu(teclat.next()));
            setcorreu(correu);

            CompteBancari compteBancari = new CompteBancari();
            do {
                System.out.println("Introdueix el compte bancari (IBAN complert)");
            } while (!compteBancari.setcompteBancari(teclat.next()));

            setcompteBancari(compteBancari);

            altaClientBD();

            System.out.println("Client donart d'alta: " + this);

        }

    }

    private void altaClientBD() throws SQLException {
        Connection conn = ConnexioBD.getConnection();

        String query = "INSERT INTO socis VALUES (?,?,?,?,?,?,?,?)";

        try (
                PreparedStatement ps = conn.prepareStatement(query);) {
            cargarDadesDeClientEnSentencia(ps);
            ps.executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException("No s'ha pogut agregar un client\n" + this.DNI.getDni());
        }

    }

    private void cargarDadesDeClientEnSentencia(PreparedStatement ps) throws SQLException {
        ps.setString(1, this.DNI.getDni());
        ps.setString(2, this.nom);
        ps.setString(3, this.cognom);
        ps.setString(4, this.compteBancari.getCompte());
        ps.setString(5, this.telefon.getTelefon());
        ps.setString(6, this.correu.getEmail());
        ps.setString(7, data_naix.toString());
    }

    private void cargarDadesDeSentenciaEnClient(ResultSet rs) throws SQLException {
        this.setDni(new Dni(rs.getString("dni")));
        this.setNom(rs.getString("nom"));
        this.setCognom(rs.getString("cognom"));
        this.correu = new Email(rs.getString("email"));
        this.setCCC(new CompteBancari(rs.getString("CCC")));
        this.telefon = new Telefon(rs.getString("telefon"));

        this.setData_naix(rs.getDate("dataNaixement").toLocalDate());
        calcularEdad();
    }

    private void setData_naix(LocalDate localDate) {
    }

    private void setCCC(CompteBancari compteBancari2) {
    }

    private void setCognom(String string) {
    }

    private void setNom(String string) {
    }

    private void setDni(Dni dni2) {
    }

    public ArrayList<Client> getClientOrdenatsCognoms() throws SQLException {
        ArrayList<Client> clients = new ArrayList<>();
        Connection conn = ConnexioBD.getConnection();

        String consulta = "SELECT * FROM socis ORDER BY cognoms, nom";
        PreparedStatement sentencia = conn.prepareStatement(consulta);

        ResultSet rs = sentencia.executeQuery();

        while (rs.next()) {
            Client c1 = new Client();
            c1.cargarDadesDeSentenciaEnClient(rs);

            clients.add(c1);
        }
        return clients;
    }

    public ArrayList<Client> getClientOrdenatsReserves(ResultSet rs) throws SQLException {
        ArrayList<Client> clients = new ArrayList<>();

        Connection conn = ConnexioBD.getConnection();

        String consulta = "SELECT COUNT (ID) AS numRes, s. * FROM reserva_lliure, socis s";
        PreparedStatement sentencia = conn.prepareStatement(consulta);

        ResultSet rs = sentencia.executeQuery();

        while (rs.next()) {
            Client c1 = new Client();
            c1.cargarDadesDeSentenciaEnClient(rs);

            // c1.setNumReserves(rs.getInt("numRes"));

            clients.add(c1);
        }
        return clients;
    }

}
