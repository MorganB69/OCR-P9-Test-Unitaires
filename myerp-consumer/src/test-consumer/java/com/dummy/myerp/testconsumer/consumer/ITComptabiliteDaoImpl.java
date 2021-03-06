package com.dummy.myerp.testconsumer.consumer;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
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
	 * Récupération d'une écriture par ID
	 * @throws NotFoundException
	 */
	@Test 
	public void getEcritureComptableByIdTest() throws NotFoundException {
		EcritureComptable testEcriture;
		Integer pId=-1;
		//RECUPERATION ECRITURE EXISTENTE
		testEcriture = dao.getEcritureComptable(pId);
		
		assertNotNull("Verification que l'ecriture n'est pas nulle",testEcriture);
		assertTrue("Verification de la reference de l ecriture",testEcriture.getReference().equals("AC-2016/00001"));
		assertFalse(testEcriture.getLibelle()=="");
		
		assertTrue("Verification que la liste des lignes d'écriture a été chargée", testEcriture.getListLigneEcriture().size()==3);
		
		//RECUPERATION ECRITURE NON EXISTENTE
		pId=12;
		 try {
			 
			 testEcriture = dao.getEcritureComptable(pId);
	            fail("NotFound Exception attendu");
	        } catch (NotFoundException e) {
	            assertThat(e.getMessage(), is("EcritureComptable non trouvée : id=" + pId));
	        }
		
	}
	
	/**
	 * Vérification de l'obtention de la liste des écritures comptables
	 */
	@Test
	public void getListEcritureComptableTest() {
		List<EcritureComptable>liste;

		
		liste=dao.getListEcritureComptable();

		assertTrue("Test taille de la liste attendu Ecritures Comptables", liste.size()==5);
		assertTrue("Test si une ecriture test est bien présente",liste.stream().filter(o -> o.getReference().equals("BQ-2016/00005")).findFirst().isPresent());
		
		
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
	
	/**
	 * Vérification de l'update d'une ecriture comptable en bd
	 * @throws NotFoundException
	 */
	@Test
	public void updateEcritureComptableTest () throws NotFoundException {
		
		EcritureComptable testEcritureToUpdate;
		testEcritureToUpdate = dao.getEcritureComptable(-1);
		testEcritureToUpdate.setReference("AC-2018/00001");
		
		dao.updateEcritureComptable(testEcritureToUpdate);
		
		EcritureComptable testEcritureUpdated;
		
		testEcritureUpdated = dao.getEcritureComptableByRef("AC-2018/00001");
		
		assertNotNull("vérification que l'ecriture mise à jour est trouvée avec la nouvelle ref",testEcritureUpdated);
		
		
		
	}
	
	/**
	 *Verification d'une suppression d'ecriture en bd 
	 * @throws NotFoundException 
	 */
	@Test
	public void deleteEcritureComptableTest() throws NotFoundException {
		Integer pId=-1;
		EcritureComptable testEcriture;
		testEcriture = dao.getEcritureComptable(pId);
		dao.deleteEcritureComptable(pId);

		
		//Verification que l'ecriture n'existe plus
		try {
			 
			 testEcriture = dao.getEcritureComptable(pId);
	            fail("NotFound Exception attendu");
	        } catch (NotFoundException e) {
	            assertThat(e.getMessage(), is("EcritureComptable non trouvée : id=" + pId));
	        }
		
		//Clear de la liste des lignes d'ecritures
		testEcriture.getListLigneEcriture().clear();
		
		
		dao.loadListLigneEcriture(testEcriture);
		
		//Verification que les lignes d'ecritures ont été supprimées
		
		assertTrue(testEcriture.getListLigneEcriture().isEmpty());
		
		
	
	}
	
	@Test
	public void insertSequenceTest() throws NotFoundException {
		SequenceEcritureComptable sequence = new SequenceEcritureComptable(2018, 42);
		dao.insertSequence(sequence, "AC");
		
		EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2018,1,1);
        vEcritureComptable.setDate(calendar.getTime());
        
        SequenceEcritureComptable last = dao.getLastSequence(vEcritureComptable);
        assertTrue("Verif de la dernière séquence insérée",last.getDerniereValeur()==42);
        assertFalse(last.getAnnee()==2016);
		
			}
	
	@Test
	public void updateSequenceTest() throws NotFoundException {
		EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2016,1,1);
        vEcritureComptable.setDate(calendar.getTime());
        
        SequenceEcritureComptable last = dao.getLastSequence(vEcritureComptable);
        
        last.setDerniereValeur(45);
        
        dao.updateSequence(last, "AC");
        
        last= dao.getLastSequence(vEcritureComptable);
        
        assertTrue("verif que la séquence a été mise à jour",last.getDerniereValeur()==45);
        assertFalse(last.getAnnee()==2018);
	}
	
	@Test
	public void getListEcritureComptableByCompteTest() {
		
		List<LigneEcritureComptable>liste;
		
		
		liste=dao.getListLigneEcritureComptableByCompte(512);

		assertTrue("Test taille de la liste attendu Ligne Ecriture Comptable", liste.size()==2);
		liste.clear();
		
		liste=dao.getListLigneEcritureComptableByCompte(411);
		assertTrue("Test taille de la liste attendu Ligne Ecriture Comptable", liste.size()==3);
		
		
	}
	

}
