package com.dummy.myerp.business.impl.manager;

import static org.junit.Assert.assertTrue;
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

@RunWith(MockitoJUnitRunner.class)
public class ComptabiliteManagerImplTest {

	//CLASSE A TESTER
	private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();
    
	//MOCK DU PROXY DAO
	private DaoProxy daoProxyMock= mock(DaoProxy.class, withSettings().verboseLogging());
	
	//MOCK DU TRANSACTION MANAGER
	private TransactionManager transactionManagerMock= mock(TransactionManager.class, withSettings().verboseLogging());
	
	//MOCK DE COMPTADAO
	private ComptabiliteDaoImpl comptaDaoMock = mock(ComptabiliteDaoImpl.class, withSettings().verboseLogging());
    
	
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
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(123)));
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
     * Verification de l'ajout d'une reference
     */
    @Test
    public void checkAdd() {
        EcritureComptable pEcritureComptable;
        pEcritureComptable = new EcritureComptable();
        pEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        pEcritureComptable.setDate(new Date());
        pEcritureComptable.setLibelle("Libelle");
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        pEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(123)));
        SequenceEcritureComptable vSeq1= new SequenceEcritureComptable();
        vSeq1.setAnnee(2018);
        vSeq1.setDerniereValeur(15);


        SequenceEcritureComptable vSeq2= new SequenceEcritureComptable();
        vSeq2=null;

        


        
        when(this.comptaDaoMock.getLastSequence(pEcritureComptable)).thenReturn(vSeq1); 
        doNothing().when(this.comptaDaoMock).insertSequence(Mockito.any(),Mockito.any());
        doNothing().when(this.comptaDaoMock).updateSequence(Mockito.any(),Mockito.any());

        
        this.manager.addReference(pEcritureComptable);
        


        assertTrue("sequence existante :"+ pEcritureComptable.toString(), pEcritureComptable.getReference().equals("AC-2018/00016"));
        
        
        when(this.comptaDaoMock.getLastSequence(pEcritureComptable)).thenReturn(vSeq2);                
        this.manager.addReference(pEcritureComptable);	
        assertTrue("sequence non existante" + pEcritureComptable.toString(), pEcritureComptable.getReference().equals("AC-2018/00001"));
        
       
    
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

    

    

    
}
