package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable; //Interface de inicialização do controlador.
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentServices;

public class DepartmentFormController implements Initializable {

	private Department entity;
	private DepartmentServices services;
	
	private List <DataChangeListener> dataChangeListeners = new ArrayList<>();//lista para receber um evento

	// variaveis para controlar a caixinha de diálogo

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName; // caso tenha alguma mensagem de erro no preenchimento do nome

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		if(services == null) {
			throw new IllegalStateException("Service was null");
		}
		
		try {
		entity = getFormData(); /*
								 * método que será responsavel por pegar os dados do formulário e instanciar o
								 * departamento no formulário
								 */
		services.saveOrUpdate(entity); //salvo no banco de dados
		
		notifyDataChangeListeners();
		
		Utils.currentStage(event).close();
		/*
		 * currentsStage(event) -> pega o evento atual (janelinha de dialogo "new Department")
		 * close -> fecha
		 */
		
		
		}
		catch (DbException e){
			Alerts.showAlert("Erro ao Salvar", null, e.getMessage(), AlertType.ERROR);		}

	}

	private void notifyDataChangeListeners() {
		
		for(DataChangeListener listeners : dataChangeListeners) {
			listeners.onDataChanged(); // emite o evento
		}
		
	}

	/*
	 * método que será responsavel por pegar os dados do formulário e instanciar o
	 * departamento no formulário
	 */
	private Department getFormData() {
		Department obj = new Department();
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		/*
		 * txtId.getText -> pega o texto do TextField Utils.tryParseToInt -> responsavel
		 * por converter esse texto para inteiro obj.setId -> insere o id no
		 * departamento obj
		 */
		obj.setName(txtName.getText());

		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
		/*
		 * currentsStage(event) -> pega o evento atual (janelinha de dialogo "new Department")
		 * close -> fecha
		 */
	}

	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	public void setDepartmentServices(DepartmentServices services) {
		this.services = services;
	}

	//metodo para objetos se inscreverem na lista "dataChangeListeners"
	//e add um evento
	
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
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes(); // para definir restrições nos TextFields

	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId); // para que a variavel txtId aceite apenas numeros inteiros
		Constraints.setTextFieldMaxLength(txtName, 30);// variavel txtName vai ter no maximo 30 caracteres

	}

}
