package com.dummy.myerp.testconsumer.consumer;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"/com/dummy/myerp/consumer/applicationContext.xml"})
@ActiveProfiles(profiles="test")
public class TestITComptabiliteDaoImpl {
	
	//CLASSE A TESTER
	
	ComptabiliteDaoImpl dao = ComptabiliteDaoImpl.getInstance();

	
	/**
	 *Vérification que la classe à tester est bien récupérée donc que le context est chargé 
	 */
	@Test
	public void testDaoNull() {
		assertNotNull(dao);

	}
	
	/**
	 *Vérification de l'obtention de la liste des comptes comptables 
	 */
	@Test
	public  void getListCompteComptableTest()  {
		System.out.println(dao.toString());
		List<CompteComptable>liste;
		CompteComptable compte = new CompteComptable(401,"Fournisseurs");
		liste=dao.getListCompteComptable();
		assertTrue("Test taille de la liste attendu Compte Comptable", liste.size()==7);
		assertTrue("Test du premier élément liste Compte Comptable",liste.get(0).equals(compte));
		
	}
	
	

}
