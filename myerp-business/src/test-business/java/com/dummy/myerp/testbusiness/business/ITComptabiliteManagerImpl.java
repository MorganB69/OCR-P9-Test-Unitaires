package com.dummy.myerp.testbusiness.business;

import static org.junit.Assert.assertTrue;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"/bootstrapContext.xml"})
@ActiveProfiles(profiles="test")
@Transactional
public class ITComptabiliteManagerImpl {
	
	//CLASSE A TESTER
	private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();
	
	@Test
	public void insertEcritureTest() throws FunctionalException {
		
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
        
        manager.insertEcritureComptable(vEcritureComptable);
        
       
	}
	
	@Test
	public void checkRefTest() throws NotFoundException {
		 EcritureComptable vEcritureComptable;
	        vEcritureComptable = new EcritureComptable();
	        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
	        Calendar calendar = new GregorianCalendar(2016,1,1);
	        vEcritureComptable.setDate(calendar.getTime());
	        vEcritureComptable.setLibelle("Libelle");
	        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
	                                                                                 null, new BigDecimal(123),
	                                                                                 null));
	        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
	                                                                                 null, null,
	                                                                                 new BigDecimal(512)));




	        
	        this.manager.addReference(vEcritureComptable);
	        
	        SequenceEcritureComptable last = this.manager.getLastSequence(vEcritureComptable);
	        //VERIFICATION DERNIERE SEQUENCE + 1 PAR RAPPORT A CELLE EN DB
	        assertTrue("sequence existante :"+ vEcritureComptable.toString(), vEcritureComptable.getReference().equals("AC-2016/00041"));
	        assertTrue("sequence mise a jour en db sur existante", last.getAnnee()==2016&&last.getDerniereValeur()==41);
	        calendar.set(2018, 1, 1);
	        vEcritureComptable.setDate(calendar.getTime());
	        
	        this.manager.addReference(vEcritureComptable);	
	        //VERIF SUR UNE SEQUENCE NON EXISTENTE DONC REF MISE A 1
	        assertTrue("sequence non existante" + vEcritureComptable.toString(), vEcritureComptable.getReference().equals("AC-2018/00001"));
	        last = this.manager.getLastSequence(vEcritureComptable);

	        assertTrue("Séquence mise à jour en bd sur non existante",last.getAnnee()==2018&&last.getDerniereValeur()==1);
		
	}

}
