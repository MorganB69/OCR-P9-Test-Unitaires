package com.dummy.myerp.testconsumer.consumer;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"/com/dummy/myerp/consumer/applicationContext.xml"})
@ActiveProfiles(profiles="test")
@Transactional
public class ITComptabiliteDaoImpl {
	
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
		List<CompteComptable>liste;

		
		liste=dao.getListCompteComptable();

		assertTrue("Test taille de la liste attendu Compte Comptable", liste.size()==7);
		assertTrue("Test si le compte comptable fournisseur est bien présent",liste.stream().filter(o -> o.getNumero().equals(401)).findFirst().isPresent());
		
		
	}
	
	/**
	 *Vérification de l'obtention de la liste des journaux comptable
	 */
	@Test
	public  void getListJournalComptableTest()  {
		List<JournalComptable>liste;

		
		liste=dao.getListJournalComptable();

		assertTrue("Test taille de la liste attendu Compte Comptable", liste.size()==4);
		assertTrue("Test si le journal fournisseur est bien présent",liste.stream().filter(o -> o.getCode().equals("AC")).findFirst().isPresent());
		
		
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
	
	@Test
	public void getLastSequenceTest() throws NotFoundException {
		EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2016,1,1);
        vEcritureComptable.setDate(calendar.getTime());
        
        SequenceEcritureComptable last = dao.getLastSequence(vEcritureComptable);
        
        //Test sur une séquence déjà existante  
        assertTrue("Vérification de la dernière séquence existente", last.getDerniereValeur()==40);
        
        calendar.set(2017,1,1);
        vEcritureComptable.setDate(calendar.getTime());
        
        //Test sur une nouvelle séquence
      
        try {
        	 last= dao.getLastSequence(vEcritureComptable);
            fail("NotFound Exception attendu");
        } catch (NotFoundException e) {
            assertThat(e.getMessage(), is("Séquence non existante"));
        }}
	
	
	@Test
	public void insertEcritureTest() throws FunctionalException, NotFoundException {
		
		EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2018,1,1);
        vEcritureComptable.setDate(calendar.getTime());
        vEcritureComptable.setReference("AC-2018/00001");
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                                                                                 null, new BigDecimal("123.42"),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(512),
                                                                                 null, null,
                                                                                 new BigDecimal("123.42")));
        
        dao.insertEcritureComptable(vEcritureComptable);
        
        EcritureComptable testEcriture;
        
        testEcriture = dao.getEcritureComptableByRef("AC-2018/00001");
        
        assertNotNull(testEcriture);
       
	}
}
