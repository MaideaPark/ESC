package project.esc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
 
 
public class connmysql {
    private Statement stat;
    private ResultSet rs;
    private ArrayList<String> aList;
    
    
    public final ArrayList<String> testMySql() {
        String dbUrl = "jdbc:mysql://localhost/push";
        String id = "root";
        String pwd = "autoset";
         
        try {
            aList = new ArrayList<String>();
            Connection con = null;
            con = DriverManager.getConnection(dbUrl, id, pwd);
            System.out.println("Success");
            stat = con.createStatement();
        
        
            // ResultSet �� ���� ��� ����
            rs = stat.executeQuery("SELECT * FROM push");
             
            // ResultSet �� ���� �����͸� ��̸���Ʈ�� �߰� 
            while (rs.next()) {
                 aList.add(rs.getNString(1));
            }
            stat.close();
            con.close();
     
        } catch (SQLException sqex) {
            System.out.println("SQLException: " + sqex.getMessage());
            System.out.println("SQLState: " + sqex.getSQLState());
        }
        
        // ��� ID ArrayList�� ��ȯ
        return aList;
     
    }
}