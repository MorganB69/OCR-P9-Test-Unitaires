package com.dummy.myerp.testconsumer.consumer;


import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;

public class TestITComptabiliteDaoImpl {
	
	//CLASSE A TESTER
	ComptabiliteDaoImpl dao = ComptabiliteDaoImpl.getInstance();
	
	@Test
	public  void getListCompteComptableTest()  {
		List<CompteComptable>liste;
		CompteComptable compte = new CompteComptable(401,"Fournisseur");
		liste=dao.getListCompteComptable();
		assertTrue(liste.toString(), liste.size()==7);
		assertTrue(liste.get(0).equals(compte));
		
	}
	
	

}
