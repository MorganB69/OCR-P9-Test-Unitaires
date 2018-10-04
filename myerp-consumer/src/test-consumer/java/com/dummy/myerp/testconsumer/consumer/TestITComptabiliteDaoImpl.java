package com.dummy.myerp.testconsumer.consumer;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

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
		
		liste=dao.getListCompteComptable();
		assertTrue("Test taille de la liste attendu Compte Comptable", liste.size()==7);
		
		
	}
	
	/**
	 * Verification de l'obtention d'une ecriture comptable by ref
	 */
	@Test
	public void getEcritureComptableByRefTest() {
		String existe ="BQ-2016/00003";
		String nonexiste ="AC-2018/00001";
		EcritureComptable ecriture;
		try {
			assertTrue("test de la récupération d'une écriture comptable existante", dao.getEcritureComptableByRef(existe).getId()==-3);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
        try {
        	dao.getEcritureComptableByRef(nonexiste);
            fail("NotFound Exception attendu");
        } catch (NotFoundException e) {
            assertThat(e.getMessage(), is("EcritureComptable non trouvée : reference=" + nonexiste));
        }
	}
	
	

}
