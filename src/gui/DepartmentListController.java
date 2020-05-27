package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentServices;

public class DepartmentListController implements Initializable {

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
	

	// método para o botão
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		initializableNodes(); // método para iniciar algum componente na tela

	}

	private void initializableNodes() {

		// metodos para iniciar algum comportamento das colunas

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		//Métodos para fazer a coluna acompanhar a tela da aplicação
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}
	
	//injetando dependencia do DepartmentServices	
	public void setDepartmentServices (DepartmentServices service) {
		this.service = service;
	}

	/* Este Método:
	 * 
	 * acessa o serviço
	 * carrega o departamento 
	 * e joga o departamento na ObservableList
	 * 
	 */
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Servico está nulo");
		}
		
		List<Department> list = service.findAll(); //Para carregar a lista de departamentos
		obsList = FXCollections.observableArrayList(list); //carregando a lista no Observable List;
		tableViewDepartment.setItems(obsList);//Para carregar os itens na TableView
		
			
	}
}
