package org.guanzon.cas.client.services;

import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.cas.client.model.Model_AP_Client_Bank_Account;
import org.guanzon.cas.client.model.Model_AP_Client_Ledger;
import org.guanzon.cas.client.model.Model_AP_Client_Master;
import org.guanzon.cas.client.model.Model_Account_Client_Accreditation;
import org.guanzon.cas.client.model.Model_Client_Address;
import org.guanzon.cas.client.model.Model_Client_Employment;
import org.guanzon.cas.client.model.Model_Client_Institution_Contact;
import org.guanzon.cas.client.model.Model_Client_Mail;
import org.guanzon.cas.client.model.Model_Client_Master;
import org.guanzon.cas.client.model.Model_Client_Mobile;
import org.guanzon.cas.client.model.Model_Client_Social_Media;
import org.guanzon.cas.client.model.Model_Corporate_Role;
import ph.com.guanzongroup.cas.cashflow.model.Model_Payee;

public class ClientModels {
    
    private final GRiderCAS poGRider;

    private Model_Client_Master poClientMaster;
    private Model_Client_Address poClientAddress;
    private Model_Client_Employment poClientEmployment;
    private Model_Client_Mobile poClientMobile;
    private Model_Client_Mail poClientMail;
    private Model_Client_Social_Media poClientSocmed;
    private Model_Client_Institution_Contact poClientInstitutionContact;
    private Model_Corporate_Role poRole;
    private Model_Account_Client_Accreditation poClientAccreditation;
    private Model_AP_Client_Master poAPClientMaster;
    private Model_AP_Client_Ledger poAPClientLedger;
    private Model_AP_Client_Bank_Account poAPClientBankAccount;
    private Model_Payee poPayee;
    
    public ClientModels(GRiderCAS applicationDriver) {
        poGRider = applicationDriver;
    }

    public Model_Client_Master ClientMaster() {
        if (poGRider == null) {
            System.err.println("ClientModel.ClientMaster: Application driver is not set.");
            return null;
        }

        if (poClientMaster == null) {
            poClientMaster = new Model_Client_Master();
            poClientMaster.setApplicationDriver(poGRider);
            poClientMaster.setXML("Model_Client_Master");
            poClientMaster.setTableName("Client_Master");
            poClientMaster.initialize();
        }

        return poClientMaster;
    }
    
    public Model_Client_Address ClientAddress() {
        if (poGRider == null) {
            System.err.println("ClientModel.ClientAddress: Application driver is not set.");
            return null;
        }

        if (poClientAddress == null) {
            poClientAddress = new Model_Client_Address();
            poClientAddress.setApplicationDriver(poGRider);
            poClientAddress.setXML("Model_Client_Address");
            poClientAddress.setTableName("Client_Address");
            poClientAddress.initialize();
        }

        return poClientAddress;
    }

    public Model_Client_Employment ClientEmployment() {
        if (poGRider == null) {
            System.err.println("ClientModel.ClientEmployment: Application driver is not set.");
            return null;
        }

        if (poClientEmployment == null) {
            poClientEmployment = new Model_Client_Employment();
            poClientEmployment.setApplicationDriver(poGRider);
            poClientEmployment.setXML("Model_Client_Employment");
            poClientEmployment.setTableName("Client_Employment");
            poClientEmployment.initialize();
        }

        return poClientEmployment;
    }

    public Model_Client_Institution_Contact ClientInstitutionContact() {
        if (poGRider == null) {
            System.err.println("ClientModel.ClientInstitutionContact: Application driver is not set.");
            return null;
        }

        if (poClientInstitutionContact == null) {
            poClientInstitutionContact = new Model_Client_Institution_Contact();
            poClientInstitutionContact.setApplicationDriver(poGRider);
            poClientInstitutionContact.setXML("Model_Client_Institution_Contact_Person");
            poClientInstitutionContact.setTableName("Client_Institution_Contact_Person");
            poClientInstitutionContact.initialize();
        }

        return poClientInstitutionContact;
    }
    
    public Model_Corporate_Role ClientRole() {
        if (poGRider == null) {
            System.err.println("ClientModel.ClientRole: Application driver is not set.");
            return null;
        }

        if (poRole == null) {
            poRole = new Model_Corporate_Role();
            poRole.setApplicationDriver(poGRider);
            poRole.setXML("Model_Corporate_Role");
            poRole.setTableName("Corporate_Role");
            poRole.initialize();
        }

        return poRole;
    }
        
