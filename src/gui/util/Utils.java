package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

// o objetivo dessa classe é abrir uma janela a partir da janela principal

public class Utils {

	//para acessar o Stage aonde o event está
	public static Stage currentStage(ActionEvent event) {  // o argumento vai receber um evento que o botão vai executar
		
		return (Stage) ((Node)event.getSource()).getScene().getWindow();
				
				/*
				 * getSource -> genérico
				 * 
				 * (Node) -> fazendo casting para Node
				 * 
				 * getScene() -> pega a cena
				 * 
				 * getWindow() -> pega a janela
				 * 
				 * getWindow() é uma superclasse do Stage e precisa ser convertida
				 * por isso a necessidade do downcast
				 * 
				 */
	}
	
	
	//para converter uma string que virá do TextFiel para inteiro
	public static Integer tryParseToInt(String str) {
	
		try{
			return Integer.parseInt(str); 
		}
		catch(NumberFormatException e) {
			return null;
		}
	}
	
}
