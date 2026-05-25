package org.guanzon.cas.client.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import org.apache.poi.hssf.record.Record;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.ClientType;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.appdriver.iface.GValidator;
import org.guanzon.cas.client.ClientGUI;
import org.guanzon.cas.client.ClientInfo;
import org.guanzon.cas.client.constants.AccountAccreditationStatus;
import org.guanzon.cas.client.model.Model_Account_Client_Accreditation;
import org.guanzon.cas.client.model.Model_Client_Address;
import org.guanzon.cas.client.model.Model_Client_Institution_Contact;
import org.guanzon.cas.client.services.ClientControllers;
import org.guanzon.cas.client.services.ClientModels;
import org.guanzon.cas.client.validator.ClientAccreditationValidatorFactory;
import org.guanzon.cas.parameter.Company;
import org.guanzon.cas.parameter.model.Model_Company;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.cashflow.model.Model_Payee;
import ph.com.guanzongroup.cas.cashflow.services.CashflowModels;

public class Account_Accreditation extends Parameter {

    private Model_Account_Client_Accreditation poModel;
    private String psValidStatus = AccountAccreditationStatus.OPEN;
    private String psApprovalUser = "";

    @Override
    public void initialize() throws SQLException, GuanzonException {
        super.initialize();

        ClientModels model = new ClientModels(poGRider);
        psRecdStat = TransactionStatus.STATE_OPEN;
        poModel = model.ClientAccreditation();
    }

    @Override
    public JSONObject isEntryOkay() throws SQLException, GuanzonException, CloneNotSupportedException {
        poJSON = new JSONObject();

        //initialize validator
        GValidator loValidator = ClientAccreditationValidatorFactory.make(poGRider.getIndustry());
        
        //initialize params for app validator
        loValidator.setApplicationDriver(poGRider);
        loValidator.setTransactionStatus(psValidStatus);
        loValidator.setMaster(poModel);
        
        //validate
        poJSON = loValidator.validate();
        
        //if validation not success
        if ("error".equals((String) poJSON.get("result"))) {
            return poJSON;
        }
        
        //if validator requires approval
        if(!AccountAccreditationStatus.OPEN.equals(poModel.getRecordStatus())){
            if (poJSON.containsKey("isRequiredApproval") && Boolean.TRUE.equals(poJSON.get("isRequiredApproval"))) {

                //get approval from approving officer
                poJSON = ShowDialogFX.getUserApproval(poGRider);
                if ("error".equals((String) poJSON.get("result"))) {
                    return poJSON;
                }

                if (Integer.parseInt(poJSON.get("nUserLevl").toString()) <= UserRight.ENCODER){
                    poJSON.put("result", "error");
                    poJSON.put("message", "User is not an authorized approving officer.");

                    return poJSON;
                }
                
                //if success, return approving officer user id
                psApprovalUser = poJSON.get("sUserIDxx") != null
                        ? poJSON.get("sUserIDxx").toString()
                        : poGRider.getUserID();

                //add approver's id to result
                poJSON.put("sApproved", psApprovalUser);
//                setApproving(psApprovalUser);
                
            }
        }

        //initialize model date modified and modifier
        poModel.setModifyingId((poGRider.getUserID()));
        poModel.setModifiedDate(poGRider.getServerDate());

        poJSON.put("result", "success");
        return poJSON;
    }

    @Override
    public Model_Account_Client_Accreditation getModel() {
        return poModel;
    }

