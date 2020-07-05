package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable; //Interface de inicialização do controlador.
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentServices;
import model.services.SellerServices;

public class SellerFormController implements Initializable {

	private Seller entity;
	private SellerServices services;
	private DepartmentServices departmentServices; // para conectar com os departamentos do banco de dados

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();// lista para receber um evento

	// variaveis para controlar a caixinha de diálogo

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthdate; // para data de nascimento

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorName; // caso tenha alguma mensagem de erro no preenchimento do nome

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<Department> obsList;

	@FXML
	public void onBtSaveAction(ActionEvent event) {

		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}

		if (services == null) {
			throw new IllegalStateException("Service was null");
		}

		try {
			entity = getFormData(); /*
									 * método que será responsavel por pegar os dados do formulário e instanciar o
									 * departamento no formulário
									 */
			services.saveOrUpdate(entity); // salvo no banco de dados

			notifyDataChangeListeners(); // notifica a lista que foi efetuado algum evento

			Utils.currentStage(event).close();
			/*
			 * currentsStage(event) -> pega o evento atual (janelinha de dialogo
			 * "new Seller") close -> fecha
			 */

		}

		// caso o nome esteja errado
		catch (ValidationException e) {
			setErrorsMessage(e.getErros());
		}

		catch (DbException e) {
			Alerts.showAlert("Erro ao Salvar", null, e.getMessage(), AlertType.ERROR);
		}

	}

	private void notifyDataChangeListeners() {

		for (DataChangeListener listeners : dataChangeListeners) {
			listeners.onDataChanged(); // emite o evento
		}

	}

	/*
	 * método que será responsavel por pegar os dados do formulário e instanciar o
	 * departamento no formulário
	 */
	private Seller getFormData() {
		Seller obj = new Seller();

		ValidationException exception = new ValidationException("Validation error");

		obj.setId(Utils.tryParseToInt(txtId.getText()));
		/*
		 * txtId.getText -> pega o texto do TextField Utils.tryParseToInt -> responsavel
		 * por converter esse texto para inteiro obj.setId -> insere o id no
		 * departamento obj
		 */

		/*
		 * esse metodo verifica a possibilidade do nome estar nulo e lança uma exceção
		 * que vai adicionar um erro no HashMap da classe ValidationException
		 */

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErrors("name", "Field can't be empty");
		}

		obj.setName(txtName.getText());

		// testando se há algum erro na lista de erros
		if (exception.getErros().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
		/*
		 * currentsStage(event) -> pega o evento atual (janelinha de dialogo
		 * "new Seller") close -> fecha
		 */
	}

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerServices services, DepartmentServices departmentServices) {
		this.services = services;
		this.departmentServices = departmentServices;
	}

	// metodo para objetos se inscreverem na lista "dataChangeListeners"
	// e add um evento

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	public void updateFormData() {

		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}

		txtId.setText(String.valueOf(entity.getId()));
		/*
		 * entity.getId() = retorna o ID do departamento converte para String, pois os
		 * valores que vão no TextField são Strings setText = passa o valor para o
		 * TextField
		 * 
		 */
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

		if (entity.getBirthDate() != null) {
			dpBirthdate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		/*
		 * no banco de dados a data esta armazenada corretamente.
		 * 
		 * porem é necessario apresentar a data para o usuario baseado no local aonde
		 * ele está
		 * 
		 * por isso necessario "LocalDate.ofInstant(instant, zone)"
		 * 
		 * necessario pegar o instante == entity.getBirthDate().toInstant()
		 * 
		 * zone = fuso horario
		 * 
		 * ZoneId.systemDefault() = pega o fuso horario da maquina do usuario
		 */
		if(entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
			//caso o departamento esteja nulo, seleciona o primeiro departamento da lista
		}
		
		comboBoxDepartment.setValue(entity.getDepartment());
		/*
		 * para carregar no comboBox o departamento associado ao vendedor
		 * 
		 */
	}

	/*
	 * este metodo é responsavel por carregar a lista de departamentos do banco de
	 * dados no observable list
	 * 
	 * 1º acessa os departamentos no banco de dados;(Banco de Dados) 2ºjava(list -->
	 * observableList (Java) 3º passa para o comboBox (JavaFX)
	 * 
	 */

	public void loadAssociatedObjects() {

		if (departmentServices == null) {
			throw new IllegalStateException("DepartmentServices was null");
		}
		List<Department> list = departmentServices.findAll();// lista que recebe os departamentos no banco de dados
		obsList = FXCollections.observableArrayList(list);// jogando os departamentos na observableList
		comboBoxDepartment.setItems(obsList);// associar a lista com a lista do Combo box
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes(); // para definir restrições nos TextFields

	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId); // para que a variavel txtId aceite apenas numeros inteiros
		Constraints.setTextFieldMaxLength(txtName, 70);// variavel txtName vai ter no maximo 70 caracteres
		Constraints.setTextFieldDouble(txtBaseSalary); // variavel aceita apenas Double
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthdate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	// metodo para pegar os erros que estão na exceção e lançar na tela

	private void setErrorsMessage(Map<String, String> errors) {

		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));// pegando a mensagem e passando pro "labelErrorName"

		}

	}

	//codigo para iniciar o combo box
	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
