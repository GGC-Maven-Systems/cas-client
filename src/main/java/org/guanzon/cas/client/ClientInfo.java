package org.guanzon.cas.client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.ClientType;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.cas.client.model.Model_Client_Address;
import org.guanzon.cas.client.model.Model_Client_Institution_Contact;
import org.guanzon.cas.client.model.Model_Client_Mail;
import org.guanzon.cas.client.model.Model_Client_Master;
import org.guanzon.cas.client.model.Model_Client_Mobile;
import org.guanzon.cas.client.model.Model_Client_Social_Media;
import org.guanzon.cas.client.services.ClientControllers;
import org.guanzon.cas.client.services.ClientModels;
import org.guanzon.cas.parameter.Barangay;
import org.guanzon.cas.parameter.Country;
import org.guanzon.cas.parameter.Province;
import org.guanzon.cas.parameter.TownCity;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;

public class ClientInfo extends Parameter{
    Model_Client_Master poClient;
    Model_Client_Mobile poMobile;
    Model_Client_Address poAddress;
    Model_Client_Mail poMail;
    Model_Client_Social_Media poSocMed;
    Model_Client_Institution_Contact poContact;
    
    Client_Role poRole;
    Client_Master poContactPerson;
    
    ArrayList<Model_Client_Mobile> paMobile;
    ArrayList<Model_Client_Address> paAddress;
    ArrayList<Model_Client_Mail> paMail;
    ArrayList<Model_Client_Social_Media> paSocMed;
    ArrayList<Model_Client_Institution_Contact> paContact;
    
    String psClientTp;
    String psCategory;
    
    @Override
    public void initialize() throws SQLException, GuanzonException{
        super.initialize();
        
        psRecdStat = Logical.YES;

        ClientModels model = new ClientModels(poGRider);
        
        poRole = new ClientControllers(poGRider, logwrapr).ClientRole();
        poContactPerson = new ClientControllers(poGRider, logwrapr).Client().Master();
        
        poClient = model.ClientMaster();
        poMobile = model.ClientMobile();               
        poMail = model.ClientMail();
        poAddress = model.ClientAddress();
        poSocMed = model.ClientSocMed();
        poContact = model.ClientInstitutionContact();
        
        if (psClientTp == null || psClientTp.isEmpty()) psClientTp = ClientType.INDIVIDUAL;
    }
        
    @Override
    public Model_Client_Master getModel() {
        return poClient;
    }
    
    public void setClientType(String clientType){
        psClientTp = clientType;
    }
    
    public Client_Role Role(){
        return poRole;
    }
    
    public Client_Master ContactPerson(){
        return poContactPerson;
    }

    public Model_Client_Mobile Mobile(int row){
        if (row > paMobile.size() -1) return null;
        
        return paMobile.get(row);
    }
    
    public Model_Client_Address Address(int row){
        if (row > paAddress.size() -1) return null;
        
        return paAddress.get(row);
    }
    
    public Model_Client_Mail Mail(int row){
        if (row > paMail.size() -1) return null;
        
        return paMail.get(row);
    }
    
    public Model_Client_Social_Media SocMed(int row){
        if (row > paSocMed.size() -1) return null;
        
        return paSocMed.get(row);
    }
    
    public Model_Client_Institution_Contact InstiContact(int row){
        if (row > paContact.size() -1) return null;
        
        return paContact.get(row);
    }
    
    public ArrayList<Model_Client_Address> AddressList(){
        return paAddress;
    }
    
    public ArrayList<Model_Client_Institution_Contact> InstiContactList(){
        return paContact;
    }
        
    public int getMobileCount(){
        return paMobile.size();
    }
    
    public int getAddressCount(){
        return paAddress.size();
    }
    
    public int getMailCount(){
        return paMail.size();
    }
    
    public int getSocMedCount(){
        return paSocMed.size();
    }
    
    public int getInstiContactCount(){
        return paContact.size();
    }
    
    public void setCategory(String category){
        psCategory = category;
    }
    
    public String getCategory(){
        return psCategory;
    }

    public JSONObject openClientRecord(String Id) throws SQLException, GuanzonException, CloneNotSupportedException {
        if (!pbInitRec){
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "Object is not initialized.");
            return poJSON;
        }
        
        poJSON = poClient.openRecord(Id);
        
