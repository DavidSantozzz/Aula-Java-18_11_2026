package view;		// Pacote com a VIEW (visão).

// Importa classes do pacote "controller".
import controller.Lembrete;
import controller.LembreteController;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JScrollPane;

@SuppressWarnings( "serial" )
class Janela extends JFrame
{
	// Atributo que funciona como contêiner para registros.
	private LembreteController _controller;
	
	private JLabel lbStatus;
	
	private JTable tabelaDados;
	private JScrollPane barraRolagem;
	
	private final Font fonte= new Font( "Arial", Font.BOLD, 18 );
	
	private final int LARGURA_BARRA_ROLAGEM= 25;	
	
	private int[] listaItensSelecionados;
	
	public Janela()
	{
		super( "Prep. Aula CRUD");
		setLayout( null );
		setSize( 600, 400 );
		setLocationRelativeTo( null );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setResizable( false );
		
		JMenuBar barraMenu= new JMenuBar();
		barraMenu.setFont( fonte );
			JMenu menuBanco= new JMenu( "Banco" );
				menuBanco.setFont( fonte );
				JMenuItem itemConectar= new JMenuItem( "Conectar" );
					itemConectar.setFont( fonte );
					itemConectar.addActionListener( ev -> conectar() );
				menuBanco.add( itemConectar );	
				JMenuItem itemAdicionar= new JMenuItem( "Adicionar");
					itemAdicionar.setFont( fonte );
					itemAdicionar.addActionListener( ev -> adicionar() );
				menuBanco.add( itemAdicionar );
				JMenuItem itemApagar= new JMenuItem( "Apagar" );
					itemApagar.setFont( fonte );
					itemApagar.addActionListener( ev -> apagar() );
				menuBanco.add( itemApagar );
			barraMenu.add( menuBanco );
		setJMenuBar( barraMenu );
		
		lbStatus= new JLabel( "Status" );
		lbStatus.setFont( fonte );
		lbStatus.setBounds( 40, 40, 120, 25 );
		add( lbStatus );
		
	    tabelaDados = new JTable();
        
        
        // O JTable DEVE ficar dentro de um JScrollPane para exibir o cabeçalho das colunas
        barraRolagem = new JScrollPane(tabelaDados);
        barraRolagem.setBounds(40, 80, 520, 200); // Exemplo de bounds para seu layout null
     // CORREÇÃO/AJUSTE AQUI: Altera a largura da barra de rolagem vertical para 25 pixels
        // (O padrão do sistema costuma ser em torno de 15px)
       
        barraRolagem.getVerticalScrollBar().setPreferredSize(
            		new java.awt.Dimension(LARGURA_BARRA_ROLAGEM, 0));
   
        
            
        // Acesse o cabeçalho da tabela e mude a fonte dele
        tabelaDados.getTableHeader().setFont( fonte );

        // Você também pode mudar a cor do texto e do fundo do cabeçalho se quiser
        tabelaDados.getTableHeader().setForeground(Color.BLACK);
        tabelaDados.getTableHeader().setBackground(new Color(200, 200, 200));
        
        tabelaDados.setFont( new Font( "Arial", Font.PLAIN, 18 ));
        

        tabelaDados.addMouseListener( new MouseAdapter()
        {
        	@Override
        	public void mouseClicked( MouseEvent e )
        	{        		
        		if ( e.getClickCount() == 2 )
        		{
	        		int linha= tabelaDados.getSelectedRow();
	        		
	        		
	        		if ( linha != -1 && linha < tabelaDados.getRowCount() )
	        		{
	        			Object objId= tabelaDados.getValueAt( linha, 0 );
	        			Object objTexto= tabelaDados.getValueAt( linha,  1 );
	        			
	        			String alterado= 
	        					JOptionPane.showInputDialog( null, "Alterar", 
	        							objTexto.toString() );
	        			if ( alterado != null )
	        			{
	        				if ( _controller.conectou() )
	        				{
	        					int id= (int) objId;
	        					Lembrete lembrete= new Lembrete( alterado );
	        					_controller.alterar( id, lembrete );
	        					atualizarTabela();
	        				}
	        			}
	        		}
        		}
        	}
        });
        
        tabelaDados.getSelectionModel().addListSelectionListener( e -> {
        	
            if ( ! e.getValueIsAdjusting() )
            {
                listaItensSelecionados= tabelaDados.getSelectedRows();
            }
        });

        // CORREÇÃO AQUI: Define as políticas das barras de rolagem
        barraRolagem.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Só aparece se tiver muitas linhas
        barraRolagem.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // NUNCA aparece a horizontal
        
        getContentPane().add(barraRolagem);
        
		conectar();
	}
	
