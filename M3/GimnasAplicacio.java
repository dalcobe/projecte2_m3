import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class GimnasAplicacio {

    public static void main(String[] args) throws SQLException, IOException {

        Connection conn = ConnexioBD.getConnection();

        Gimnas g = new Gimnas();
        System.out.println(g);

        g.gestionarGimnas();

    }

}
