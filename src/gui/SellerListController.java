package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import model.entities.Seller;
import model.services.DepartmentServices;
import model.services.SellerServices;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerServices service;

	@FXML
	private TableView<Seller> tableViewSeller;

	@FXML
	private TableColumn<Seller, Integer> tableColumnId; // Coluna ID

	@FXML
	private TableColumn<Seller, String> tableColumnName; // Coluna Name

	@FXML
	private TableColumn<Seller, String> tableColumnEmail; // Coluna email
	
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate; // Coluna dataAniversario
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary; // Coluna Salario
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT; // coluna para editar

	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE; // coluna para remover

	@FXML
	private Button btNew;

	// para inserir a lista na tela na coluna Seller

	private ObservableList<Seller> obsList;

	// método para o botão
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller(); // novo vendedor para tela do formulário
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		initializableNodes(); // método para iniciar algum componente na tela

	}

	private void initializableNodes() {

		// metodos para iniciar algum comportamento das colunas

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));	
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");//para data aparecer formatada
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));		
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2); // para formatar o salario
		
		// Métodos para fazer a coluna acompanhar a tela da aplicação
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
	}

	// injetando dependencia do SellerServices
	public void setSellerServices(SellerServices service) {
		this.service = service;
	}

	/*
	 * Este Método:
	 * 
	 * acessa o serviço carrega o vendedor e joga o vendedor na
	 * ObservableList
	 * 
	 */

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Servico está nulo");
		}

		List<Seller> list = service.findAll(); // Para carregar a lista de vendedor
		obsList = FXCollections.observableArrayList(list); // carregando a lista no Observable List;
		tableViewSeller.setItems(obsList);// Para carregar os itens na TableView
		initEditButtons(); // metodo para acrescentar um botão edit em cada linha da tabela
		initRemoveButtons(); //método para acrescentar um botão de remover em cada linha da tabela
	}

	// Quando criamos uma janela de dialogo devemos dizer o stage que criou a janela
	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
		// O argumento recebe o Stage da Janela que abriu a janelinha
		// O absoluteName recebe o nome da View que vai carregar
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName)); // para carregar uma tela
			Pane pane = loader.load();

			// Para injetar o vendedor na tela do formulário:

			SellerFormController controller = loader.getController(); // pegou o controlador da tela do formulário
			controller.setSeller(obj);// injetor o vendedor no controlador
			controller.setServices(new SellerServices(), new DepartmentServices());
			controller.loadAssociatedObjects(); //para carregar os departamentos do banco de dados e deixar no controller
			controller.subscribeDataChangeListener(this); // inscrevendo para receber o evento do formulário

			controller.updateFormData(); // para carregar os dados do vendedor no formulário

			// Para carregar uma nova janelinha de dialogo na frente do Stage, eu preciso
			// declarar um novo Stage

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller Data: "); // Para definir um nome para o titulo da tela
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
	 * Esse método recebe o evento (No caso, o recebimento de um novo vendedor)
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
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
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

	private void removeEntity(Seller obj) {
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