	private void conectar()
	{
		lbStatus.setForeground( Color.black );
		lbStatus.setText( "Aguarde...");
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		new Thread()
		{
			public void run()
			{
				_controller= new LembreteController();
				// A linha abaixo é necessária para a cor de fundo.
				lbStatus.setOpaque( true );
				if ( _controller.conectou() )
				{
					lbStatus.setBackground( Color.black );
					lbStatus.setForeground( Color.green );
					lbStatus.setText( "Sucesso!");
					
					atualizarTabela();		
				}
				else
				{
					lbStatus.setBackground( new Color( 200, 200, 200 ) );
					lbStatus.setForeground( Color.red );
					lbStatus.setText( "Falha!" );
					mensagemFalhaConexao();
				}

				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}.start();
	}
	
	private void adicionar()
	{
		String texto= JOptionPane.showInputDialog( "Digite novo texto:");
		if ( texto != null && ! texto.trim().isEmpty() )
		{
			if ( _controller != null && _controller.conectou() )
			{
				Lembrete lembrete= new Lembrete( texto );
				_controller.inserir( lembrete );
				atualizarTabela();
			}
		}
	}
	
	private void apagar()
	{
		if ( _controller.conectou() && listaItensSelecionados != null &&
				listaItensSelecionados.length > 0 )
		{
			for ( int item : listaItensSelecionados )
			{
				int id= (int) tabelaDados.getValueAt( item,  0 );
				_controller.apagar( id );
			}
			atualizarTabela();
		}
	}
	
	private void mensagemFalhaConexao()
	{
		JLabel lbMensagem= new JLabel( "Falha ao conectar com o banco " +
				_controller.getNomeBanco() );
		lbMensagem.setFont( new Font( "Arial", Font.BOLD, 15 ) );
		JOptionPane.showMessageDialog( this, lbMensagem,
				"Erro de conexão", JOptionPane.ERROR_MESSAGE );
	}
	
	// Método chamado após o sucesso da conexão ou via clique de botão de atualizar
	private void atualizarTabela() 
	{
	    // Executa em uma Thread separada para a interface gráfica não congelar
	    new Thread() 
	    {
	        public void run() 
	        {
	            // Chama o método genérico passando a query e os parâmetros desejados
	            DefaultTableModel modelo = _controller.executarConsulta();
	            
	           
	            
                tabelaDados.setModel(modelo); // Atualiza os dados instantaneamente

                tabelaDados.setRowHeight( 30 );

	    		definirLarguraColunas();
	            

	    		
	            // Força a interface a se repintar e aplicar as mudanças
	            getContentPane().revalidate();
	            getContentPane().repaint();
	        }
	    }.start();
	}
	
	private void definirLarguraColunas()
	{
		tabelaDados.setAutoResizeMode( JTable.AUTO_RESIZE_LAST_COLUMN );
		
		// O total aqui deve somar a largura disponível no seu JScrollPane (ex: 520px)
		definirLarguraColuna(tabelaDados, 0, 50);   // ID (Mais estreita)
		
         
		definirLarguraColuna(tabelaDados, 1, tabelaDados.getWidth() - 50 - 2 );  // Nome
	}
	/**
	 * Ajusta de forma segura a largura ideal e preferencial de uma coluna específica.
	 */
	private void definirLarguraColuna(JTable tabela, int indiceColuna, int largura) 
	{
	    if ( indiceColuna < tabela.getColumnModel().getColumnCount() ) 
	    {
	        tabela.getColumnModel().getColumn(indiceColuna).setPreferredWidth(largura);
	        tabela.getColumnModel().getColumn(indiceColuna).setMinWidth(largura);
	    }
	}

	public static void main( String[] args )
	{
		new Janela().setVisible( true );
	}
}
