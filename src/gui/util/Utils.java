package gui.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

// o objetivo dessa classe é abrir uma janela a partir da janela principal

public class Utils {

	// para acessar o Stage aonde o event está
	public static Stage currentStage(ActionEvent event) { // o argumento vai receber um evento que o botão vai executar

		return (Stage) ((Node) event.getSource()).getScene().getWindow();

		/*
		 * getSource -> genérico
		 * 
		 * (Node) -> fazendo casting para Node
		 * 
		 * getScene() -> pega a cena
		 * 
		 * getWindow() -> pega a janela
		 * 
		 * getWindow() é uma superclasse do Stage e precisa ser convertida por isso a
		 * necessidade do downcast
		 * 
		 */
	}

	// para converter uma string que virá do TextFiel para inteiro
	public static Integer tryParseToInt(String str) {

		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	// para formatar a data
	public static <T> void formatTableColumnDate(TableColumn<T, Date> tableColumn, String format) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Date> cell = new TableCell<T, Date>() {
				private SimpleDateFormat sdf = new SimpleDateFormat(format);

				@Override
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						setText(sdf.format(item));
					}
				}
			};
			return cell;
		});
	}

	public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, int decimalPlaces) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Double> cell = new TableCell<T, Double>() {
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						Locale.setDefault(Locale.US);
						setText(String.format("%." + decimalPlaces + "f", item));
					}
				}
			};
			return cell;
		});
	}

}