    public Model_Client_Mobile ClientMobile() {
        if (poGRider == null) {
            System.err.println("ClientModel.ClientMobile: Application driver is not set.");
            return null;
        }

        if (poClientMobile == null) {
            poClientMobile = new Model_Client_Mobile();
            poClientMobile.setApplicationDriver(poGRider);
            poClientMobile.setXML("Model_Client_Mobile");
            poClientMobile.setTableName("Client_Mobile");
            poClientMobile.initialize();
        }

        return poClientMobile;
    }
        
    public Model_Client_Mail ClientMail() {
        if (poGRider == null) {
            System.err.println("ClientModel.ClientMail: Application driver is not set.");
            return null;
        }

        if (poClientMail == null) {
            poClientMail = new Model_Client_Mail();
            poClientMail.setApplicationDriver(poGRider);
            poClientMail.setXML("Model_Client_eMail_Address");
            poClientMail.setTableName("Client_eMail_Address");
            poClientMail.initialize();
        }

        return poClientMail;
    }
        
    public Model_Client_Social_Media ClientSocMed() {
        if (poGRider == null) {
            System.err.println("ClientModel.ClientSocialMedia: Application driver is not set.");
            return null;
        }

        if (poClientSocmed == null) {
            poClientSocmed = new Model_Client_Social_Media();
            poClientSocmed.setApplicationDriver(poGRider);
            poClientSocmed.setXML("Model_Client_Social_Media");
            poClientSocmed.setTableName("Client_Social_Media");
            poClientSocmed.initialize();
        }

        return poClientSocmed;
    }    
    
    public Model_Account_Client_Accreditation ClientAccreditation() {
        if (poGRider == null) {
            System.err.println("ClientModel.ClientAccreditation: Application driver is not set.");
            return null;
        }

        if (poClientAccreditation == null) {
            poClientAccreditation = new Model_Account_Client_Accreditation();
            poClientAccreditation.setApplicationDriver(poGRider);
            poClientAccreditation.setXML("Model_Account_Client_Accreditation");
            poClientAccreditation.setTableName("Account_Client_Accreditation");
            poClientAccreditation.initialize();
        }

        return poClientAccreditation;
    }   
    
    public Model_AP_Client_Master APClientMaster() {
        if (poGRider == null) {
            System.err.println("ClientModel.APClientMaster: Application driver is not set.");
            return null;
        }

        if (poAPClientMaster == null) {
            poAPClientMaster = new Model_AP_Client_Master();
            poAPClientMaster.setApplicationDriver(poGRider);
            poAPClientMaster.setXML("Model_AP_Client_Master");
            poAPClientMaster.setTableName("AP_Client_Master");
            poAPClientMaster.initialize();
        }

        return poAPClientMaster;
    }
    
    public Model_AP_Client_Ledger APClientLedger() {
        if (poGRider == null) {
            System.err.println("ClientModel.APClientLedger: Application driver is not set.");
            return null;
        }

        if (poAPClientLedger == null) {
            poAPClientLedger = new Model_AP_Client_Ledger();
            poAPClientLedger.setApplicationDriver(poGRider);
            poAPClientLedger.setXML("Model_AP_Client_Ledger");
            poAPClientLedger.setTableName("AP_Client_Ledger");
            poAPClientLedger.initialize();
        }

        return poAPClientLedger;
    }
    
    public Model_AP_Client_Bank_Account APClientBankAccount() {
        if (poGRider == null) {
            System.err.println("ClientModel.APClientBankAccount: Application driver is not set.");
            return null;
        }

        if (poAPClientBankAccount == null) {
            poAPClientBankAccount = new Model_AP_Client_Bank_Account();
            poAPClientBankAccount.setApplicationDriver(poGRider);
            poAPClientBankAccount.setXML("Model_AP_Client_Bank_Account");
            poAPClientBankAccount.setTableName("AP_Client_Bank_Account");
            poAPClientBankAccount.initialize();
        }

        return poAPClientBankAccount;
    }
    
    public Model_Payee Payee(){
        if (poGRider == null){
            System.err.println("CashflowModels.Payee: Application driver is not set.");
            return null;
        }
        
        if (poPayee == null){
            poPayee = new Model_Payee();
            poPayee.setApplicationDriver(poGRider);
            poPayee.setXML("Model_Payee");
            poPayee.setTableName("Payee");
            poPayee.initialize();
        }

        return poPayee;
    }
    
}
