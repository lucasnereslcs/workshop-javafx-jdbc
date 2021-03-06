package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentServices {

	// para capturar os dados do banco de dados

	private DepartmentDao dao = DaoFactory.createDepartmentDao();

	public List<Department> findAll() {

		return dao.findAll();

	}
	
	
	public void saveOrUpdate(Department obj) {
		
		if(obj.getId()==null) {
			dao.insert(obj);  //insere novo departamento no banco de dados
		}
		
		else {
			dao.update(obj); //departamento ja existente, basta apenas atualiza-lo
		}
	}
	
	//para remover o departamento
	public void remove (Department obj) {
		dao.deleteById(obj.getId());
	}
}
