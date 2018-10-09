package com.dummy.myerp.business.impl.manager;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class ComptabiliteManagerImplTest {

	//CLASSE A TESTER
	private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();
    
	//MOCK DU PROXY DAO
	private DaoProxy daoProxyMock= mock(DaoProxy.class);
	
	//MOCK DU TRANSACTION MANAGER
	private TransactionManager transactionManagerMock= mock(TransactionManager.class);
	
	//MOCK DE COMPTADAO
	private ComptabiliteDaoImpl comptaDaoMock = mock(ComptabiliteDaoImpl.class);
    
	
	/**
	 * Configuration du test : Mock de la DAO et transaction manager
	 */
	@Before
	public void setUp() {
		ComptabiliteManagerImpl.setDaoProxy(this.daoProxyMock);
		ComptabiliteManagerImpl.setTransactionManager(this.transactionManagerMock);
		when(this.daoProxyMock.getComptabiliteDao()).thenReturn(this.comptaDaoMock); 
		when(this.transactionManagerMock.beginTransactionMyERP()).thenReturn(null);
		 

	        
	}


    /**
     * Test d'une écriture comptable correcte avec référence
     * @throws Exception
     */
    @Test
    public void checkEcritureComptableUnit() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2018,1,1);
        vEcritureComptable.setDate(calendar.getTime());
        vEcritureComptable.setReference("AC-2018/00001");
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal("123.42"),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal("123.42")));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    /**
     * Vérification que les contraintes renvoient une erreur sur écriture vide
     * @throws Exception
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    /**
     * Vérification qu'une ecriture non equilibree renvoie une exception
     * @throws Exception
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(1234)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    /**
     * Verification qu'une ecriture avec deux lignes au debit renvoie une exception
     * @throws Exception
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    /**
     * Verification avec un montant negatif sur une ligne d ecriture comptable possible
     * @throws Exception
     */
    @Test
    public void checkEcritureComptableUnitRG4() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2018,1,1);
        vEcritureComptable.setDate(calendar.getTime());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal("-12"),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal("15"),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal("3")));
      
        
        vEcritureComptable.setReference("AC-2018/00123");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    


    /**
     * Verification de l'ajout d'une reference
     * @throws NotFoundException 
     */
    @Test
    public void checkAdd() throws NotFoundException {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2018,1,1);
        vEcritureComptable.setDate(calendar.getTime());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(123)));
        SequenceEcritureComptable vSeq1= new SequenceEcritureComptable();
        vSeq1.setAnnee(2018);
        vSeq1.setDerniereValeur(15);




        


        
        when(this.comptaDaoMock.getLastSequence(vEcritureComptable)).thenReturn(vSeq1); 
        doNothing().when(this.comptaDaoMock).insertSequence(Mockito.any(),Mockito.any());
        doNothing().when(this.comptaDaoMock).updateSequence(Mockito.any(),Mockito.any());

        
        this.manager.addReference(vEcritureComptable);
        


        assertTrue("sequence existante :"+ vEcritureComptable.toString(), vEcritureComptable.getReference().equals("AC-2018/00016"));
        
        
        when(this.comptaDaoMock.getLastSequence(vEcritureComptable)).thenThrow(NotFoundException.class);                
        this.manager.addReference(vEcritureComptable);	
        assertTrue("sequence non existante" + vEcritureComptable.toString(), vEcritureComptable.getReference().equals("AC-2018/00001"));
        
       
    
    }
    
    /**
     * Verifie qu'une exception est lancee si le code journal correspond pas a la ref
     * @throws Exception
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5Journal() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(123)));
        
        vEcritureComptable.setReference("BQ-2018/00123");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    /**
     * Verifie qu'une exception est lancee si la date de l'ecriture correspond pas a la ref
     * @throws Exception
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5Date() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2018,1,1);
        vEcritureComptable.setDate(calendar.getTime());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(123)));
        
        vEcritureComptable.setReference("AC-2017/00123");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    /**
     * Verifie qu'une exception est lancee si une ligne d'ecriture a plus de 2 chiffres apres la virgule
     * @throws Exception
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG7() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2018,1,1);
        vEcritureComptable.setDate(calendar.getTime());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal("123.124"),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal("123.124")));
        
        
        vEcritureComptable.setReference("AC-2018/00123");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    
    /**
     * Verifie qu'une exception est lancee si la référence n'est pas unique
     * @throws Exception
     */
    @Test
    public void checkEcritureComptableContextRG6() throws Exception {
        //ECRITURE COMPTABLE A TESTER
    	EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2018,1,1);
        vEcritureComptable.setDate(calendar.getTime());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal("123.124"),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal("123.124")));
        
        vEcritureComptable.setReference("AC-2018/00123");
        
        //ECRITURE DE LA DAO MOCKEE AVEC LA MEME REF
        EcritureComptable daoEcritureComptable;
        daoEcritureComptable = new EcritureComptable();
        daoEcritureComptable.setId(12);
        daoEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar2 = new GregorianCalendar(2018,1,1);
        daoEcritureComptable.setDate(calendar2.getTime());
        daoEcritureComptable.setLibelle("Libelle");
        daoEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal("123.124"),
                                                                                 null));
        daoEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal("123.124")));
        
        daoEcritureComptable.setReference("AC-2018/00123");
        
        when(this.comptaDaoMock.getEcritureComptableByRef(vEcritureComptable.getReference())).thenReturn(daoEcritureComptable); 
        
        
        //VERIFICATION QU UNE FUNCTIONAL EXCEPTION EST LEVEE SI MEME REF ET NOUVELLE ECRITURE(ID : null)
        try {
        	manager.checkEcritureComptableContext(vEcritureComptable);
            fail("Functional Exception attendu");
        } catch (FunctionalException e) {
            assertThat(e.getMessage(), is("Une autre écriture comptable existe déjà avec la même référence."));
        }
        
        //VERIFICATION QU UNE FUNCTIONAL EXCEPTION EST LEVEE SI MEME REF ET ID DIFFERENTS
        vEcritureComptable.setId(13);
        try {
        	manager.checkEcritureComptableContext(vEcritureComptable);
            fail("Functional Exception attendu");
        } catch (FunctionalException e) {
            assertThat(e.getMessage(), is("Une autre écriture comptable existe déjà avec la même référence."));
        }
        
        //VERIFICATION QU IL N Y A PAS D ERREUR SI L ID EST LE MEME
        vEcritureComptable.setId(12);
        manager.checkEcritureComptableContext(vEcritureComptable);
        
        //VERIFICATION QUE C EST BON SI REF NON TROUVEE ET NOUVELLE ECRITURE
        daoEcritureComptable=null;
        when(this.comptaDaoMock.getEcritureComptableByRef(vEcritureComptable.getReference())).thenThrow(NotFoundException.class); 
        
        vEcritureComptable.setId(0);
        manager.checkEcritureComptableContext(vEcritureComptable);
        
        
       
    }


    

    

    
}
