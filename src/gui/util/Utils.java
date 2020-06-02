package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

// o objetivo dessa classe � abrir uma janela a partir da janela principal

public class Utils {

	//para acessar o Stage aonde o event est�
	public static Stage currentStage(ActionEvent event) {  // o argumento vai receber um evento que o bot�o vai executar
		
		return (Stage) ((Node)event.getSource()).getScene().getWindow();
				
				/*
				 * getSource -> gen�rico
				 * 
				 * (Node) -> fazendo casting para Node
				 * 
				 * getScene() -> pega a cena
				 * 
				 * getWindow() -> pega a janela
				 * 
				 * getWindow() � uma superclasse do Stage e precisa ser convertida
				 * por isso a necessidade do downcast
				 * 
				 */
	}
	
	
	//para converter uma string que vir� do TextFiel para inteiro
	public static Integer tryParseToInt(String str) {
	
		try{
			return Integer.parseInt(str); 
		}
		catch(NumberFormatException e) {
			return null;
		}
	}
	
}
