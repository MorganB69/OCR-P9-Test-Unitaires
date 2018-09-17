package com.dummy.myerp.model.bean.comptabilite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CompteComptableTest {
	
@Test
public void getByNumeroTest() {
	
	//Creation de comptes comptables pour l'exemple
	CompteComptable banque = new CompteComptable(512,"banque");
	CompteComptable client = new CompteComptable(411, "client");
	CompteComptable fournisseur = new CompteComptable (401,"fournisseur");
	
	//Compte Comptable que l'on test
	CompteComptable compteTest = null;

	//Ajout des comptes test a une liste
	List<CompteComptable> testList = new ArrayList<CompteComptable>();
		
	testList.addAll(Arrays.asList(banque,client,fournisseur));
	
	//TEST
	compteTest=CompteComptable.getByNumero(testList, 512);
	Assert.assertTrue(compteTest.toString(),compteTest.getNumero()==512&&compteTest.getLibelle().equals("banque"));


	
}
}
