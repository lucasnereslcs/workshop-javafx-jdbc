package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentServices;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentServices service;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId; // Coluna ID

	@FXML
	private TableColumn<Department, String> tableColumnName; // Coluna Name

	@FXML
	private TableColumn<Department, Department> tableColumnEDIT; // coluna para editar

	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE; // coluna para remover

	@FXML
	private Button btNew;

	// para inserir a lista na tela na coluna Department

	private ObservableList<Department> obsList;

	// método para o botão
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department(); // novo departamento para tela do formulário
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		initializableNodes(); // método para iniciar algum componente na tela

	}

	private void initializableNodes() {

		// metodos para iniciar algum comportamento das colunas

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// Métodos para fazer a coluna acompanhar a tela da aplicação
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}

	// injetando dependencia do DepartmentServices
	public void setDepartmentServices(DepartmentServices service) {
		this.service = service;
	}

	/*
	 * Este Método:
	 * 
	 * acessa o serviço carrega o departamento e joga o departamento na
	 * ObservableList
	 * 
	 */

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Servico está nulo");
		}

		List<Department> list = service.findAll(); // Para carregar a lista de departamentos
		obsList = FXCollections.observableArrayList(list); // carregando a lista no Observable List;
		tableViewDepartment.setItems(obsList);// Para carregar os itens na TableView
		initEditButtons(); // metodo para acrescentar um botão edit em cada linha da tabela
		initRemoveButtons(); //método para acrescentar um botão de remover em cada linha da tabela
	}

	// Quando criamos uma janela de dialogo devemos dizer o stage que criou a janela
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		// O argumento recebe o Stage da Janela que abriu a janelinha
		// O absoluteName recebe o nome da View que vai carregar
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); // para carregar uma tela
			Pane pane = loader.load();

			// Para injetar o departamento na tela do formulário:

			DepartmentFormController controller = loader.getController(); // pegou o controlador da tela do formulário
			controller.setDepartment(obj);// injetor o departamento no controlador
			controller.setDepartmentServices(new DepartmentServices());
			controller.subscribeDataChangeListener(this); // inscrevendo para receber o evento do formulário

			controller.updateFormData(); // para carregar os dados do departamento no formulário

			// Para carregar uma nova janelinha de dialogo na frente do Stage, eu preciso
			// declarar um novo Stage

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department Data: "); // Para definir um nome para o titulo da tela
			dialogStage.setScene(new Scene(pane)); // o painel será a nova cena
			dialogStage.setResizable(false); // função que define se a janela pode (true) ou não (false) ser
												// redimensionada
			dialogStage.initOwner(parentStage); // quem é o pai dessa janela?? = parentStage

			dialogStage.initModality(Modality.WINDOW_MODAL);
			/*
			 * essa janela fica travada, enquanto vc não fechar a janela, vc não consegue
			 * aceessar a janela anterior
			 * 
			 */
			dialogStage.showAndWait(); // mostrar e esperar

		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error load View", e.getMessage(), AlertType.ERROR);
		}
	}

	/*
	 * Esse método recebe o evento (No caso, o recebimento de um novo departamento)
	 * e executa alguma ação (no caso, a atualização dos dados no formulário)
	 * 
	 */
	@Override
	public void onDataChanged() {
		updateTableView(); // quando os dados forem inseridos ou alterados a lista sera atualizada com esse
							// metodo

	}

	private void initEditButtons() { // metodo para acrescentar um botão edit em cada linha da tabela
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Department obj) {
		// alerta para confirmar delete
		Optional<ButtonType> result = Alerts.showConfirmation("Confirtmation", "Are you sure to delete?");

		// A classe Optional retorna um objeto, e para acessar esse objeto é neecssario
		// usar metodo Get

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}

	}
}
