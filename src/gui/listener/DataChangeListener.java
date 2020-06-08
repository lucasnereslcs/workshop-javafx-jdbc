package gui.listener;

/*
 * interface criada com o objetivo de:
 * no momento em que um novo departamento for criado
 * e inserido no formulario
 * ao fechar a janelinha de dialogo do Departmento
 * a tela que exibe a lista, seja atualizada imediatamente
 * exibindo o novo departamento
 * 
 */
public interface DataChangeListener {

	void onDataChanged(); //evento para ser disparado quando os dados mudarem
	
}