        if ("success".equals((String) poJSON.get("result"))){
            
            //load addresses
            String lsSQL = "SELECT * FROM Client_Address" +
                            " WHERE sClientID = " + SQLUtil.toSQL(poClient.getClientId()) +
                            " ORDER BY sAddrssID";
            
            paAddress = new ArrayList<>();
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            
            while (loRS.next()){
                Model_Client_Address object = (Model_Client_Address) poAddress.clone();
                object.newRecord();
                
                JSONObject loJSON = object.openRecord(loRS.getString("sAddrssID"));
                
                if ("success".equals((String) loJSON.get("result"))) {
                    lsSQL = "SELECT sTownIDxx, sTownName, sProvIDxx FROM TownCity WHERE sTownIDxx = " + SQLUtil.toSQL(object.getTownId());
                    
                    ResultSet loRx = poGRider.executeQuery(lsSQL);
                    
                    if (loRx.next()){
                        object.Town().setTownId(loRx.getString("sTownIDxx"));
                        object.Town().setDescription(loRx.getString("sTownName"));
                        
                        lsSQL = "SELECT sProvIDxx, sDescript FROM Province WHERE sProvIDxx = " + SQLUtil.toSQL(loRx.getString("sProvIDxx"));
                    
                        loRx = poGRider.executeQuery(lsSQL);
                        
                        if (loRx.next()){
                            object.Town().Province().setProvinceId(loRx.getString("sProvIDxx"));
                            object.Town().Province().setDescription(loRx.getString("sDescript"));
                        }
                    }
                    
                    lsSQL = "SELECT sBrgyIDxx, sBrgyName FROM Barangay WHERE sBrgyIDxx = " + SQLUtil.toSQL(object.getBarangayId());
                    
                    loRx = poGRider.executeQuery(lsSQL);

                    if (loRx.next()){
                        object.Barangay().setBarangayId(loRx.getString("sBrgyIDxx"));
                        object.Barangay().setBarangayName(loRx.getString("sBrgyName"));
                    }

                    paAddress.add(object);                    
                } else 
                    return loJSON;
            }
            
            //load mobile nos
            lsSQL = "SELECT * FROM Client_Mobile" +
                            " WHERE sClientID = " + SQLUtil.toSQL(poClient.getClientId()) +
                            " ORDER BY sMobileID";
            
            loRS = poGRider.executeQuery(lsSQL);
            
            paMobile = new ArrayList<>();
            while (loRS.next()){
                Model_Client_Mobile object = (Model_Client_Mobile) poMobile.clone();
                object.newRecord();
                
                JSONObject loJSON = object.openRecord(loRS.getString("sMobileID"));
                
                if ("success".equals((String) loJSON.get("result"))) 
                    paMobile.add(object);
                else 
                    return loJSON;
            }
            
            //load email addresses
            lsSQL = "SELECT * FROM Client_eMail_Address" +
                            " WHERE sClientID = " + SQLUtil.toSQL(poClient.getClientId()) +
                            " ORDER BY sEmailIDx";
            
            loRS = poGRider.executeQuery(lsSQL);
            
            paMail = new ArrayList<>();
            while (loRS.next()){
                Model_Client_Mail object = (Model_Client_Mail) poMail.clone();
                object.newRecord();
                
                JSONObject loJSON = object.openRecord(loRS.getString("sEmailIDx"));
                
                if ("success".equals((String) loJSON.get("result"))) 
                    paMail.add(object);
                else 
                    return loJSON;
            }
            
            //load social media accounts
            lsSQL = "SELECT * FROM Client_Social_Media" +
                            " WHERE sClientID = " + SQLUtil.toSQL(poClient.getClientId()) +
                            " ORDER BY sSocialID";
            
            loRS = poGRider.executeQuery(lsSQL);
            
            paSocMed = new ArrayList<>();
            while (loRS.next()){
                Model_Client_Social_Media object = (Model_Client_Social_Media) poSocMed.clone();
                object.newRecord();
                
                JSONObject loJSON = object.openRecord(loRS.getString("sSocialID"));
                
                if ("success".equals((String) loJSON.get("result"))) 
                    paSocMed.add(object);
                else 
                    return loJSON;
            }
         
            //load institution contact person
            lsSQL = "SELECT * FROM Client_Institution_Contact_Person"
                    + " WHERE sClientID = " + SQLUtil.toSQL(poClient.getClientId())
                    + " ORDER BY sContctID";
            System.out.println("SQL : " + lsSQL);
            loRS = poGRider.executeQuery(lsSQL);
            
            boolean lbHasContact = false;
            paContact = new ArrayList<>();
            Model_Client_Institution_Contact object;
            while (loRS.next()) {
                lbHasContact = true;
                object = (Model_Client_Institution_Contact) poContact.clone();
                object.initialize();

                JSONObject loJSON = object.openRecord(loRS.getString("sContctID"));
                if ("success".equals((String) loJSON.get("result"))) {
                    paContact.add(object);
                } else {
                    return loJSON;
                }
            }
            
            if(!lbHasContact){
                object = (Model_Client_Institution_Contact) poContact.clone();
                object.newRecord();
                paContact.add(object);
            }
        }

