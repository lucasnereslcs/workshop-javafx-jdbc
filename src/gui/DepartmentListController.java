package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentServices;

public class DepartmentListController implements Initializable, DataChangeListener{

	private DepartmentServices service;
	
	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId; // Coluna ID

	@FXML
	private TableColumn<Department, String> tableColumnName; // Coluna Name

	@FXML
	private Button btNew;
	
	//para inserir a lista na tela na coluna Department
	
	private ObservableList <Department> obsList;
	

	// m�todo para o bot�o
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department(); //novo departamento para tela do formul�rio
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		initializableNodes(); // m�todo para iniciar algum componente na tela

	}

	private void initializableNodes() {

		// metodos para iniciar algum comportamento das colunas

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		//M�todos para fazer a coluna acompanhar a tela da aplica��o
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}
	
	//injetando dependencia do DepartmentServices	
	public void setDepartmentServices (DepartmentServices service) {
		this.service = service;
	}

	/* Este M�todo:
	 * 
	 * acessa o servi�o
	 * carrega o departamento 
	 * e joga o departamento na ObservableList
	 * 
	 */
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Servico est� nulo");
		}
		
		List<Department> list = service.findAll(); //Para carregar a lista de departamentos
		obsList = FXCollections.observableArrayList(list); //carregando a lista no Observable List;
		tableViewDepartment.setItems(obsList);//Para carregar os itens na TableView
		
			
	}
	
	//Quando criamos uma janela de dialogo devemos dizer o stage que criou a janela
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) { 
		//O argumento recebe o Stage da Janela que abriu a janelinha
		//O absoluteName recebe o nome da View que vai carregar
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); // para carregar uma tela
			Pane pane = loader.load();
			
			//Para injetar o departamento na tela do formul�rio:
			
			DepartmentFormController controller = loader.getController(); //pegou o controlador da tela do formul�rio
			controller.setDepartment(obj);//injetor o departamento no controlador
			controller.setDepartmentServices(new DepartmentServices());
			controller.subscribeDataChangeListener(this);
			
			controller.updateFormData(); //para carregar os dados do departamento no formul�rio
			
			
			
			//Para carregar uma nova janelinha de dialogo na frente do Stage, eu preciso declarar um novo Stage
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department Data: "); //Para definir um nome para o titulo da tela
			dialogStage.setScene(new Scene(pane)); // o painel ser� a nova cena
			dialogStage.setResizable(false); //fun��o que define se a janela pode (true) ou n�o (false) ser redimensionada
			dialogStage.initOwner(parentStage); //quem � o pai dessa janela?? = parentStage
			
			dialogStage.initModality(Modality.WINDOW_MODAL); 
			/* essa janela fica travada, enquanto vc n�o fechar a janela, 
			 * vc n�o consegue aceessar a janela anterior
			
			*/
			 dialogStage.showAndWait(); //mostrar e esperar
			 
			
		}
		catch(IOException e) {
			Alerts.showAlert("IO Exception", "Error load View", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();	//quando os dados forem inseridos ou alterados a lista sera atualizada com esse metodo
		
	}
}
