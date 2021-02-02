package server;



import java.sql.*;


public class AuthServiceDataBase implements AuthService {
    private Connection connection;
    private Statement stmt;


    private void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection=DriverManager.getConnection("jdbc:sqlite:users.db");
        stmt=connection.createStatement();
    }


    private void disconnect() {
        try {
            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        try {
            connect();
            PreparedStatement pstmtAuth=connection.prepareStatement("SELECT nickname FROM users WHERE (login = ?) AND (password = ?)");
            pstmtAuth.setString(1,login);
            pstmtAuth.setString(2,password);
            ResultSet rs=pstmtAuth.executeQuery();
            if (rs.next()) {
                return rs.getString("nickname");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            disconnect();
        }

        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        try {
            connect();
            PreparedStatement pstmtSearch = connection.prepareStatement("SELECT login, nickname FROM users WHERE (login = ?) OR (nickname = ?)");
            pstmtSearch.setString(1, login);
            pstmtSearch.setString(2, nickname);
            ResultSet rs=pstmtSearch.executeQuery();
            if (rs.next()) {
                return false;
            }
            PreparedStatement pstmtReg=connection.prepareStatement("INSERT INTO users (login , password , nickname) VALUES (? , ? , ?)");
            pstmtReg.setString(1, login);
            pstmtReg.setString(2, password);
            pstmtReg.setString(3, nickname);
            pstmtReg.executeUpdate();

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }finally {
            disconnect();
        }

        return true;
    }
}