        return poJSON;
    }
    public static String formatFullName(
            String surname,
            String firstName,
            String suffix,
            String middleName) {

        return (cap(surname) + ", "
                + cap(firstName)
                + (isEmpty(suffix) ? "" : " " + cap(suffix))
                + (isEmpty(middleName) ? "" : " " + cap(middleName)))
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String cap(String s) {
        if (isEmpty(s)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String word : s.trim().split("\\s+")) {
            sb.append(    Character.toUpperCase(word.charAt(0))   + word.substring(1).toLowerCase()  ).append(" ");
        }
        return sb.toString().trim();
    }
    
    public JSONObject openContactRecord(String Id, int fnRow) throws SQLException, GuanzonException, CloneNotSupportedException {
        if (!pbInitRec){
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "Object is not initialized.");
            return poJSON;
        }
        
        //open contact record
        poJSON = poContactPerson.getModel().openRecord(Id);
        
        if ("success".equals((String) poJSON.get("result"))){
            
            //get last row and update record (considered new record when added to array list)
            Model_Client_Institution_Contact loContact = (Model_Client_Institution_Contact) InstiContact(fnRow);
            
            loContact.setCategoryCode(psCategory);
            loContact.setClientId(poClient.getClientId());
            loContact.setcCPrsonID(poContactPerson.getModel().getClientId());
            
            //contact person name
            String lsLastnme = poContactPerson.getModel().getLastName() == null ? "" : poContactPerson.getModel().getLastName();
            String lsFrstnme = poContactPerson.getModel().getFirstName()== null ? "" : poContactPerson.getModel().getFirstName();
            String lsMidnme = poContactPerson.getModel().getMiddleName()== null ? "" : poContactPerson.getModel().getMiddleName();
            String lsSuffix = poContactPerson.getModel().getSuffixName()== null ? "" : poContactPerson.getModel().getSuffixName();

            String lsfullname = poContactPerson.getModel().getCompanyName() == null ? "" : formatFullName(lsLastnme,lsFrstnme,lsSuffix,lsMidnme);

            loContact.setContactPersonName(lsfullname);
            
            //load mobile nos
            String lsSQL = "SELECT * FROM Client_Mobile" +
                            " WHERE sClientID = " + SQLUtil.toSQL(poContactPerson.getModel().getClientId()) +
                            " AND cRecdStat = '1'" +
                            " AND cPrimaryx = '1'" +
                            " ORDER BY sMobileID";
            
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            
            while (loRS.next()){
                Model_Client_Mobile object = (Model_Client_Mobile) poMobile.clone();
                object.newRecord();
                
                JSONObject loJSON = object.openRecord(loRS.getString("sMobileID"));
                if ("success".equals((String) loJSON.get("result"))){
                    
                    switch(object.getMobileType()){
                    
                    case "0": //Mobile
                        loContact.setMobileNo(object.getMobileNo());
                        break;
                    case "1": //Telephone
                        loContact.setLandlineNo(object.getMobileNo());
                        break;
                    case "2": //Fax No
                        loContact.setFaxNo(object.getMobileNo());
                    }
                }
            }
            
            //load email addresses
            lsSQL = "SELECT * FROM Client_eMail_Address" +
                            " WHERE sClientID = " + SQLUtil.toSQL(poContactPerson.getModel().getClientId()) +
                            " AND cRecdStat = '1'" +
                            " AND cPrimaryx = '1'" +
                            " ORDER BY sEmailIDx";

            loRS = poGRider.executeQuery(lsSQL);

            while (loRS.next()){
                Model_Client_Mail object = (Model_Client_Mail) poMail.clone();
                object.newRecord();

                JSONObject loJSON = object.openRecord(loRS.getString("sEmailIDx"));
                if ("success".equals((String) loJSON.get("result"))) loContact.setMailAddress(object.getMailAddress());

            }
            
            //load social media accounts
            lsSQL = "SELECT * FROM Client_Social_Media" +
                            " WHERE sClientID = " + SQLUtil.toSQL(poContactPerson.getModel().getClientId()) +
                            " AND cRecdStat = '1'" +
                            " ORDER BY sSocialID";
            
            loRS = poGRider.executeQuery(lsSQL);
            
            //clear exisitng records before initializing data
            loContact.setSocMedAccount1("");
            loContact.setSocMedAccount2("");
            loContact.setSocMedAccount3("");
            
            while (loRS.next()){
                Model_Client_Social_Media object = (Model_Client_Social_Media) poSocMed.clone();
                object.newRecord();
                
                //set first 3 accounts of contact social media
                JSONObject loJSON = object.openRecord(loRS.getString("sSocialID"));
                switch(loRS.getRow()){
                    
                    case 1:
                        if ("success".equals((String) loJSON.get("result"))) loContact.setSocMedAccount1(object.getAccount());
                        break;
                    case 2:
                        if ("success".equals((String) loJSON.get("result"))) loContact.setSocMedAccount2(object.getAccount());
                    break;
                    case 3:
                        if ("success".equals((String) loJSON.get("result"))) loContact.setSocMedAccount3(object.getAccount());
                    break;
                }
            }
        }

        return poJSON;
    }
    
    public JSONObject addMobile() throws CloneNotSupportedException{
        poJSON = new JSONObject();
        
        if (!pbInitRec){
            poJSON.put("result", "error");
            poJSON.put("message", "Object is not initialized.");
            return poJSON;
        }
        
        if (getEditMode() != EditMode.ADDNEW && getEditMode() != EditMode.UPDATE){
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid edit mode. Unable to add mobile.");
            return poJSON;
        }
         
        if (paMobile.size() > 0){
            if (paMobile.get(getMobileCount() - 1).getMobileNo().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Unable to add new mobile record.\n\nLast record is still empty.");
                return poJSON;
            }
        }
        
        Model_Client_Mobile object = new ClientModels(poGRider).ClientMobile();//(Model_Client_Mobile) poMobile.clone();
        object.newRecord();

        paMobile.add(object);
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public JSONObject addAddress() throws CloneNotSupportedException, SQLException{
        poJSON = new JSONObject();
        
        if (!pbInitRec){
            poJSON.put("result", "error");
            poJSON.put("message", "Object is not initialized.");
            return poJSON;
        }
        
        if (getEditMode() != EditMode.ADDNEW && getEditMode() != EditMode.UPDATE){
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid edit mode. Unable to add mobile.");
            return poJSON;
        }
         
        if (paAddress.size() > 0){
            if (paAddress.get(getAddressCount() - 1).getAddress().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Unable to add new address record.\n\nLast record's address is still empty.");
                return poJSON;
            }
            
            if (paAddress.get(getAddressCount() - 1).getTownId().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Unable to add new address record.\n\nLast record's town is still empty.");
                return poJSON;
            }
        }
        
        Model_Client_Address object = new ClientModels(poGRider).ClientAddress(); //(Model_Client_Address) poaddress.clone();
        object.newRecord();

        paAddress.add(object);
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public JSONObject addMail() throws CloneNotSupportedException{
        poJSON = new JSONObject();
        
        if (!pbInitRec){
            poJSON.put("result", "error");
            poJSON.put("message", "Object is not initialized.");
            return poJSON;
        }
        
        if (getEditMode() != EditMode.ADDNEW && getEditMode() != EditMode.UPDATE){
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid edit mode. Unable to add mobile.");
            return poJSON;
        }
         
        if (paMail.size() > 0){
            if (paMail.get(getMailCount()- 1).getMailAddress().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Unable to add new email record.\n\nLast record is still empty.");
                return poJSON;
            }
        }
        
        Model_Client_Mail object = new ClientModels(poGRider).ClientMail();//(Model_Client_Mail) poMail.clone();
        object.newRecord();

        paMail.add(object);
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public JSONObject addSocMed() throws CloneNotSupportedException{
        poJSON = new JSONObject();
        
        if (!pbInitRec){
            poJSON.put("result", "error");
            poJSON.put("message", "Object is not initialized.");
            return poJSON;
        }
        
        if (getEditMode() != EditMode.ADDNEW && getEditMode() != EditMode.UPDATE){
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid edit mode. Unable to add mobile.");
            return poJSON;
        }
         
        if (paSocMed.size() > 0){
            if (paSocMed.get(getSocMedCount()- 1).getAccount().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Unable to add new social media record.\n\nLast record is still empty.");
                return poJSON;
            }
        }
        
        Model_Client_Social_Media object = new ClientModels(poGRider).ClientSocMed();//(Model_Client_Social_Media) poSocMed.clone();
        object.newRecord();

        paSocMed.add(object);
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public JSONObject addInstiContact() throws CloneNotSupportedException, SQLException{
        poJSON = new JSONObject();
        
        if (!pbInitRec){
            poJSON.put("result", "error");
            poJSON.put("message", "Object is not initialized.");
            return poJSON;
        }
        
        if (getEditMode() != EditMode.ADDNEW && getEditMode() != EditMode.UPDATE){
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid edit mode. Unable to add mobile.");
            return poJSON;
        }
         
        if (paContact.size() > 0){
            
            if (paContact.get(getInstiContactCount()- 1).getContactPersonName().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Unable to add new contact person record.\n\nLast record's name is still empty.");
                return poJSON;
            }
            
            if (paContact.get(getInstiContactCount()- 1).getMobileNo().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Unable to add new contact person record.\n\nLast record's mobile number is still empty.");
                return poJSON;
            }
            
            if (paContact.get(getInstiContactCount()- 1).getsRoleIDxx().isEmpty()){
                poJSON.put("result", "error");
                poJSON.put("message", "Unable to add new contact person record.\n\nLast record's role is still empty.");
                return poJSON;
            }
        }
        
        Model_Client_Institution_Contact object = new ClientModels(poGRider).ClientInstitutionContact();//(Model_Client_Institution_Contact) poContact.clone();
        object.newRecord();
        
        paContact.add(object);
        
        poJSON.put("result", "success");
        return poJSON;
    }

    public JSONObject searchBirthPlace(String lsValue) throws SQLException, GuanzonException{
        if (lsValue == null) lsValue = "";
        
        TownCity loParam = new ParamControllers(poGRider, logwrapr).TownCity();
        
        poJSON = loParam.searchRecord(lsValue, false);
        
        if ("success".equals((String) poJSON.get("result"))){
            poClient.setBirthPlaceId(loParam.getModel().getTownId());
            poClient.BirthTown().setTownId(loParam.getModel().getTownId());
            poClient.BirthTown().setDescription(loParam.getModel().getDescription());
        } else {
            poClient.setBirthPlaceId("");
            poClient.BirthTown().setTownId("");
            poClient.BirthTown().setDescription("");
        }
        
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public JSONObject searchCitizenship(String lsValue) throws SQLException, GuanzonException{
        if (lsValue == null) lsValue = "";
        
        Country loParam = new ParamControllers(poGRider, logwrapr).Country();
        
        poJSON = loParam.searchRecord(lsValue, false);
        
        if ("success".equals((String) poJSON.get("result"))){
            poClient.setCitizenshipId(loParam.getModel().getCountryId());
            poClient.Citizenship().setCountryId(loParam.getModel().getCountryId());
            poClient.Citizenship().setDescription(loParam.getModel().getDescription());
        } else {
            poClient.setCitizenshipId("");
            poClient.Citizenship().setCountryId("");
            poClient.Citizenship().setDescription("");
        }
        
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }
    
    //Added by Arsiela- 05-23-2026
    public JSONObject searchSpouse(String lsValue) throws SQLException, GuanzonException{
        if (lsValue == null) lsValue = "";
        ClientInfo loParam = new ClientControllers(poGRider, logwrapr).ClientInfo();
        loParam.setClientType(Logical.NO);
        poJSON = loParam.searchRecord(lsValue, false);
        if ("success".equals((String) poJSON.get("result"))){
            poClient.setSpouseId(loParam.getModel().getClientId());
        } else {
            poClient.setSpouseId("");
        }
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public String getSpouseName() throws SQLException, GuanzonException{
        Model_Client_Master loClient = new ClientModels(poGRider).ClientMaster();
        poJSON = loClient.openRecord(getModel().getSpouseId());
        if (!"success".equals((String) poJSON.get("result"))){
            return "";
        }
        return loClient.getCompanyName();
    }
    
    public JSONObject searcbNationality(String lsValue, boolean byCode)  throws SQLException, GuanzonException{
        
        //filter data with cRecdStat(1) and sNational is not empty. To force the user on completing the data
        String lsSql = "SELECT sCntryIDx, sNational, sDescript FROM Country WHERE cRecdStat = '1' AND (sNational IS NOT NULL and trim(sNational) <> '')";
 
        poJSON = ShowDialogFX.Search(
                poGRider,
                lsSql, 
                lsValue, 
                "ID»Nationality»Country",
                "sCntryIDx»sNational»sDescript",
                "sCntryIDx»sNational»sDescript",
                byCode ? 0 : 1);
        
        if (poJSON == null) {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        Country loParam = new ParamControllers(poGRider, logwrapr).Country();
        poJSON = loParam.openRecord((String) poJSON.get("sCntryIDx"));
        
        if ("success".equals((String) poJSON.get("result"))){
            poClient.setCitizenshipId(loParam.getModel().getCountryId());
            poClient.Citizenship().setCountryId(loParam.getModel().getCountryId());
            poClient.Citizenship().setDescription(loParam.getModel().getDescription());
            poClient.Citizenship().setNationality(loParam.getModel().getNationality());
        } else {
            poClient.setCitizenshipId("");
            poClient.Citizenship().setCountryId("");
            poClient.Citizenship().setDescription("");
            poClient.Citizenship().setNationality("");
        }
        
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public JSONObject searchProvince(int lnRow, String lsValue, boolean lbByCode) throws SQLException, GuanzonException{
        if (lsValue == null) lsValue = "";
        
        Province loParam = new ParamControllers(poGRider, logwrapr).Province();
        
        poJSON = loParam.searchRecord(lsValue, lbByCode);
        
        if ("success".equals((String) poJSON.get("result"))){
            paAddress.get(lnRow).Town().Province().setProvinceId(loParam.getModel().getProvinceId());
            paAddress.get(lnRow).Town().Province().setDescription(loParam.getModel().getDescription());
            paAddress.get(lnRow).Town().setProvinceId(loParam.getModel().getProvinceId());
        } else {
            paAddress.get(lnRow).Town().Province().setProvinceId("");
            paAddress.get(lnRow).Town().Province().setDescription("");
            paAddress.get(lnRow).Town().setProvinceId("");
        }
        
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public JSONObject searchTown(int lnRow, String lsValue, boolean lbByCode) throws SQLException, GuanzonException{
        if (lsValue == null) lsValue = "";
        
        TownCity loParam = new ParamControllers(poGRider, logwrapr).TownCity();
        
        if (paAddress.get(lnRow).Town().getProvinceId() == null ||
            paAddress.get(lnRow).Town().getProvinceId().isEmpty()){
            poJSON = loParam.searchRecord(lsValue, lbByCode);
        } else {
            poJSON = loParam.searchRecord(lsValue, lbByCode, paAddress.get(lnRow).Town().getProvinceId());
        }
        
        if ("success".equals((String) poJSON.get("result"))){
            paAddress.get(lnRow).setTownId(loParam.getModel().getTownId());
            paAddress.get(lnRow).Town().setTownId(loParam.getModel().getTownId());
            paAddress.get(lnRow).Town().setDescription(loParam.getModel().getDescription());
            paAddress.get(lnRow).Town().setProvinceId(loParam.getModel().getProvinceId());
        } else {
            paAddress.get(lnRow).setTownId("");
            paAddress.get(lnRow).Town().setTownId("");
            paAddress.get(lnRow).Town().setDescription("");
        }
        
        return poJSON;
    }
    
    public JSONObject searchBarangay(int lnRow, String lsValue, boolean lbByCode) throws SQLException, GuanzonException{
        if (lsValue == null) lsValue = "";
        
        Barangay loParam = new ParamControllers(poGRider, logwrapr).Barangay();
        
        if (paAddress.get(lnRow).Town().getTownId() == null ||
            paAddress.get(lnRow).Town().getTownId().isEmpty()){
            poJSON = loParam.searchRecord(lsValue, lbByCode);
        } else {
            poJSON = loParam.searchRecord(lsValue, lbByCode, paAddress.get(lnRow).Town().getTownId());
        }
        
        if ("success".equals((String) poJSON.get("result"))){
            paAddress.get(lnRow).setBarangayId(loParam.getModel().getBarangayId());
            paAddress.get(lnRow).Barangay().setBarangayId(loParam.getModel().getBarangayId());
            paAddress.get(lnRow).Barangay().setBarangayName(loParam.getModel().getBarangayName());
            
            paAddress.get(lnRow).setTownId(loParam.getModel().getTownId());
            paAddress.get(lnRow).Town().setTownId(loParam.getModel().getTownId());
            paAddress.get(lnRow).Town().setProvinceId(loParam.getModel().Town().getProvinceId());                        
        } else {
            paAddress.get(lnRow).setBarangayId("");
            paAddress.get(lnRow).Barangay().setBarangayId("");
            paAddress.get(lnRow).Barangay().setBarangayName("");        
        }
        
        return poJSON;
    }
    
    public JSONObject searchContactPerson(String value, boolean byCode){
        
        try{
            
            String lsSQL = getSQ_Browse();
            
            String lsCondition = "(c.sClientID IS NOT NULL and d.sClientID IS NOT NULL) "
                                    + "AND a.sClientID IN (SELECT e.sClientID FROM Client_Address e WHERE e.cRecdStat = '1' AND e.cPrimaryx = '1') "
                                    + "AND a.sClientID IN (SELECT f.sClientID FROM Client_Social_Media f WHERE f.cRecdStat = '1')";
            lsSQL = MiscUtil.addCondition(lsSQL, lsCondition);
        
            poJSON = ShowDialogFX.Search(poGRider,
                    lsSQL,
                    value,
                    "Birthday»Name»Mobile»Email",
                    "dBirthDte»xFullName»xMobileNo»xEMailAdd",
                    "a.dBirthDte»TRIM(CONCAT(a.sLastName, ', ', a.sFrstName, IF(a.sSuffixNm <> '', CONCAT(' ', a.sSuffixNm, ''), ''), ' ', a.sMiddName))»IFNULL(c.sMobileNo, '')»IFNULL(d.sEMailAdd, '')",
                    byCode ? 0 : 1);

            if (poJSON != null) {
                return poContactPerson.getModel().openRecord((String) poJSON.get("sClientID"));
            } else {
                poJSON = new JSONObject();
                poJSON.put("result", "error");
                poJSON.put("message", "No record loaded.");
                return poJSON;
            }

        }catch(Exception e){
            logwrapr.severe(e.getMessage());
            
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
            return poJSON;
        }
        
    }

    @Override
    public JSONObject isEntryOkay() throws SQLException, GuanzonException, CloneNotSupportedException {
        poJSON = new JSONObject();

        //validate master                        
        if (getEditMode() == EditMode.ADDNEW || getEditMode() == EditMode.UPDATE) {
            
            poJSON = poClient.setClientType(psClientTp);
            
            //remove empty address
            if (paAddress.get(paAddress.size() - 1).getAddress().isEmpty()) {
                paAddress.remove(paAddress.size() - 1);
            }
            
            //validate address
            switch (paAddress.size()) {
                case 0:
                    addAddress();
                case 1:
                    //check last row's town id if set
                    if (paAddress.get(paAddress.size() - 1).getTownId().isEmpty()) {
                        poJSON.put("result", "error");
                        poJSON.put("message", "Town must have a value.");
                        return poJSON;
                    }
                    //check last row's address if set
                    if (paAddress.get(paAddress.size() - 1).getAddress().isEmpty()) {
                        poJSON.put("result", "error");
                        poJSON.put("message", "Address must have a value.");
                        return poJSON;
                    }
            }

            if (poClient.getClientType().equals(ClientType.INDIVIDUAL)) {
                
                if (poClient.getLastName().isEmpty()) {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Last name must not be empty.");
                    return poJSON;
                }

                if (poClient.getFirstName().isEmpty()) {
                    poJSON.put("result", "error");
                    poJSON.put("message", "First name must not be empty.");
                    return poJSON;
                }
                
                String lsName = formatFullName( poClient.getLastName(),poClient.getFirstName(),poClient.getSuffixName(),poClient.getMiddleName());
                lsName = lsName.trim();
            
                poClient.setCompanyName(lsName);
                
                //remove empty mobile
                if (paMobile.get(paMobile.size() - 1).getMobileNo().isEmpty()) {
                    paMobile.remove(paMobile.size() - 1);
                }

                //validate mobile
                switch (paMobile.size()) {
                    case 0:
                        addMobile();
                    case 1:
                        //check last row's mobile if set
                        if (paMobile.get(paMobile.size() - 1).getMobileNo().isEmpty()) {
                            poJSON.put("result", "error");
                            poJSON.put("message", "Client must have a mobile number.");
                            return poJSON;
                        }
                }
                
                //remove empty email address
                if (paMail.get(paMail.size() - 1).getMailAddress().isEmpty()) {
                    paMail.remove(paMail.size() - 1);
                }
                
                //validate email address
                switch (paMail.size()) {
                   case 0:
                       addMail();
                   case 1:
                       //check last row's email address if set
                       if (Mail(paMail.size() - 1).getMailAddress().isEmpty()) {
                           poJSON.put("result", "error");
                           poJSON.put("message", "Email Address must have a value.");
                           return poJSON;
                       }
               }
                
                //remove empty social medias
                if (paSocMed.get(paSocMed.size() - 1).getAccount().isEmpty()) {
                    paSocMed.remove(paSocMed.size() - 1);
                }
                
                //validate social medias
                switch (paSocMed.size()) {
                   case 0:
                       addSocMed();
                   case 1:
                       //check last row's social media if set
                       if (SocMed(paSocMed.size() - 1).getAccount().isEmpty()) {
                           poJSON.put("result", "error");
                           poJSON.put("message", "Social Media must have a value.");
                           return poJSON;
                       }
               }
                    
            } else {
                
                poClient.setLastName("");
                poClient.setFirstName("");
                poClient.setMiddleName("");
                poClient.setSuffixName("");

                //check company name
                if (poClient.getCompanyName().isEmpty()) {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Company name must not be empty.");
                    return poJSON;
                }
                
                //check category paramter value
                if (getCategory() == null || getCategory().isEmpty()) {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Invalid category code!");
                    return poJSON;
                }
                
                //remove empty contact person
                if(paContact.size() > 0){
                    if (paContact.get(paContact.size() - 1).getContactPersonName().isEmpty() &&
                            paContact.get(paContact.size() - 1).getMobileNo().isEmpty() &&
                            paContact.size() > 1) {
                        paContact.remove(paContact.size() - 1);
                    }
                    
                    //check last row's full name and mobile no if set
                    if (InstiContact(paContact.size() - 1).getContactPersonName().isEmpty() &&
                            InstiContact(paContact.size() - 1).getMobileNo().isEmpty()) {
                        poJSON.put("result", "error");
                        poJSON.put("message", "Contact person name and mobile number must have a value.");
                        return poJSON;
                    }

                    //check last row's full name if set
                    if (InstiContact(paContact.size() - 1).getsRoleIDxx().isEmpty()) {
                        poJSON.put("result", "error");
                        poJSON.put("message", "Contact person role must have a value.");
                        return poJSON;
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Please add atleast one primary contact!");
                    return poJSON;
                }
            }

            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poClient.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
            poClient.setModifiedDate(poGRider.getServerDate());
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }

    @Override
    protected JSONObject initFields() throws SQLException, GuanzonException{        
        poMobile.initialize();
        poMobile.newRecord();
        
        poAddress.initialize();
        poAddress.newRecord();
        
        poMail.initialize();
        poMail.newRecord();
        
        poSocMed.initialize();
        poSocMed.newRecord();
        
        poContact.initialize();
        poContact.newRecord();
        
        paMobile = new ArrayList<>();
        paMobile.add(poMobile);
        
        paAddress = new ArrayList<>();
        paAddress.add(poAddress);
        
        paMail = new ArrayList<>();
        paMail.add(poMail);
        
        paSocMed = new ArrayList<>();
        paSocMed.add(poSocMed);
        
        paContact = new ArrayList<>();
        paContact.add(poContact);
        
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        
        return poJSON;
    }
    
    @Override
    protected JSONObject willSave() throws SQLException, GuanzonException{
        
        if (getEditMode() == EditMode.ADDNEW || getEditMode() == EditMode.UPDATE){
            
            //assign master client id (INDIVIDUAL OR INSTITUTION)
            if (getEditMode() == EditMode.ADDNEW){
                poClient.setClientId(MiscUtil.getNextCode(poClient.getTable(), "sClientID", true, poGRider.getGConnection().getConnection(), poGRider.getBranchCode()));
            }
            
            //assign client ids to details
            int lnCtr;
            
            //CONTACT ADDRESS
            Model_Client_Address loAddress;
            int lnPrimaryAddr = 0;
            
            for (lnCtr = 0; lnCtr <= paAddress.size() - 1; lnCtr++){
                
                loAddress = paAddress.get(lnCtr);
                if (loAddress.getAddress().isEmpty() && loAddress.getTownId().isEmpty()) {
                    paAddress.remove(lnCtr);
                    break;
                }
                loAddress.setClientId(poClient.getClientId());
                
                //if (paAddress.size() == 1) loAddress.isPrimaryAddress(true);
                if (loAddress.isPrimaryAddress()) lnPrimaryAddr += 1;
            }
            
            if (lnPrimaryAddr > 1) {
                poJSON = new JSONObject();
                poJSON.put("result", "error");
                poJSON.put("message", "Only one address should be primary!\nPlease check address.");
                return poJSON;
            }
            if (lnPrimaryAddr <= 0) {
                poJSON = new JSONObject();
                poJSON.put("result", "error");
                poJSON.put("message", "Please select atleast one primary address!");
                return poJSON;
            }
           
            //initialize contact person for supplier account or individual contact entry
            if (getModel().getClientType().equals(ClientType.INSTITUTION)){
                
                Model_Client_Institution_Contact loContact;
                int lnPrimaryContact = 0;
                
                for (lnCtr = 0; lnCtr <= paContact.size() - 1; lnCtr++){
                    loContact = paContact.get(lnCtr);

                    //remove row if empty
                    if (loContact.getContactPersonName().isEmpty()) {
                        paContact.remove(lnCtr);
                        break;
                    }
                    
                    /**
                     see method openContactRecord()
                     initialization of primary ids are moved here by searched individual client
                     GUILLIER 2026/03/10
                     **/
                    
                    if (loContact.isPrimaryContactPersion()) lnPrimaryContact += 1;
                }

                if (lnPrimaryContact > 1) {
                    poJSON = new JSONObject();
                    poJSON.put("result", "error");
                    poJSON.put("message", "Only one contact person should be primary active!\nPlease check contact details.");
                    return poJSON;
                }

                if (lnPrimaryContact <= 0) {
                    poJSON = new JSONObject();
                    poJSON.put("result", "error");
                    poJSON.put("message", "Please select atleast one primary contact!");
                    return poJSON;
                 }

            }else{
                
                //MOBILE NUMBER
                Model_Client_Mobile loMobile;
                int lnPrimaryMobile = 0;

                for (lnCtr = 0; lnCtr <= paMobile.size() - 1; lnCtr++){
                    loMobile = paMobile.get(lnCtr);

                    if (loMobile.getMobileNo().isEmpty()) {
                        paMobile.remove(lnCtr);
                        break;
                    }

                    loMobile.setClientId(poClient.getClientId());
                    loMobile.setMobileNetwork(CommonUtils.classifyNetwork(loMobile.getMobileNo()));

                    //get primary mobile
                    if (loMobile.isPrimaryMobile()) lnPrimaryMobile += 1;
                }

                if (lnPrimaryMobile > 1) {
                    poJSON = new JSONObject();
                    poJSON.put("result", "error");
                    poJSON.put("message", "Only one mobile should be primary!\nPlease check address.");
                    return poJSON;
                }
                if (lnPrimaryMobile <= 0) {
                    poJSON = new JSONObject();
                    poJSON.put("result", "error");
                    poJSON.put("message", "Please select atleast one primary mobile!");
                    return poJSON;
                }
                
                //CONTACT EMAIL
                Model_Client_Mail loMail;
                int lnPrimaryMail = 0;

                for (lnCtr = 0; lnCtr <= paMail.size() - 1; lnCtr++){
                    loMail = paMail.get(lnCtr);

                    if (loMail.getMailAddress().isEmpty()) {
                        paMail.remove(lnCtr);
                        break;
                    }
                    loMail.setClientId(poClient.getClientId());

                    if (loMail.isPrimaryEmail()) lnPrimaryMail += 1;
                }

                if (lnPrimaryMail > 1) {
                    poJSON = new JSONObject();
                    poJSON.put("result", "error");
                    poJSON.put("message", "Only one email should be primary!\nPlease check address.");
                    return poJSON;
                }
                if (lnPrimaryMail <= 0) {
                    poJSON = new JSONObject();
                    poJSON.put("result", "error");
                    poJSON.put("message", "Please select atleast one primary email!");
                    return poJSON;
                }

                //CONTACT SOCIAL MEDIA
                Model_Client_Social_Media loSocMed;
                for (lnCtr = 0; lnCtr <= paSocMed.size() - 1; lnCtr++){
                    loSocMed = paSocMed.get(lnCtr);

                    if (loSocMed.getAccount().isEmpty()) {
                        paSocMed.remove(lnCtr);
                        break;
                    }
                    loSocMed.setClientId(poClient.getClientId());
                }
                
            }
        }
        
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        poJSON.put("message", "Record has been saved");
        return poJSON;
    }
    
    @Override
    protected JSONObject saveOthers() throws SQLException, GuanzonException{     
        Model_Client_Mobile loMobile;
        Model_Client_Address loAddress;
        Model_Client_Mail loMail;
        Model_Client_Social_Media loSocMed;
        Model_Client_Institution_Contact loContact;
        
        int lnCtr;
        
        if (!paAddress.isEmpty()){
            for (lnCtr = 0; lnCtr <= paAddress.size() - 1; lnCtr++){
                loAddress = paAddress.get(lnCtr);

                if (loAddress.getEditMode() == EditMode.ADDNEW){
                    loAddress.setModifiedDate(poGRider.getServerDate());
                    poJSON = loAddress.saveRecord();
                    if (!"success".equals((String) poJSON.get("result"))) return poJSON;
                } else {
                    //validate if record is modified
                    loAddress.updateRecord();
                    loAddress.setModifiedDate(poGRider.getServerDate());
                    loAddress.saveRecord();
                }
            }
        }
        
        //save contact via supplier entry or indiviual contact person
        if (getModel().getClientType().equals(ClientType.INSTITUTION)){
            
            if (!paContact.isEmpty()){
                
                //save record for current entries
                for (lnCtr = 0; lnCtr <= paContact.size() - 1; lnCtr++){
                    loContact = paContact.get(lnCtr);

                    if (loContact.getEditMode() == EditMode.ADDNEW){
                        loContact.setClientId(poClient.getClientId());
                        loContact.setModifiedDate(poGRider.getServerDate());
                        poJSON = loContact.saveRecord();
                        if (!"success".equals((String) poJSON.get("result"))) return poJSON;
                        
                    } else {
                        //validate if record is modified
                        poJSON = loContact.updateRecord();
                        if (!"success".equals((String) poJSON.get("result"))) return poJSON;
                        loContact.isPrimaryContactPersion(loContact.isPrimaryContactPersion());
                        loContact.setModifiedDate(poGRider.getServerDate());
                        poJSON = loContact.saveRecord();
                        if (!"success".equals((String) poJSON.get("result"))) return poJSON;
                    }
                }
            }
        }else {
            
            if (!paMobile.isEmpty()){
                for (lnCtr = 0; lnCtr <= paMobile.size() - 1; lnCtr++){
                    loMobile = paMobile.get(lnCtr);

                    if (loMobile.getEditMode() == EditMode.ADDNEW){
                        loMobile.setModifiedDate(poGRider.getServerDate());
                        poJSON = loMobile.saveRecord();
                        if (!"success".equals((String) poJSON.get("result"))) return poJSON;
                    } else {
                        //validate if record is modified
                        loMobile.updateRecord();
                        loMobile.setModifiedDate(poGRider.getServerDate());
                        loMobile.saveRecord();
                    }
                }
            }
            
            if (!paMail.isEmpty()){
                for (lnCtr = 0; lnCtr <= paMail.size() - 1; lnCtr++){
                    loMail = paMail.get(lnCtr);

                    if (loMail.getEditMode() == EditMode.ADDNEW){
                        loMail.setModifiedDate(poGRider.getServerDate());
                        poJSON = loMail.saveRecord();
                        if (!"success".equals((String) poJSON.get("result"))) return poJSON;
                    } else {
                        //validate if record is modified
                        loMail.updateRecord();
                        loMail.setModifiedDate(poGRider.getServerDate());
                        loMail.saveRecord();
                    }
                }
            }
            
            if (!paSocMed.isEmpty()){
                for (lnCtr = 0; lnCtr <= paSocMed.size() - 1; lnCtr++){
                    loSocMed = paSocMed.get(lnCtr);

                    if (loSocMed.getEditMode() == EditMode.ADDNEW){
                        loSocMed.setModifiedDate(poGRider.getServerDate());
                        poJSON = loSocMed.saveRecord();
                        if (!"success".equals((String) poJSON.get("result"))) return poJSON;
                    } else {
                        //validate if record is modified
                        loSocMed.updateRecord();
                        loSocMed.setModifiedDate(poGRider.getServerDate());
                        loSocMed.saveRecord();
                    }
                }
            }
            
        }
        
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        poJSON.put("message", "Record has been saved");
        return poJSON;
    }
   
    @Override
    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException{
        try{
            
            String lsSQL = getSQ_Browse();
        
            poJSON = ShowDialogFX.Search(poGRider,
                    lsSQL,
                    value,
                    "Birthday»Name»Mobile»Email",
                    "dBirthDte»xFullName»xMobileNo»xEMailAdd",
                    "a.dBirthDte»TRIM(CONCAT(a.sLastName, ', ', a.sFrstName, IF(a.sSuffixNm <> '', CONCAT(' ', a.sSuffixNm, ''), ''), ' ', a.sMiddName))»IFNULL(c.sMobileNo, '')»IFNULL(d.sEMailAdd, '')",
                    byCode ? 0 : 1);

            if (poJSON != null) {
                return poClient.openRecord((String) poJSON.get("sClientID"));
            } else {
                poJSON = new JSONObject();
                poJSON.put("result", "error");
                poJSON.put("message", "No record loaded.");
                return poJSON;
            }

        }catch(Exception e){
            logwrapr.severe(e.getMessage());
            
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
            return poJSON;
        }
    }
    
    @Override
    public String getSQ_Browse(){
        String lsCondition = "";

        if (psRecdStat.length() > 1) {
            for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
            }

            lsCondition = "a.cRecdStat IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "a.cRecdStat = " + SQLUtil.toSQL(psRecdStat);
        }               
        
        String lsSQL = "SELECT" +
                            "  a.sClientID" +
                            ", a.cClientTp" +
                            ", a.sLastName" +
                            ", a.sFrstName" +
                            ", a.sMiddName" +
                            ", a.sSuffixNm" +
                            ", a.sMaidenNm" +
                            ", a.sCompnyNm xFullName" +
                            ", a.cGenderCd" +
                            ", a.cCvilStat" +
                            ", a.sCitizenx" +
                            ", a.dBirthDte" +
                            ", a.sBirthPlc" +
                            ", a.sAddlInfo" +
                            ", a.sSpouseID" +
                            ", a.sTaxIDNox" +
                            ", a.sLTOIDxxx" +
                            ", a.sPHBNIDxx" +
                            ", a.cLRClient" +
                            ", a.cMCClient" +
                            ", a.cSCClient" +
                            ", a.cSPClient" +
                            ", a.cCPClient" +
                            ", a.cRecdStat" +
                            ", a.sModified" +
                            ", a.dModified" +
                            ", TRIM(CONCAT(a.sLastName, ', ', a.sFrstName, IF(a.sSuffixNm <> '', CONCAT(' ', a.sSuffixNm, ''), ''), ' ', a.sMiddName)) xFullName " +
                            ", IFNULL(c.sMobileNo, '') xMobileNo" +
                            ", IFNULL(d.sEMailAdd, '') xEMailAdd" +
                        " FROM Client_Master a" +
                            " LEFT JOIN Client_Mobile c ON a.sClientID = c.sClientID" +
                                " AND c.cPrimaryx = '1'" +
                            " LEFT JOIN Client_eMail_Address d ON a.sClientID = d.sClientID" +
                                " AND d.cPrimaryx = '1'";
        
        lsSQL = MiscUtil.addCondition(lsSQL, lsCondition);
        
        if (psClientTp.length() > 1) {
            for (int lnCtr = 0; lnCtr <= psClientTp.length() - 1; lnCtr++) {
                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psClientTp.charAt(lnCtr)));
            }

            lsCondition = "a.cClientTp IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "a.cClientTp = " + SQLUtil.toSQL(psClientTp);
        }      
        
        lsSQL = MiscUtil.addCondition(lsSQL, lsCondition);
        
        return MiscUtil.addCondition(lsSQL, lsCondition);
    }
}
