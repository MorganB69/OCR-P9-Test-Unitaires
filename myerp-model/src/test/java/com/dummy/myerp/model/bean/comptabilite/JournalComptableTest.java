package com.dummy.myerp.model.bean.comptabilite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class JournalComptableTest {
	
	@Test
	public void getByCodeTest() {
		
		//Creation de journaux comptables pour l'exemple
		JournalComptable banque = new JournalComptable("bq","banque");
		JournalComptable achat = new JournalComptable("ach", "achat");
		JournalComptable vente = new JournalComptable ("vte","vente");
		
		//Journal Comptable que l'on test
		JournalComptable journalTest = null;

		//Ajout des comptes test a une liste
		List<JournalComptable> testList = new ArrayList<JournalComptable>();
			
		testList.addAll(Arrays.asList(banque,achat,vente));
		
		//TEST
		journalTest=JournalComptable.getByCode(testList, "bq");
		Assert.assertTrue(journalTest.toString(),journalTest.getCode()=="bq"&&journalTest.getLibelle().equals("banque"));


		
	}
}
