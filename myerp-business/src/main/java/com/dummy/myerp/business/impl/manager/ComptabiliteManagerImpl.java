package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ====================
	

    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {
    }


    // ==================== Getters/Setters ====================
    @Override
    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();
    }


    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }

    /**
     * 
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }
    
    /* (non-Javadoc)
     * {@inheritDoc}
     * */
    @Override
    public SequenceEcritureComptable getLastSequence(EcritureComptable pEcritureComptable) throws NotFoundException {
    	return getDaoProxy().getComptabiliteDao().getLastSequence(pEcritureComptable);
    	
    }
    


    /**
     * {@inheritDoc}		
     */
    // TODO à tester
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) {
        // TODO à implémenter
        // Bien se réferer à la JavaDoc de cette méthode !
        /* Le principe :
                1.  Remonter depuis la persitance la dernière valeur de la séquence du journal pour l'année de l'écriture
                    (table sequence_ecriture_comptable)
                2.  * S'il n'y a aucun enregistrement pour le journal pour l'année concernée :
                        1. Utiliser le numéro 1.
                    * Sinon :
                        1. Utiliser la dernière valeur + 1
                3.  Mettre à jour la référence de l'écriture avec la référence calculée (RG_Compta_5)
                4.  Enregistrer (insert/update) la valeur de la séquence en persitance
                    (table sequence_ecriture_comptable)
         */
    	
    	//Dernière séquence à récupérer
    	SequenceEcritureComptable vSequenceEcritureComptable;
    	
    	//Séquence à update ou insérer en DAO
    	SequenceEcritureComptable vSequenceToInsert;
    	
    	vSequenceEcritureComptable = new SequenceEcritureComptable();   	
    	vSequenceToInsert = new SequenceEcritureComptable();
    	
    	
    	//Calcul de la dernière séquence
    	int derniereSequence;
    	
    	//Récupération de la dernière séquence
    	try {
			vSequenceEcritureComptable= getLastSequence(pEcritureComptable);
			derniereSequence=vSequenceEcritureComptable.getDerniereValeur()+1;
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			derniereSequence=1;
			vSequenceEcritureComptable=null;
		}
    	
    	

    	
    	//Composition de la référence
    	String ref = "";
    	//Rajout du code du journal
    	ref+=pEcritureComptable.getJournal().getCode();
    	ref+="-";
    	//Rajout de la date
    	 	Calendar calendar = Calendar.getInstance();
    	 	calendar.setTime(pEcritureComptable.getDate());	     
    	ref+=String.valueOf(calendar.get(Calendar.YEAR));
    	ref+="/";
    	//Rajout de dernière séquence
    	ref+=String.format("%05d", derniereSequence);
    	
    	
    	//Mise à jour de la référence
    	pEcritureComptable.setReference(ref);
    	
    	
    	//Enregistrement de la séquence en base de données
    	
    	//Si la séquence n'existait pas, on paramètre la nouvelle puis on l'insert en bd
    	if(vSequenceEcritureComptable==null) {
    		vSequenceToInsert.setAnnee(calendar.get(Calendar.YEAR));
    		vSequenceToInsert.setDerniereValeur(derniereSequence);
    		
    		insertSequence(vSequenceToInsert,pEcritureComptable.getJournal().getCode());
    	}
    	//Sinon on part de la séquence récupérée, on modifie et l'update.
    	else {
    		vSequenceToInsert=vSequenceEcritureComptable;
    		vSequenceToInsert.setDerniereValeur(derniereSequence);
    		
    		try {
				updateSequence(vSequenceToInsert, pEcritureComptable.getJournal().getCode());
			} catch (FunctionalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	

    	
    	
    	
    }




	/**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    // TODO tests à compléter
    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
    	
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
                                          new ConstraintViolationException(
                                              "L'écriture comptable ne respecte pas les contraintes de validation",
                                              vViolations));
        }
        
        

        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }

        // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
        int vNbrCredit = 0;
        int vNbrDebit = 0;
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrCredit++;
            }
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrDebit++;
            }
        }
        // On test le nombre de lignes car si l'écriture à une seule ligne
        //      avec un montant au débit et un montant au crédit ce n'est pas valable
        if (pEcritureComptable.getListLigneEcriture().size() < 2
            || vNbrCredit < 1
            || vNbrDebit < 1) {
            throw new FunctionalException(
                "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        }

        // TODO ===== RG_Compta_5 : Format et contenu de la référence
        // vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal...
        if(pEcritureComptable.getReference()!=null) {
        String journal=new String();
        String date=new String();
        String refToTest=pEcritureComptable.getReference();
        journal=refToTest.substring(0, refToTest.indexOf("-"));
        date= refToTest.substring(refToTest.indexOf("-")+1, refToTest.indexOf("-")+5);
        System.out.println(journal + " "+date);
	 	
        //Récupération de l'année
        Calendar calendar = Calendar.getInstance();
	 	String dateToTest=new String();
	 	calendar.setTime(pEcritureComptable.getDate());	     
	 	dateToTest=String.valueOf(calendar.get(Calendar.YEAR));
	 	
	 	if(!dateToTest.equals(date))throw new FunctionalException(
                "La date de la référence ne correspond pas à la date de l'écriture.");
        
    	
	 	if(!journal.equals(pEcritureComptable.getJournal().getCode()))throw new FunctionalException(
	            "Le code du journal de la référence ne correspond pas au code du journal de l'écriture.");
	    
        }
        

		
    
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(
                    pEcritureComptable.getReference());
               

                // Si l'écriture à vérifier est une nouvelle écriture (id == null),
                // ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                // c'est qu'il y a déjà une autre écriture avec la même référence
                if (pEcritureComptable.getId() == null
                    || !pEcritureComptable.getId().equals(vECRef.getId())) {
                    throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
    
    
    @Override
    public void insertSequence(SequenceEcritureComptable vSequenceToInsert, String code) {
    	 TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
         try {
             getDaoProxy().getComptabiliteDao().insertSequence(vSequenceToInsert,code);
             getTransactionManager().commitMyERP(vTS);
             vTS = null;
         } finally {
             getTransactionManager().rollbackMyERP(vTS);
         }
		
	}
    
    @Override
    public void updateSequence(SequenceEcritureComptable vSequenceToInsert, String code) throws FunctionalException {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateSequence(vSequenceToInsert,code);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
    
    public String testString() {
    	return	getDaoProxy().getComptabiliteDao().testString();
    }
    }
