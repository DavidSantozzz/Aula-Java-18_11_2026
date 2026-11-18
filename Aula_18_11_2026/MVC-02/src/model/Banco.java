package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;



import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class Banco 
{
	private final String nomeBanco= "dbAula20261118";
	private final String usuario = "root";
    private final String senha = "root";
    
    private String url;
    
    private boolean conectou= false;
	
	public Banco()
	{
		System.out.println( "Executando Construtor de Banco." );
		
		url = "jdbc:mysql://localhost:3306/" + nomeBanco +
			  "?useTimezone=true&serverTimezone=UTC";    
		
		conectou= false;
        
        try ( Connection conexao= conectar() ) 
        {
            System.out.println("Conexão bem-sucedida!");
            conectou= true;
        } 
        catch (SQLException e) 
        {
        	System.err.println( "Erro na tentativa de conexão!" );
            e.printStackTrace();
            //System.exit( -1 );
        }
       
	}
	
	public String getNomeBanco() { return nomeBanco; }
	
	public boolean conectou() { return conectou; }
	
	public void inserir( String texto )
	{
		executarComando( "CALL CADASTRAR ( ? ); ", texto );
	}
	
	public void alterar( int id, String texto )
	{
		executarComando( "CALL ALTERAR ( ?, ? ); ", id, texto );
	}
	
	public void apagar( int id )
	{
		executarComando( "CALL DELETAR ( ? ); ", id );
	}
	
	public DefaultTableModel executarConsulta()
	{
		String sql = "CALL CONSULTAR();";
		return executarConsulta( sql );
	}
	
	private Connection conectar() throws SQLException
	{
		return DriverManager.getConnection( url, usuario, senha );
	}
	
	
	private int executarComando( String sql, Object ... parametros )
	{
		int linhasAfetadas= 0;
		if ( conectou )
		{
			try ( Connection conexao= conectar();
					PreparedStatement stmt= 
							conexao.prepareStatement( sql )
				)
			{
				for ( int i= 0; i < parametros.length; i++ )
				{
					stmt.setObject( i+1, parametros[ i ] );
				}
				
				linhasAfetadas= stmt.executeUpdate();
			}
			catch ( SQLException exc )
			{
				String msgErro= "Erro ao executar " + sql;
				System.err.println( msgErro );				
			}
		}
		
		return linhasAfetadas;
	}
	
	private ResultSet obterConsulta( PreparedStatement stmt, 
									Object ... parametros ) throws SQLException
	{
		for ( int i= 0; i < parametros.length; i++ )
		{
			stmt.setObject( i+1, parametros[ i ] );
		}
		return stmt.executeQuery();
	}
	
	private DefaultTableModel executarConsulta( String sql, Object... parametros)
	{
		Vector<String> nomesColunas= new Vector<>();
		Vector<Vector<Object>> dadosTabela= new Vector<>();
		
		try ( 	Connection conexao= conectar();
				PreparedStatement stmt= conexao.prepareStatement( sql );
				ResultSet rs= obterConsulta( stmt, parametros ); 
			)
		{
			ResultSetMetaData metaData= rs.getMetaData();
			int totalColunas= metaData.getColumnCount();
			
			// Descobre dinamicamente os nomes das colunas
            for (int i = 1; i <= totalColunas; i++)
            {
                nomesColunas.add(metaData.getColumnName(i));
            }
            
            // Extrai todas as linhas de resultado
            while (rs.next()) 
            {
                Vector<Object> linha= new Vector<>();
                for (int i = 1; i <= totalColunas; i++) 
                {
                    linha.add(rs.getObject(i));
                }
                dadosTabela.add(linha);
            }

            // Retorna o modelo preenchido
            return new DefaultTableModel(dadosTabela, nomesColunas)
            {
                @Override
                public boolean isCellEditable(int row, int column)
                {
                    return false; // Torna as células da tabela não editáveis direto pelo clique
                }
            };
		}
		catch ( SQLException exc )
		{
			 System.err.println("Erro ao executar consulta genérica: " +
					 			exc.getMessage());
	         return new DefaultTableModel(); // Retorna modelo vazio em caso de erro.	      
		}
	}
    
}
