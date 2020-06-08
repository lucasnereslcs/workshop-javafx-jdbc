package model.exceptions;

import java.util.HashMap;
import java.util.Map;

//classe com objetivo de lançar exceção quando algum dado invalido for digitado
public class ValidationException extends RuntimeException {

	
	private static final long serialVersionUID = 1L;
	
	//para guardar/carregar erros na tela do formulario
	//primeiro nome do campo, segundo erro correspondente
	private Map <String, String> errors = new HashMap<>();

	public ValidationException (String msg) {
		super(msg);
	}
	
	public Map <String, String> getErros(){
		return errors;
	}
	
	//para adicionar erro
	public void addErrors(String fieldName, String errorMessage) {
		
		errors.put(fieldName, errorMessage);
		
	}
}