    @Override
    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException {
        String lsSQL = getSQ_Browse();
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "Transaction No»Date»Name",
                "sTransNox»dTransact»sCompnyNm",
                "sTransNox»sCompnyNm",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sTransNox"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    @Override
    public String getSQ_Browse() {
        String lsSQL;
        String lsCondition = "";

        if (psRecdStat.length() > 1) {
            for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
            }

            lsCondition = "a.cTranStat IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "a.cTranStat = " + SQLUtil.toSQL(psRecdStat);
        }

        lsSQL = " SELECT "
                + " a.sTransNox, "
                + " a.cAcctType, "
                + " a.sClientID, "
                + " a.sAddrssID, "
                + " a.sContctID, "
                + " a.dTransact, "
                + " a.cAcctType, "
                + " a.sRemarksx, "
                + " a.cTranType, "
                + " a.sCategrCd, "
                + " a.cTranStat, "
                + " b.sCompnyNm, "
                + " c.sAddrssID, "
                + " d.sMobileNo, "
                + " IFNULL(CONCAT(c.sHouseNox, ', ', c.sAddressx, ', ', c.sBrgyIDxx, ', ', c.sTownIDxx), '') xAddressx"
                + " FROM Account_Client_Accreditation a "
                + " LEFT JOIN Client_Master b "
                + " ON a.sClientID = b.sClientID "
                + " LEFT JOIN Client_Address c "
                + " ON a.sAddrssID = c.sAddrssID "
                + " LEFT JOIN Client_Institution_Contact_Person d "
                + " ON a.sContctID = d.sContctID ";

        if (!lsCondition.isEmpty()) {
            lsSQL = MiscUtil.addCondition(lsSQL, lsCondition);
        }

        return lsSQL;
    }
    
    public AP_Client_Master getAPClientMaster(String foClientID)
            throws GuanzonException, SQLException {
        
        AP_Client_Master loObject = new ClientControllers(poGRider, null).APClientMaster();
        poJSON = loObject.openRecord(foClientID);
        
        //if error, initialize as new record (no record found). else, update the retrieved record
        if ("error".equals((String) poJSON.get("result"))) {
            loObject.newRecord();
        }else{
            loObject.updateRecord();
        }
        loObject.setWithParentClass(true);
        return loObject;
    }
    
    public String getCompany() throws SQLException, GuanzonException{
        Model_Company loObject = new ParamModels(poGRider).Company();
        loObject.initialize();
        loObject.openRecord(poGRider.getCompnyId());
        
        return loObject.getCompanyName();
    }

