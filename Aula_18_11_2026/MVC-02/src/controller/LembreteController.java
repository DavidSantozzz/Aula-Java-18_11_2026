package controller;	// Pacote Controller. 

import javax.swing.table.DefaultTableModel;

import model.Banco;

public class LembreteController 
{
	private Banco _banco;
	
	public LembreteController()
	{
		_banco= new Banco();
	}
	
	public boolean conectou()
	{
		return _banco.conectou();
	}
	
	public void alterar( int id, Lembrete lembrete )
	{
		_banco.alterar( id, lembrete.texto() ); // Usa o record.
	}
	
	public void inserir( Lembrete lembrete )
	{
		_banco.inserir( lembrete.texto() );
	}
	
	public DefaultTableModel executarConsulta()
	{
		return _banco.executarConsulta();
	}
	
	public void apagar( int id )
	{
		_banco.apagar(id);
	}
	
	public String getNomeBanco()
	{
		return _banco.getNomeBanco();
	}
}
