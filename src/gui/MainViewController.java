package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentServices;
import model.services.SellerServices;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {

		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			controller.setSellerServices (new SellerServices());
			controller.updateTableView();
		});

	}

	@FXML
	public void onMenuItemDepartmentAction() {

		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentServices (new DepartmentServices());
			controller.updateTableView();
		});

	}

	@FXML//Evento do menu Item About
	public void onMenuItemAboutAction() {

		loadView("/gui/About.fxml", x -> {});  //x -> {} = fun��o que n�o vai executar nada

	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {

	}

	// Fun��o para abrir outra tela:
	//synchronized --> para que a fun��o n�o seja interrompida por outras threads
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializinAction) { // parametro recebe o caminho da view principal
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); // para carregar uma tela
			VBox newVBox = loader.load();

			/*
			 * Para mostrar a view na tela principal:
			 * 
			 * primeiro pegar referencia da cena, que � a variavel mainScene da classe Main
			 *
			 * � necess�rio pegar os filhos do VBox da janela About e inseri-los na janela
			 * Principal.
			 * 
			 */

			Scene mainScene = Main.getMainScene(); // pegando referencia da cena
			VBox mainVBox = ((VBox) ((ScrollPane) mainScene.getRoot()).getContent());

			/*
			 * -------PEGANDO REFERENCIA PARA O VBox DA TELA PRINCIPAL ---------------
			 * 
			 * 1� mainScene.getRoot()--> M�todo responsavel por pegar o primeiro nodo da
			 * janela principal (Scroll Pane) 2�Fazer um casting do nodo para Scroll Pane 3�
			 * getContent --> Acessa o conte�do do Scroll Pane 4� Como o Scroll Pane � uma
			 * referencia para VBox deve ser feito um casting de tudo para VBox
			 * 
			 * 
			 * 
			 */

			/*
			 * -----PROXIMO PASSO: EXCLUIR OS ELEMENTOS DO VBox DA TELA PRINCIPAL E INCLUIR
			 * O ELEMENTOS DO VBOX DA TELA ABOUT ------------
			 * 
			 * 
			 */

			Node mainMenu = mainVBox.getChildren().get(0); // referencia para menu. pega o primeiro elemento do mainVBox
															// a partir da posi��o 0.
			mainVBox.getChildren().clear(); // limpa todos os filhos do mainVBox
			mainVBox.getChildren().add(mainMenu);// adicionou o menu
			mainVBox.getChildren().addAll(newVBox.getChildren()); // adicionou os filhos do newVBox (Tela About)
			
			// fun��o para ativar o parametro Consumer<T> initializinAction
			
			/*
			 *  fun��o que est� retornando um controller do tipo <T>
			 *  esta fun��o vai retornar um controller do tipo que for passado
			 */
			
			T controller = loader.getController();   
			
			/*
			 * fun��o que vai executar a a��o que foi solicitada no parametro
			 */
			
			initializinAction.accept(controller);
			
			

		} catch (IOException e) {
			Alerts.showAlert("IOException", "Erro carregando a pagina", e.getMessage(), AlertType.ERROR);
		}
	}

	
}