    public JSONObject searchCategory(String fsValue, boolean fbByCode) throws SQLException, GuanzonException {
        JSONObject loJSON;

        String lsSQL = "SELECT"
                + " sCategrCd"
                + ", sDescript"
                + " FROM Category  WHERE cRecdStat = '1'";

        if (fbByCode) {
            lsSQL = MiscUtil.addCondition(lsSQL, "sCategrCd = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, "sDescript LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }

        loJSON = ShowDialogFX.Search(
                poGRider,
                lsSQL,
                fsValue,
                "Code»Description",
                "sCategrCd»sDescript",
                "sCategrCd»sDescript",
                fbByCode ? 0 : 1);
        
        if (loJSON != null) {
            loJSON.put("result", "success");
            getModel().setCategoryCode((String) loJSON.get("sCategrCd"));
        } else {
            loJSON.put("result", "success");
            loJSON.put("message", "No record selected.");
        }

        return loJSON;
    }

    public JSONObject searchCompany(String fsValue ,boolean fbByCode) throws SQLException, GuanzonException, Exception {
        JSONObject loJSON = null;
        //retrieve records (value not empty), new entry (empty string)
        String lsSQL = "SELECT"
                    + " sClientID "
                    + ", sCompnyNm "
                    + " FROM Client_Master"
                    + " WHERE cRecdStat = " + SQLUtil.toSQL(RecordStatus.ACTIVE)
                    + " AND cClientTp = " + SQLUtil.toSQL(Logical.YES);

        loJSON = ShowDialogFX.Search(poGRider, 
                lsSQL, 
                fsValue, 
                "Client ID»Client Name", 
                "sClientID»sCompnyNm",
                "sClientID»sCompnyNm",
                fbByCode ? 0 : 1);

        if (loJSON == null) {
            return loJSON;
        }else{
            if ("error".equals(loJSON.get("result"))) {
                return loJSON;
            }
        }
        
        //Check Existing Supplier Accreditation Added by Arsiela - 05-23-2026s
        String lsClientId = loJSON.get("sClientID").toString();
        if(lsClientId != null && !"".equals(lsClientId)){
            //check existing record of client id to other supplier accreditation records
            loJSON = checkExistingSupplierRecord(lsClientId); //Moved script to method by Arsiela 05-23-2026 09:19 AM
            if ("error".equals((String) loJSON.get("result"))) {
                return loJSON;
            }
        }
        
        //initialize Client GUI
        ClientGUI loClient = new ClientGUI();

        loClient.setGRider(poGRider);
        loClient.setLogWrapper(null);
        loClient.setCategoryCode((String) getModel().getCategoryCode());

        //filter client type 
        loClient.setClientType(ClientType.INSTITUTION);

        //searchRecord(fsValue,fbByCode) will run make sure to set client and bycode
        //bycode true client id
        //bycode false company

        //set search by code
        loClient.setByCode(fbByCode);
        
        //set cilent id to load for company entry
        loClient.setClientId(lsClientId);
        
        //load record
        CommonUtils.showModal(loClient);

        //initialize new json for result
        JSONObject loResult = new JSONObject();

        //load if button 
        if (!loClient.isCancelled()) {
            lsClientId = loClient.getClientId();
            //check existing record of client id to other supplier accreditation records
            loJSON = checkExistingSupplierRecord(lsClientId); //Moved script to method by Arsiela 05-23-2026 09:19 AM
            if ("error".equals((String) loJSON.get("result"))) {
                return loJSON;
            }
//            //check existing record of client id to other supplier accreditation records
//            lsSQL = "SELECT " +
//                   " * " +
//                   " FROM " +
//                   " Account_Client_Accreditation";
//            lsSQL = MiscUtil.addCondition(lsSQL, 
//                                    " sClientID = " + SQLUtil.toSQL(lsClientId != null ? lsClientId : "" + " ") +
//                                    " AND (sTransNox <> " + SQLUtil.toSQL(poModel.getTransactionNo()) +
//                                    " AND cTranStat <> '4')"
//            );
//
//            ResultSet loRS = poGRider.executeQuery(lsSQL);
//            if (MiscUtil.RecordCount(loRS) > 0) {
//                loResult.put("result", "error");
//                loResult.put("message", "Client is already accredited as supplier!");
//                return loResult;
//            }
//            MiscUtil.close(loRS);

            //set company id for supplier accreditation
            getModel().setClientId(lsClientId != null ? lsClientId: "");
            //get address
            for(Model_Client_Address loAddr : loClient.getClient().AddressList()){
                //set primary address
                if (loAddr.isPrimaryAddress()) {
                    
                    getModel().setAddressId(loAddr.getAddressId()!= null ? loAddr.getAddressId() : "");
                    
                    getModel().ClientAddress().setBarangayId(loAddr.getBarangayId());
                    getModel().ClientAddress().setTownId(loAddr.getTownId());
                    break;
                }
            }

            //get contact
            for(Model_Client_Institution_Contact loContact : loClient.getClient().InstiContactList()){

                //set primary contact person of company for supplier accreditation and ap client master
                if (loContact.isPrimaryContactPersion()) {
                    getModel().setContactId(loContact.getContactPId()!= null ? loContact.getContactPId() : "");
                    break;
                }
            }
        }
        loResult.put("result", "success");
        return loResult;
    }
    
    public JSONObject addCompany() throws SQLException, GuanzonException, Exception {
        //initialize new json for result
        JSONObject loResult = new JSONObject();
        
        String lsClientId = poModel.getClientId();
        
        //initialize Client GUI
        ClientGUI loClient = new ClientGUI();

        loClient.setGRider(poGRider);
        loClient.setLogWrapper(null);
        loClient.setCategoryCode((String) getModel().getCategoryCode());

        //filter client type 
        loClient.setClientType(ClientType.INSTITUTION);

        //searchRecord(fsValue,fbByCode) will run make sure to set client and bycode
        //bycode true client id
        //bycode false company

        //set search by code
        loClient.setByCode(false);

        //set cilent empty, to create a new record
        //Arsiela - 05-22-2026 - Load Client of Create new Client
        loClient.setClientId(lsClientId);
        
        //load record
        CommonUtils.showModal(loClient);

        //load if button 
        if (!loClient.isCancelled()) {
            lsClientId = loClient.getClient().getModel().getClientId();
            //check existing record of client id to other supplier accreditation records
            loResult = checkExistingSupplierRecord(lsClientId); //Moved script to method by Arsiela 05-23-2026 09:19 AM
            if ("error".equals((String) loResult.get("result"))) {
                return loResult;
            }

            //set company id for supplier accreditation
            getModel().setClientId(lsClientId != null ? lsClientId : "");

            //get address
            for(Model_Client_Address loAddr : loClient.getClient().AddressList()){

                //set primary address
                if (loAddr.isPrimaryAddress()) {
                    
                    getModel().setAddressId(loAddr.getAddressId()!= null ? loAddr.getAddressId() : "");
                    
                    getModel().ClientAddress().setBarangayId(loAddr.getBarangayId());
                    getModel().ClientAddress().setTownId(loAddr.getTownId());
                    break;
                }
            }

            //get contact
            for(Model_Client_Institution_Contact loContact : loClient.getClient().InstiContactList()){

                //set primary contact person of company for supplier accreditation and ap client master
                if (loContact.isPrimaryContactPersion()) {
                    getModel().setContactId(loContact.getContactPId()!= null ? loContact.getContactPId() : "");
                    break;
                }
            }
        }
        loResult.put("result", "success");
        return loResult;
    }
    
    private JSONObject checkExistingSupplierRecord(String fsClientId) throws SQLException{
        JSONObject loResult = new JSONObject();
        //check existing record of client id to other supplier accreditation records
        String lsSQL = "SELECT " +
                        "* " +
                       "FROM " +
                        "Account_Client_Accreditation";

        lsSQL = MiscUtil.addCondition(lsSQL, 
                                 "sClientID = " + SQLUtil.toSQL(fsClientId != null ? fsClientId: "" + " ") +
                                "AND " +
                                 "(sTransNox <> " + SQLUtil.toSQL(poModel.getTransactionNo()) +
                                "AND " +
                                 "cTranStat <> '4')"
        );

        ResultSet loRS = poGRider.executeQuery(lsSQL);
        if (MiscUtil.RecordCount(loRS) > 0) {
            loResult.put("result", "error");
            loResult.put("message", "Client is already accredited as supplier!");
            return loResult;
        }
        MiscUtil.close(loRS);
        
        loResult.put("result", "success");
        loResult.put("message", "success");
        return loResult;
    }
    
    public JSONObject StatusChange(String fStatus) throws SQLException, GuanzonException, CloneNotSupportedException{
        poGRider.beginTrans(fStatus, fStatus, SOURCE_CODE, SOURCE_CODE);
        
        poJSON = statusChange(getModel().getTable(), getModel().getTransactionNo(), fStatus, true);
        if (poJSON.get("result").equals("error")) {
            poGRider.rollbackTrans();
        }
        poGRider.commitTrans();
        return poJSON;
    }

    public JSONObject CloseTransaction() throws SQLException, GuanzonException, CloneNotSupportedException {
        poJSON = new JSONObject();
        
        //initliaze ongoing record status, for validator
        psValidStatus = AccountAccreditationStatus.CONFIRMED;
        
        //initialize validator
        poJSON = isEntryOkay();
        if ("error".equals((String) poJSON.get("result"))) {
            return poJSON;
        }
        System.out.println(pbWthParent);
        poGRider.beginTrans("UPDATE STATUS", "ConfirmTransaction", SOURCE_CODE, getModel().getTransactionNo());
 

        //if Accreditation 
        if (getModel().getAccountType().equals("0")) {
            
            //initialize AP_Client_Object
            AP_Client_Master loObject = getAPClientMaster(getModel().getClientId());

            //check editmode if new or update
            if (loObject.getEditMode() == EditMode.ADDNEW) {
                
                loObject.getModel().setClientId(poModel.getClientId());
                loObject.getModel().setAddressId(poModel.getAddressId());
                loObject.getModel().setContactId(poModel.getContactId());
                loObject.getModel().setCategoryCode(poModel.getCategoryCode());
                loObject.getModel().setdateClientSince(poGRider.getServerDate());
                loObject.getModel().setBeginningDate(poGRider.getServerDate());
                
                //if blacklisting set  Inactive record 
                loObject.getModel().setRecordStatus(getModel().getTransactionType().equals("1") ? "0" : "1");
                
                poJSON = loObject.saveRecord();
                
                if ("error".equals((String) poJSON.get("result"))) {
                    return poJSON;
                }
            } else {
                
                //make sure its onready mode
                if (loObject.getEditMode() == EditMode.UPDATE) {
                    
                    //enter to update
                    loObject.updateRecord();
                    loObject.getModel().setClientId(poModel.getClientId());
                    loObject.getModel().setAddressId(poModel.getAddressId());
                    loObject.getModel().setContactId(poModel.getContactId());
                    loObject.getModel().setCategoryCode(poModel.getCategoryCode());
                    loObject.getModel().setdateClientSince(poGRider.getServerDate());
                    loObject.getModel().setBeginningDate(poGRider.getServerDate());
                    
                    //if blacklisting set  Inactive record 
                    loObject.getModel().setRecordStatus(getModel().getTransactionType().equals("1") ? "0" : "1");
                    poJSON = loObject.saveRecord();
                    
                    if ("error".equals((String) poJSON.get("result"))) {
                        return poJSON;
                    }
                }

            }
            
            //Generate Payee for Contact person - Arsiela 05-23-2026 09:58AM
            //Load all contact persion and check if payee was checked if none create a payee directly to the supplier
            System.out.println("---------------SAVING PAYEE------------------");
            ClientInfo loClient = new ClientControllers(poGRider, logwrapr).ClientInfo();
            loClient.initialize();
            loClient.setWithParentClass(true);
            loClient.setWithUI(false);
            
            poJSON = loClient.openClientRecord(getModel().getClientId());
            if (!"success".equals((String) poJSON.get("result"))) {
                poGRider.rollbackTrans();
                return poJSON;
            }
            
            /*
            BR - 05-23-2026 01:24 PM
            Magiinsert lang ito after ng confirmation/approval. 
            If cPayee is = 0, Payee name is company name and clientID and sAPclientid is equal to supplier id. If payee is = 1, 
            Payee name is the contact person and sAPCluientId is supplier id and sclientID is client id ni contact person;
            */
            Model_Payee object;
            boolean lbCPPayee = false;
            for(int lnCtr = 0;lnCtr < loClient.getInstiContactCount(); lnCtr++){
                object = new CashflowModels(poGRider).Payee();
                if(Logical.YES.equals(loClient.InstiContact(lnCtr).getcPayeexxx())){
                    poJSON = object.newRecord();
                    if (!"success".equals((String) poJSON.get("result"))) {
                        poGRider.rollbackTrans();
                        return poJSON;
                    }
                    object.setAPClientID(getModel().getClientId());
                    object.setClientID(loClient.InstiContact(lnCtr).getContactPId()); 
                    object.setPayeeName(loClient.InstiContact(lnCtr).getContactPersonName().trim());
                    object.setModifiedDate(poGRider.getServerDate());
                    object.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
                    poJSON = object.saveRecord();
                    if (!"success".equals((String) poJSON.get("result"))){
                        poGRider.rollbackTrans();
                        return poJSON;
                    }
                    
                    if(!lbCPPayee){
                        lbCPPayee = true;
                    }
                }
            }
            
            if(!lbCPPayee){
                object = new CashflowModels(poGRider).Payee();
                poJSON = object.newRecord();
                if (!"success".equals((String) poJSON.get("result"))) {
                    poGRider.rollbackTrans();
                    return poJSON;
                }
                object.setAPClientID(getModel().getClientId());
                object.setClientID(getModel().getClientId()); 
                object.setPayeeName(getModel().Client().getCompanyName().trim());
                object.setModifiedDate(poGRider.getServerDate());
                object.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
                poJSON = object.saveRecord();
                if (!"success".equals((String) poJSON.get("result"))) {
                    poGRider.rollbackTrans();
                    return poJSON;
                }
            }
            
            /**
             TODO: RESTRUCTURING OF SUPPLIER ACCREDITATION TABLE FOR PAYEE ID.
             **/
//            String lsSQL = "SELECT sCPerson1, cPayeexxx FROM Client_Institution_Contact_Person";
//            lsSQL = MiscUtil.addCondition(lsSQL, " sClientID = " + SQLUtil.toSQL(poModel.getClientId()));
//            
//            ResultSet loRS = poGRider.executeQuery(lsSQL);
//            
//            if (MiscUtil.RecordCount(loRS) > 0) {
//                
//                while (loRS.next()) {
//                    
//                    String lssCPerson1 = loRS.getString("sCPerson1");
//                    String lscPayeexxx = loRS.getString("cPayeexxx");
//                    
//                    lsSQL = "SELECT sPayeeIDx FROM Payee";
//                    lsSQL = MiscUtil.addCondition(lsSQL,
//                                " sClientID = " + SQLUtil.toSQL(poModel.getClientId()) +
//                            " AND" +
//                                " sPayeeNme = " + SQLUtil.toSQL(lssCPerson1));
//                    
//                    loRS = poGRider.executeQuery(lsSQL);
//                    
//                    if (loRS != null && loRS.next()) {
//                        
//                        
//                        poJSON = loObject.openRecord(loRS.getString("sPayeeIDx"));
//
//                        //if error, initialize as new record (no record found). else, update the retrieved record
//                        if ("error".equals((String) poJSON.get("result"))) {
//                            loObject.newRecord();
//                        }else{
//                            loObject.updateRecord();
//                        }
//                        
//                        Payee loPayee = getPayee(loRS.getString("sPayeeIDx"));
//                        loPayee.setRecordStatus("0");
//                        
//                        
//                        if (loPayee.getEditMode() == EditMode.ADDNEW) {
//                            
//                        }else{
//                            
//                        }
//                    }
//                }
//            }
        }
        
        if(psApprovalUser == null || "".equals(psApprovalUser)){
            psApprovalUser = poGRider.getUserID();
        }
        
        String lsSQL = "UPDATE "
                + poModel.getTable()
                + " SET sApproved= " + SQLUtil.toSQL(psApprovalUser)
                + ", dApproved= " + SQLUtil.toSQL(poGRider.getServerDate())
                + " WHERE sTransNox = " + SQLUtil.toSQL(getModel().getTransactionNo());

        Long lnResult = poGRider.executeQuery(lsSQL,
                getModel().getTable(),
                poGRider.getBranchCode(), "", "");
        if (lnResult <= 0L) {
            poGRider.rollbackTrans();

            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "Error updating the transaction status.");
            return poJSON;
        }
        
        //change status
        poJSON = statusChange(getModel().getTable(), (String) getModel().getValue("sTransNox"),"", psValidStatus, false,true);
        if (!"success".equals((String) poJSON.get("result"))) {
            poGRider.rollbackTrans();
            return poJSON;
        }

        poGRider.commitTrans();

        openRecord(getModel().getTransactionNo());
        
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        poJSON.put("message", "Transaction confirmed successfully.");

        return poJSON;
    }

