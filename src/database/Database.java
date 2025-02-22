package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class Database {
    public String url = "jdbc:mysql://localhost:3306/mercadinho";
    public String username = "root";
    public String password = "P@$$w0rd";
    
    
    public boolean exists(String table, String column, String value){
        try (Connection connection = DriverManager.getConnection(url, username, password)){
            String query = String.format("SELECT * FROM %s WHERE %s = '%s'", table, column, value);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            return rs.next();
        }
        catch (SQLException e) {
            throw new IllegalStateException("SQL Error", e);
        }
    }
    
    public void updateValue(String table, String[] values, String id){
         try (Connection connection = DriverManager.getConnection(url, username, password)){
            String set = "SET ";
            String[] columns = this.getColumnsNamesList(table);
            
            for(int i = 0; i < values.length; i++){
                if(i == 0 ){
                    String keyValuePair = String.format("%s = %s", columns[i + 1], values[i]);
                    set = set + keyValuePair;
                }
                else{
                    String keyValuePair = String.format(",%s = %s", columns[i + 1], values[i]);
                    set = set + keyValuePair;
                }                
            }
                       
            String query = String.format("UPDATE %s %s WHERE id_produto = %s;", table, set, id);
            Statement statement = connection.createStatement();
            statement.execute(query);
        }
        catch (SQLException e) {
            throw new IllegalStateException("SQL Error", e);
        }
    }
    
  
    public String[] getColumnsNamesList(String table){
        try (Connection connection = DriverManager.getConnection(url, username, password)){
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(String.format("SHOW COLUMNS FROM %s", table));
            ResultSetMetaData rsmd = rs.getMetaData();
            
            
            int numeroColunas = rsmd.getColumnCount();
            String[] parametros = new String[numeroColunas];
            
            int x = 0;
            while(rs.next()){
                parametros[x] = rs.getString(1);
                x++;
            }
            return parametros;
        }
        catch (SQLException e) {
            throw new IllegalStateException("SQL Error", e);
        }
    }

    public int countAll(String table){
        try (Connection connection = DriverManager.getConnection(url, username, password)){
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(String.format("SELECT COUNT(*) FROM %s;", table));
            
            int total = 0;
            while(rs.next()){
                total = rs.getInt(1);
            }
            return total;
        }
        catch (SQLException e) {
            throw new IllegalStateException("SQL Error", e);
        }
    }
        
    public String[][] selectAll(String table, String columns) {
        try (Connection connection = DriverManager.getConnection(url, username, password)){
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(String.format("SELECT %s FROM %s;",columns, table));
                ResultSetMetaData rsmd = rs.getMetaData();
                
                int numeroColunas = rsmd.getColumnCount();
                int numeroLinhas = this.countAll(table);
                String[][] results = new String[numeroLinhas][numeroColunas];

                while(rs.next()){
                    for(int i=1; i<=numeroColunas; i++){
                        results[rs.getRow() - 1][i-1] = rs.getString(i);
                    }
                }
            return results;
        } 
        catch (SQLException e) {
            throw new IllegalStateException("SQL Error", e);
        }
    }
    
    public void insertInto(String table, String produtoValues){
        try (Connection connection = DriverManager.getConnection(url, username, password)){
            Statement statement = connection.createStatement();
            
            String[] columnsNamesList = this.getColumnsNamesList(table);
            columnsNamesList[0] = "";
            String columnsNames = String.join(",", columnsNamesList);
            char[] columnsNamesChar = columnsNames.toCharArray();
            columnsNamesChar[0] = ' ';
            columnsNames = String.valueOf(columnsNamesChar);
            
            String query = String.format("INSERT INTO %s (%s) VALUES (%s);", table, columnsNames, produtoValues);
            statement.execute(query);
        }
        catch (SQLException e) {
            throw new IllegalStateException("SQL Error", e);
        }
    }
    
    public void deleteRow(String table, int row){
        try (Connection connection = DriverManager.getConnection(url, username, password)){
            Statement statement = connection.createStatement();
            String query = String.format("DELETE FROM %s WHERE id_produto = %d;", table, row);
            statement.execute(query);
        }
        catch (SQLException e) {
            throw new IllegalStateException("SQL Error", e);
        }
    }
    public String[] getProduto(String[] columns, int id){
        try (Connection connection = DriverManager.getConnection(url, username, password)){
            String columnsNames = String.join(",", columns);
            String query = String.format("SELECT %s FROM produto WHERE id_produto=%s", columnsNames, id);
            Statement statement = connection.createStatement(); 
            ResultSet rs = statement.executeQuery(query);
            String[] produto = new String[columns.length];
            
            while(rs.next()){
                for(int i = 1; i <= columns.length;i++){
                    produto[i - 1] = rs.getString(i);
                }
            }
            
            return produto;
        } catch (SQLException e) {
            throw new IllegalStateException("SQL Error", e);
        }
    }
}
