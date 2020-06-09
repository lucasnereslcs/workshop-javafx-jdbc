package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerServices {

	// para capturar os dados do banco de dados

	private SellerDao dao = DaoFactory.createSellerDao();

	public List<Seller> findAll() {

		return dao.findAll();

	}
	
	
	public void saveOrUpdate(Seller obj) {
		
		if(obj.getId()==null) {
			dao.insert(obj);  //insere novo vendedor no banco de dados
		}
		
		else {
			dao.update(obj); //vendedor ja existente, basta apenas atualiza-lo
		}
	}
	
	//para remover o vendedor
	public void remove (Seller obj) {
		dao.deleteById(obj.getId());
	}
}