    public JSONObject VoidTransaction() throws SQLException, GuanzonException, CloneNotSupportedException {
        poJSON = new JSONObject();

        //initliaze ongoing record status, for validator
        psValidStatus = AccountAccreditationStatus.VOID;
        
        //initialize validator
        poJSON = isEntryOkay();
        if ("error".equals((String) poJSON.get("result"))) {
            return poJSON;
        }
        poGRider.beginTrans("UPDATE STATUS", "VoidTransaction", SOURCE_CODE, getModel().getTransactionNo());

        String lsSQL = "UPDATE "
                + poModel.getTable()
                + " SET cTranStat = " + SQLUtil.toSQL("4")
                + " WHERE sTransNox = " + SQLUtil.toSQL(getModel().getTransactionNo());

        Long lnResult = poGRider.executeQuery(lsSQL,
                getModel().getTable(),
                poGRider.getBranchCode(), "", "");
        if (lnResult <= 0L) {
            poGRider.rollbackTrans();

            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "Error updating the transaction status.");
            return poJSON;
        }

        poGRider.commitTrans();

        openRecord(getModel().getTransactionNo());
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        poJSON.put("message", "Transaction voided successfully.");

        return poJSON;
    }
    
    public void ShowStatusHistory() throws SQLException, GuanzonException, Exception{
        
        CachedRowSet crs = getStatusHistory();
            crs.beforeFirst();
            
            while(crs.next()){
                
                switch (crs.getString("cRefrStat")){
                    case "":
                        crs.updateString("cRefrStat", "-");
                        break;
                    case AccountAccreditationStatus.OPEN:
                        crs.updateString("cRefrStat", "OPEN");
                        break;
                    case AccountAccreditationStatus.CONFIRMED:
                        crs.updateString("cRefrStat", "CONFIRMED");
                        break;
                    case AccountAccreditationStatus.VOID:
                        crs.updateString("cRefrStat", "VOID");
                        break;
                    default:
                        char ch = crs.getString("cRefrStat").charAt(0);
                        String stat = String.valueOf((int) ch - 64);

                        switch (stat){
                            
                            case AccountAccreditationStatus.OPEN:
                                crs.updateString("cRefrStat", "OPEN");
                                break;
                            case AccountAccreditationStatus.CONFIRMED:
                                crs.updateString("cRefrStat", "CONFIRMED");
                                break;
                            case AccountAccreditationStatus.VOID:
                                crs.updateString("cRefrStat", "VOID");
                                break;
                        }
                }
                crs.updateRow(); 
            }
            
            JSONObject loJSON  = getEntryBy();
            String entryBy = "";
            String entryDate = "";

            if ("success".equals((String) loJSON.get("result"))){
                entryBy = (String) loJSON.get("sCompnyNm");
                entryDate = (String) loJSON.get("sEntryDte");
            }

            showStatusHistoryUI("Supplier Accreditation", (String) getModel().getTransactionNo(), entryBy, entryDate, crs);
            
    }
    
    public JSONObject getEntryBy() throws SQLException, GuanzonException {
        
        poJSON = new JSONObject();
        
        String lsEntry = "";
        String lsEntryDate = "";
        String lsSQL =  " SELECT b.sModified, b.dModified " 
                        + " FROM Account_Client_Accreditation a "
                        + " LEFT JOIN xxxAuditLogMaster b ON b.sSourceNo = a.sTransNox AND b.sEventNme LIKE 'ADD%NEW' AND b.sRemarksx = " + SQLUtil.toSQL(poModel.getTable());
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransNox =  " + SQLUtil.toSQL(getModel().getTransactionNo())) ;
        System.out.println("Execute SQL : " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
          if (MiscUtil.RecordCount(loRS) > 0L) {
            if (loRS.next()) {
                if(loRS.getString("sModified") != null && !"".equals(loRS.getString("sModified"))){
                    if(loRS.getString("sModified").length() > 10){
                        lsEntry = getSysUser(poGRider.Decrypt(loRS.getString("sModified"))); 
                    } else {
                        lsEntry = getSysUser(loRS.getString("sModified")); 
                    }
                    // Get the LocalDateTime from your result set
                    LocalDateTime dModified = loRS.getObject("dModified", LocalDateTime.class);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
                    lsEntryDate =  dModified.format(formatter);
                }
            } 
          }
          MiscUtil.close(loRS);
        } catch (SQLException e) {
          poJSON.put("result", "error");
          poJSON.put("message", e.getMessage());
          return poJSON;
        } 
        
        poJSON.put("result", "success");
        poJSON.put("sCompnyNm", lsEntry);
        poJSON.put("sEntryDte", lsEntryDate);
        return poJSON;
    }
    
    public String getSysUser(String fsId) throws SQLException, GuanzonException {
        String lsEntry = "";
        String lsSQL =   " SELECT b.sCompnyNm from xxxSysUser a " 
                       + " LEFT JOIN Client_Master b ON b.sClientID = a.sEmployNo ";
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sUserIDxx =  " + SQLUtil.toSQL(fsId)) ;
        System.out.println("SQL " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
          if (MiscUtil.RecordCount(loRS) > 0L) {
            if (loRS.next()) {
                lsEntry = loRS.getString("sCompnyNm");
            } 
          }
          MiscUtil.close(loRS);
        } catch (SQLException e) {
          poJSON.put("result", "error");
          poJSON.put("message", e.getMessage());
        } 
        return lsEntry;
    }
}
