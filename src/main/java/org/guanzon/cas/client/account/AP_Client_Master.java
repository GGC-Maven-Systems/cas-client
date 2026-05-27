package org.guanzon.cas.client.account;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.agent.systables.SysTableContollers;
import org.guanzon.appdriver.agent.systables.TransactionAttachment;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscReplUtil;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.base.WebFile;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.iface.GValidator;
import org.guanzon.appdriver.token.RequestAccess;
import org.guanzon.cas.client.model.Model_AP_Client_Ledger;
import org.guanzon.cas.client.model.Model_AP_Client_Master;
import org.guanzon.cas.client.model.Model_AP_Client_Bank_Account;
import org.guanzon.cas.client.services.ClientModels;
import org.guanzon.cas.client.validator.APClientValidatorFactory;
import org.guanzon.cas.parameter.Banks;
import org.guanzon.cas.parameter.model.Model_Company;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.guanzon.cas.parameter.services.ParamModels;
import org.guanzon.cas.purchasing.services.POController;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AP_Client_Master extends Parameter {
    private final String SOURCE_CODE = "APCL";
    
    private static JSONObject token = null;

    private Model_AP_Client_Master poModel;
    private Model_AP_Client_Bank_Account poBankAccount;
    
    private List<Model_AP_Client_Ledger> paLedger;
    
    public List<TransactionAttachment> paAttachments;

    @SuppressWarnings("unchecked")
    public List<Model_AP_Client_Ledger> getLedgerList() {
        return (List<Model_AP_Client_Ledger>) (List<?>) paLedger;
    }

    @Override
    public void initialize() throws SQLException, GuanzonException {
        super.initialize();

        psRecdStat = TransactionStatus.STATE_OPEN;
        paLedger = new ArrayList<Model_AP_Client_Ledger>();

        ClientModels model = new ClientModels(poGRider);
        poModel = model.APClientMaster();
        poBankAccount = model.APClientBankAccount();
        
        paAttachments = new ArrayList<>();
    }

    @Override
    public JSONObject isEntryOkay() throws SQLException, GuanzonException, CloneNotSupportedException {
        poJSON = new JSONObject();

        //initialize validator
        GValidator loValidator = APClientValidatorFactory.make(poGRider.getIndustry());
        
        //initialize params for app validator
        loValidator.setApplicationDriver(poGRider);
        loValidator.setMaster(poModel);
        
        //validate
        poJSON = loValidator.validate();
        
        //if validation not success
        if ("error".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        //set modified date and id
        poModel.setModifyingId(poGRider.getUserID());
        poModel.setModifiedDate(poGRider.getServerDate());

        poJSON.put("result", "success");
        return poJSON;
    }
    
    private boolean lbValidate = false;
    public void validateEntry(boolean fbValidate){
        lbValidate = fbValidate;
    }

    @Override
    protected JSONObject willSave() throws SQLException, GuanzonException {
        poJSON = new JSONObject();
        if(lbValidate){
            if(poModel.getPayment() == null || "".equals(poModel.getPayment())){
                poJSON.put("result", "error");
                poJSON.put("message", "Payment type cannot be empty.");
                return poJSON;
            }
            if(poModel.getTermId() == null || "".equals(poModel.getTermId())){
                poJSON.put("result", "error");
                poJSON.put("message", "Term cannot be empty.");
                return poJSON;
            }
            if(poModel.getCreditLimit() <= 0.0000){
                poJSON.put("result", "error");
                poJSON.put("message", "Credit limit cannot be zero.");
                return poJSON;
            }
        
            System.out.println("-----------------------------VALIDATE BANK ACCOUNT------------------------------------------");
            if(poBankAccount != null){
                if(poBankAccount.getEditMode() == EditMode.ADDNEW || poBankAccount.getEditMode() == EditMode.UPDATE){
                    if(poBankAccount.getBankID() == null || "".equals(poBankAccount.getBankID())){
//                        if((poBankAccount.getAccountNumber() != null && !"".equals(poBankAccount.getAccountNumber()))
//                            || (poBankAccount.getAccountName() != null && !"".equals(poBankAccount.getAccountName()))){
                            poJSON.put("result", "error");
                            poJSON.put("message", "Bank cannot be empty.");
                            return poJSON;
//                        }
                    } 
//                    else {
                        if(poBankAccount.getAccountNumber() == null || "".equals(poBankAccount.getAccountNumber())){
                            poJSON.put("result", "error");
                            poJSON.put("message", "Account number cannot be empty.");
                            return poJSON;
                        }
                        if(poBankAccount.getAccountName() == null || "".equals(poBankAccount.getAccountName())){
                            poJSON.put("result", "error");
                            poJSON.put("message", "Account name cannot be empty.");
                            return poJSON;
                        }
//                    }

                }
            }
            System.out.println("-----------------------------------------------------------------------");
        
        }
        //assign other info on attachment
        for (int lnCtr = 0; lnCtr <= getTransactionAttachmentCount()- 1; lnCtr++) {
            TransactionAttachmentList(lnCtr).getModel().setSourceNo(poModel.getClientId());
            TransactionAttachmentList(lnCtr).getModel().setSourceCode(SOURCE_CODE);
            TransactionAttachmentList(lnCtr).getModel().setBranchCode(poGRider.getBranchCode());
            TransactionAttachmentList(lnCtr).getModel().setImagePath(System.getProperty("sys.default.path.temp.attachments"));

            String lsOriginalFileName = TransactionAttachmentList(lnCtr).getModel().getFileName();
            //Check existing file name in database
            if(EditMode.ADDNEW == TransactionAttachmentList(lnCtr).getModel().getEditMode()){
                int lnCopies = 0;
                String fsFilePath = TransactionAttachmentList(lnCtr).getModel().getImagePath() + "/" + TransactionAttachmentList(lnCtr).getModel().getFileName();
                String lsNewFileName = TransactionAttachmentList(lnCtr).getModel().getFileName();
                while ("error".equals((String)checkExistingFileName(lsNewFileName).get("result"))) {
                    lnCopies++;
                    //Rename the file
                    int dotIndex = TransactionAttachmentList(lnCtr).getModel().getFileName().lastIndexOf(".");
                    if (dotIndex == -1) {
                        lsNewFileName = TransactionAttachmentList(lnCtr).getModel().getFileName() +"_"+lnCopies;
                    } else {
                        lsNewFileName = TransactionAttachmentList(lnCtr).getModel().getFileName().substring(0, dotIndex) +"_"+ lnCopies +TransactionAttachmentList(lnCtr).getModel().getFileName().substring(dotIndex);
                    }
                }

                if(lnCopies > 0){
                    Path source = Paths.get(fsFilePath);
                    try {
                        // Copy file into the target directory with a new name
                        Path target = Paths.get(System.getProperty("sys.default.path.temp.attachments")).resolve(lsNewFileName);
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                        //check if file is existing
                        int lnChecker = 0;
                        File file = new File(TransactionAttachmentList(lnCtr).getModel().getImagePath() + "/" + lsNewFileName);
                        while(!file.exists() && lnChecker < 5){
                            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);  
                            System.out.println("Re-Copying... " + lnChecker);
                            lnChecker++;
                        }
                        TransactionAttachmentList(lnCtr).getModel().setFileName(lsNewFileName);
                        System.out.println("File copied successfully as " + lsNewFileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            //Upload Attachment when send status is 0
            try {
                if("0".equals(TransactionAttachmentList(lnCtr).getModel().getSendStatus())){
                    poJSON = uploadCASAttachments(poGRider, System.getProperty("sys.default.access.token"), lnCtr,lsOriginalFileName);
                    if ("error".equals((String) poJSON.get("result"))) {
                        return poJSON;
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                poJSON = setJSON("error", MiscUtil.getException(ex));
                return poJSON;
            }
        }
        
        poJSON.put("result", "success");
        return poJSON;
    }

    @Override
    protected JSONObject saveOthers() throws SQLException, GuanzonException {
        try {
            
            //Save Bank Account
            System.out.println("-----------------------------SAVE BANK ACCOUNT------------------------------------------");
            if(poBankAccount != null){
                if(poBankAccount.getEditMode() == EditMode.ADDNEW || poBankAccount.getEditMode() == EditMode.UPDATE){
                    if(poBankAccount.getBankID() != null && !"".equals(poBankAccount.getBankID())
                        && (poBankAccount.getAccountNumber() != null && !"".equals(poBankAccount.getAccountNumber()))
                        && (poBankAccount.getAccountName() != null && !"".equals(poBankAccount.getAccountName()))){
                            poBankAccount.setClientID(getModel().getClientId());
                            poBankAccount.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
                            poBankAccount.setModifiedDate(poGRider.getServerDate());
                            poBankAccount.setRecordStatus(RecordStatus.ACTIVE);
                            poJSON = poBankAccount.saveRecord();
                            if (!isJSONSuccess(poJSON)) {
                                return poJSON;
                            }
                    }
                }
            }
            System.out.println("-----------------------------------------------------------------------");
            
            //Save Attachments
                System.out.println("-----------------------------SAVE TRANSACTION ATTACHMENT------------------------------------------");
            for (int lnCtr = 0; lnCtr <= getTransactionAttachmentCount() - 1; lnCtr++) {
                if (paAttachments.get(lnCtr).getEditMode() == EditMode.ADDNEW || paAttachments.get(lnCtr).getEditMode() == EditMode.UPDATE) {
                    paAttachments.get(lnCtr).getModel().setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
                    paAttachments.get(lnCtr).getModel().setModifiedDate(poGRider.getServerDate());
                    paAttachments.get(lnCtr).setWithParentClass(true);
                    poJSON = paAttachments.get(lnCtr).saveRecord();
                    if (!isJSONSuccess(poJSON)) {
                        return poJSON;
                    }
                }
            }
            System.out.println("-----------------------------------------------------------------------");

        } catch (SQLException | GuanzonException | CloneNotSupportedException  ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public String getFullAddress() throws SQLException, GuanzonException{
        String lsHouseNo = poModel.ClientAddress().getHouseNo() == null || poModel.ClientAddress().getHouseNo().isEmpty() ? "" : poModel.ClientAddress().getHouseNo();
        String lsAddress = poModel.ClientAddress().getAddress() == null || poModel.ClientAddress().getAddress().isEmpty() ? "" : poModel.ClientAddress().getAddress();
        String lsBrgy = poModel.ClientAddress().Barangay().getBarangayName() == null || poModel.ClientAddress().Barangay().getBarangayName().isEmpty() ? "" : poModel.ClientAddress().Barangay().getBarangayName();
        String lsTown = poModel.ClientAddress().Town().getDescription() == null || poModel.ClientAddress().Town().getDescription().isEmpty() ? "" : poModel.ClientAddress().Town().getDescription();
        String lsProvince = poModel.ClientAddress().Town().Province().getDescription() == null || poModel.ClientAddress().Town().Province().getDescription().isEmpty() ? "" : poModel.ClientAddress().Town().Province().getDescription();
        
        String lsFullAddress = "";
        if(!lsHouseNo.isEmpty()){
            lsFullAddress = lsHouseNo;
        }
        if(!lsAddress.isEmpty()){
            if(!lsFullAddress.isEmpty()){
                lsFullAddress = lsFullAddress + " " + lsAddress;
            } else {
                lsFullAddress = lsAddress;
            }
        }
        if(!lsBrgy.isEmpty()){
            if(!lsFullAddress.isEmpty()){
                lsFullAddress = lsFullAddress + " " + lsBrgy;
            } else {
                lsFullAddress = lsBrgy;
            }
        }
        if(!lsTown.isEmpty()){
            if(!lsFullAddress.isEmpty()){
                lsFullAddress = lsFullAddress + " " + lsTown;
            } else {
                lsFullAddress = lsTown;
            }
        }
        if(!lsProvince.isEmpty()){
            if(!lsFullAddress.isEmpty()){
                lsFullAddress = lsFullAddress + ", " + lsProvince;
            } else {
                lsFullAddress = lsProvince;
            }
        }
        
        if(lsFullAddress == null){
            lsFullAddress = "";
        } else {
            lsFullAddress = lsFullAddress.trim();
        }
        System.out.println("Full Address : " + lsFullAddress);
        return lsFullAddress;
    }

    @Override
    public Model_AP_Client_Master getModel() {
        return poModel;
    }

    @Override
    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException {
        String lsSQL = getSQ_Browse();
        System.out.println("SQL : " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "Client ID»Name»Address»Contact Person",
                "sClientID»sCompnyNm»xAddressx»xContactP",
                "a.sClientID»IFNULL(b.sCompnyNm,'')»TRIM(CONCAT(c.sHouseNox, ', ', c.sAddressx, ', ', c.sBrgyIDxx, ', ', c.sTownIDxx))»d.sCPerson1",
                byCode ? 0 : 1);

        if (poJSON != null) {
            poJSON = poModel.openRecord((String) poJSON.get("sClientID"));
            if (poJSON.get("result").toString().equalsIgnoreCase("success")) {
                //clear retrieve ledger
                paLedger.clear();
                //if client tagged as confirmed supplier, get date approved and set as date for client since. else set server date
                lsSQL = "SELECT " +
                        "* " +
                       "FROM " +
                        "Account_Client_Accreditation";

                lsSQL = MiscUtil.addCondition(lsSQL, 
                                        " sClientID = " + SQLUtil.toSQL(poModel.getClientId()!= null ? poModel.getClientId(): "" + " ") +
                                        " AND cTranStat = '1' "
                );

                lsSQL = lsSQL + 
                           " ORDER BY " +
                            "dTimeStmp " +
                           "DESC " +
                            "LIMIT 1";

                ResultSet loRS = poGRider.executeQuery(lsSQL);
                if (MiscUtil.RecordCount(loRS) > 0) {
                    while (loRS.next()) {
                        poModel.setdateClientSince(loRS.getDate("dApproved") == null ? poGRider.getServerDate() : loRS.getDate("dApproved"));
                    }
                }else{
                    poModel.setdateClientSince(poGRider.getServerDate());
                }
                MiscUtil.close(loRS);

            }
            return poJSON;
            
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

            lsCondition = "a.cRecdStat IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "a.cRecdStat = " + SQLUtil.toSQL(psRecdStat);
        }

        lsSQL = "SELECT" +
                    "  a.sClientID" +
                    ", a.sAddrssID" +
                    ", a.sContctID" +
                    ", a.sCategrCd" +
                    ", a.dCltSince" +
                    ", a.dBegDatex" +
                    ", a.nBegBalxx" +
                    ", a.sTermIDxx" +
                    ", a.nDiscount" +
                    ", a.nCredLimt" +
                    ", a.nABalance" +
                    ", a.nOBalance" +
                    ", a.nLedgerNo" +
                    ", a.cVatablex" +
                    ", a.cHoldAcct" +
                    ", a.cAutoHold" +
                    ", a.cRecdStat" +
                    ", b.sCompnyNm" +
                    ", c.sAddrssID" +
                    ", d.sMobileNo" +
                    ", TRIM(CONCAT(c.sHouseNox, ', ', c.sAddressx, ', ', c.sBrgyIDxx, ', ', c.sTownIDxx)) xAddressx" +
                    ", d.sCPerson1 xContactP" +
                " FROM AP_Client_Master a" +
                    " LEFT JOIN Client_Master b" +
                        " ON a.sClientID = b.sClientID" +
                    " LEFT JOIN Client_Address c" +
                        " ON a.sAddrssID = c.sAddrssID" +
                    " LEFT JOIN Client_Institution_Contact_Person d" +
                        " ON a.sContctID = d.sContctID";

        if (!lsCondition.isEmpty()) {
            lsSQL = MiscUtil.addCondition(lsSQL, lsCondition);
        }

        return lsSQL;
    }

    public JSONObject searchTerm(String fsValue, boolean fbByCode) throws SQLException, GuanzonException {
        JSONObject loJSON;

        String lsSQL = "SELECT"
                + " sTermCode"
                + " , sDescript"
                + " FROM Term WHERE cRecdStat = '1'";

        if (fbByCode) {
            lsSQL = MiscUtil.addCondition(lsSQL, "sTermCode = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, "sDescript LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }

        loJSON = ShowDialogFX.Search(
                poGRider,
                lsSQL,
                fsValue,
                "Code»Description",
                "sTermCode»sDescript",
                "sTermCode»sDescript",
                fbByCode ? 0 : 1);

        if (loJSON != null) {
            loJSON.put("result", "success");
            getModel().setTermId((String) loJSON.get("sTermCode"));
        } else {
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record selected.");
        }

        return loJSON;
    }
    
    public JSONObject searchBank(String fsValue, boolean fbByCode) throws SQLException, GuanzonException {
        Banks loObject = new ParamControllers(poGRider, logwrapr).Banks();
        loObject.initialize();
        loObject.setRecordStatus(RecordStatus.ACTIVE);
        poJSON = loObject.searchRecord(fsValue, fbByCode);
        if (isJSONSuccess(poJSON)) {
           poJSON = BankAccount().setBankID(loObject.getModel().getBankID());
        }

        return poJSON;
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
        System.out.println("Category Query = " + lsSQL);
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
            loJSON = new JSONObject();
            loJSON.put("result", "success");
            loJSON.put("message", "No record selected.");
        }

        return loJSON;
    }

    public JSONObject loadLedgerList(String fsDateFrom, String fsDateTo) throws SQLException, GuanzonException, CloneNotSupportedException {
        poJSON = new JSONObject();
        if (getModel().getClientId() == null || "".equals(getModel().getClientId())) {
            poJSON.put("result", "error");
            poJSON.put("message", "No record Loaded. Please load Client");
            return poJSON;
        }
        paLedger.clear();
        String lsSQL = "SELECT "
                + " a.sClientID"
                + ", b.nLedgerNo"
                + ", b.dTransact"
                + " FROM AP_Client_Master a "
                + " LEFT JOIN AP_Client_Ledger b ON a.sClientID = b.sClientID "
                + " ORDER BY b.dTransact ASC";

        lsSQL = MiscUtil.addCondition(lsSQL, " a.sClientID =" + SQLUtil.toSQL(getModel().getClientId())
                                            + " AND b.dTransact BETWEEN " + SQLUtil.toSQL(fsDateFrom)
                                            + " AND " + SQLUtil.toSQL(fsDateTo)
                                                );
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        System.out.println("Load list query is " + lsSQL);

        if (MiscUtil.RecordCount(loRS) <= 0) {
        } else {
            while (loRS.next()) {
                Model_AP_Client_Ledger loLedger = new ClientModels(poGRider).APClientLedger();
                poJSON = loLedger.openRecord(loRS.getString("sClientID"), loRS.getString("nLedgerNo"));
                if ("success".equals((String) poJSON.get("result"))) {
                    paLedger.add(loLedger);
                } else {
                    return poJSON;
                }
            }
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }
    
    
    public Model_AP_Client_Bank_Account BankAccount(){
        if (poBankAccount == null) {
            poBankAccount = new ClientModels(poGRider).APClientBankAccount();
            poBankAccount.initialize();
        }
        return poBankAccount;
    }

    public JSONObject loadBankAccount() throws SQLException, GuanzonException, CloneNotSupportedException {
        poJSON = new JSONObject();
        if (getModel().getClientId() == null || "".equals(getModel().getClientId())) {
            poJSON.put("result", "error");
            poJSON.put("message", "No record Loaded. Please load Client");
            return poJSON;
        }
        
        if(poBankAccount == null || getEditMode() == EditMode.READY){
            poBankAccount = new ClientModels(poGRider).APClientBankAccount();
            poBankAccount.initialize();
        }
        
        String lsSQL = "SELECT "
                + " sAPBnkIDx"
                + " FROM AP_Client_Bank_Account "
                + " ORDER BY sAPBnkIDx";

        lsSQL = MiscUtil.addCondition(lsSQL, " sClientID = " + SQLUtil.toSQL(getModel().getClientId()));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        System.out.println("Load list query is " + lsSQL);

        if (MiscUtil.RecordCount(loRS) <= 0) {
            poJSON = poBankAccount.newRecord();
            if (!"success".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
        }

        if(loRS.next()) {
            poJSON = poBankAccount.openRecord(loRS.getString("sAPBnkIDx"));
            if (!"success".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
            
            if(getEditMode() == EditMode.UPDATE && poBankAccount.getEditMode() != EditMode.UPDATE){
                poJSON = poBankAccount.updateRecord();
                if (!"success".equals((String) poJSON.get("result"))) {
                    return poJSON;
                }
            }
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }

    
    /**
     * Retrieves and downloads all attachments associated with the current transaction.
     * 
     * This method fetches attachment metadata from the system tables, populates the
     * {@code paAttachments} collection, and performs a web download for each file. 
     * Downloaded files are decoded from Base64 and saved to the system's temporary 
     * attachment directory defined in the system properties.
     * 
     * @return A {@link JSONObject} indicating the overall success of the attachment loading process.
     * @throws SQLException If a database error occurs while fetching attachment records.
     * @throws GuanzonException If an error occurs during file synchronization or processing.
     */
    public JSONObject loadAttachments()
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();
        paAttachments = new ArrayList<>();
        String lsSourceCode = "";
        TransactionAttachment loAttachment = new SysTableContollers(poGRider, null).TransactionAttachment();
        List loList = loAttachment.getAttachments(SOURCE_CODE, getModel().getClientId());
        lsSourceCode = SOURCE_CODE;
        for (int lnCtr = 0; lnCtr <= loList.size() - 1; lnCtr++) {
            paAttachments.add(TransactionAttachment());
            poJSON = paAttachments.get(getTransactionAttachmentCount() - 1).openRecord((String) loList.get(lnCtr));
            if (isJSONSuccess(poJSON)) {
                if(getModel().getEditMode() == EditMode.UPDATE){
                   poJSON = paAttachments.get(getTransactionAttachmentCount() - 1).updateRecord();
                }
                System.out.println(paAttachments.get(getTransactionAttachmentCount() - 1).getModel().getTransactionNo());
                System.out.println(paAttachments.get(getTransactionAttachmentCount() - 1).getModel().getSourceNo());
                System.out.println(paAttachments.get(getTransactionAttachmentCount() - 1).getModel().getSourceCode());
                System.out.println(paAttachments.get(getTransactionAttachmentCount() - 1).getModel().getFileName());
            }
            
            //Download Attachments
            poJSON = WebFile.DownloadFile(WebFile.getAccessToken(System.getProperty("sys.default.access.token"))
                    , "0032" //Constant
                    , "" //Empty
                    , paAttachments.get(getTransactionAttachmentCount() - 1).getModel().getFileName()
                    , lsSourceCode
                    , paAttachments.get(getTransactionAttachmentCount() - 1).getModel().getSourceNo()
                    , "");
            if (isJSONSuccess(poJSON)) {
                
                poJSON = (JSONObject) poJSON.get("payload");
                if(WebFile.Base64ToFile((String) poJSON.get("data")
                        , (String) poJSON.get("hash")
                        , System.getProperty("sys.default.path.temp.attachments") + "/"
                        , (String) poJSON.get("filename"))){
                    System.out.println("poJSON success: " +  poJSON.toJSONString());
                    System.out.println("File downloaded succesfully.");
                } else {
                    poJSON = (JSONObject) poJSON.get("error");
                    poJSON.put("result", "error");
                    System.out.println("ERROR WebFile.DownloadFile: " + poJSON.get("message"));
                    System.out.println("poJSON error WebFile.DownloadFile: " + poJSON.toJSONString());
                }
                
            } else {
                System.out.println("poJSON error WebFile.DownloadFile: " + poJSON.toJSONString());
            }
        }
        
        poJSON = setJSON("success","success");
        return poJSON;
    }
    /**
     * Instantiates a new TransactionAttachment controller.
     */
    private TransactionAttachment TransactionAttachment()
            throws SQLException,
            GuanzonException {
        return new SysTableContollers(poGRider, null).TransactionAttachment();
    }
    /**
     * Retrieves the attachment record at the specified index.
     * @param row The zero-based index of the attachment.
     */
    public TransactionAttachment TransactionAttachmentList(int row) {
        return (TransactionAttachment) paAttachments.get(row);
    }
    /**
     * Returns the total count of attachments in the current list.
     */
    public int getTransactionAttachmentCount() {
        if (paAttachments == null) {
            paAttachments = new ArrayList<>();
        }

        return paAttachments.size();
    }
    /**
     * Appends a new, empty attachment record to the collection.
     * @return A {@link JSONObject} indicating the success of the addition.
     * @throws SQLException, GuanzonException if initialization fails.
     */    
    public JSONObject addAttachment()
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        if (paAttachments.isEmpty()) {
            paAttachments.add(TransactionAttachment());
            poJSON = paAttachments.get(getTransactionAttachmentCount() - 1).newRecord();
        } else {
            if (!paAttachments.get(paAttachments.size() - 1).getModel().getTransactionNo().isEmpty()) {
                paAttachments.add(TransactionAttachment());
            } else {
                poJSON = setJSON("error", "Unable to add transaction attachment.");
                return poJSON;
            }
        }
        poJSON = setJSON("success", "success");
        return poJSON;
    }
    /**
     * Removes a new attachment or marks an existing one as Inactive.
     * @param fnRow Index of the attachment to remove/deactivate.
     * @return Result status as a {@link JSONObject}.
     */    
    public JSONObject removeAttachment(int fnRow) throws GuanzonException, SQLException{
        poJSON = new JSONObject();
        if(getTransactionAttachmentCount() <= 0){
            poJSON = setJSON("error", "No transaction attachment to be removed.");
            return poJSON;
        }
        
        if(paAttachments.get(fnRow).getEditMode() == EditMode.ADDNEW){
            paAttachments.remove(fnRow);
            System.out.println("Attachment :"+ fnRow+" Removed");
        } else {
            paAttachments.get(fnRow).getModel().setRecordStatus(RecordStatus.INACTIVE);
            System.out.println("Attachment :"+ fnRow+" Deactivate");
        }
        
        poJSON.put("result", "success");
        return poJSON;
    }
    /**
     * Adds an attachment by filename or reactivates it if it already exists as Inactive.
     * @param fFileName The name of the file to add.
     * @return The index of the added or reactivated attachment.
     */    
    public int addAttachment(String fFileName) throws SQLException, GuanzonException{
        for(int lnCtr = 0;lnCtr <= getTransactionAttachmentCount() - 1;lnCtr++){
            if(fFileName.equals(paAttachments.get(lnCtr).getModel().getFileName())
                && RecordStatus.INACTIVE.equals(paAttachments.get(lnCtr).getModel().getRecordStatus())){
                paAttachments.get(lnCtr).getModel().setRecordStatus(RecordStatus.ACTIVE);
                System.out.println("Attachment :"+ lnCtr+" Activate");
                return lnCtr;
            }
        }
        
        addAttachment();
        paAttachments.get(getTransactionAttachmentCount() - 1).getModel().setFileName(fFileName);
        paAttachments.get(getTransactionAttachmentCount() - 1).getModel().setSourceNo(getModel().getClientId());
        paAttachments.get(getTransactionAttachmentCount() - 1).getModel().setRecordStatus(RecordStatus.ACTIVE);
        return getTransactionAttachmentCount() - 1;
    }
    
    /**
     * Copies a file from the source path to the system's temporary attachment directory.
     * <p>
     * Includes a retry mechanism that attempts to re-copy the file up to 5 times 
     * if the target file is not immediately detected after the initial operation.
     * 
     * @param fsPath The absolute path of the source file to be copied.
     */
    public void copyFile(String fsPath){
        Path source = Paths.get(fsPath);
        Path targetDir = Paths.get(System.getProperty("sys.default.path.temp.attachments"));

        try {
            // Ensure target directory exists
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // Copy file into the target directory
            Files.copy(source, targetDir.resolve(source.getFileName()),
                       StandardCopyOption.REPLACE_EXISTING);

            //check if file is existing
            int lnChecker = 0;
            File file = new File(targetDir+ "/" + source.getFileName());
            System.out.println("File Path : " + file.getPath());
            while(!file.exists() && lnChecker < 5){
                Files.copy(source, targetDir.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);  
                System.out.println("Re-Copying... " + lnChecker);
                lnChecker++;
            }
            
            if(!file.exists()){
                System.out.println("File did not copy!");
                return;
            } else {
                System.out.println("File copied successfully!");
            } 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Checks if a filename already exists in the attachment database records.
     * 
     * @param fsFileName The name of the file to validate.
     * @return A {@link JSONObject} containing an error message if the filename exists, 
     *         otherwise an empty success object.
     * @throws SQLException, GuanzonException If a database access error occurs.
     */
    public JSONObject checkExistingFileName(String fsFileName) throws SQLException, GuanzonException{
        poJSON = new JSONObject();
        
        String lsSQL = MiscUtil.addCondition(MiscUtil.makeSelect(TransactionAttachment().getModel()), 
                                                                    " sFileName = " + SQLUtil.toSQL(fsFileName)
                                                                    );
        System.out.println("Executing SQL: " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
            if (MiscUtil.RecordCount(loRS) > 0) {
                if(loRS.next()){
                    if(loRS.getString("sFileName") != null && !"".equals(loRS.getString("sFileName"))){
                        poJSON = setJSON("error", "File name already exist in database.\nTry changing the file name to upload.");
                    }
                }
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            System.out.println("No record loaded.");
        }
        return poJSON;
    }
    
    /**
     * Resets the other record to its default initial state.
     */
    public void resetOthers() {
        paAttachments = new ArrayList<>();
    }
    
    /**
     * Uploads a specific transaction attachment to the web server.
     * <p>
     * This method verifies the file's existence (handling renames), generates an MD5 hash 
     * for data integrity, and transmits the file via the {@link WebFile} API. Upon successful 
     * upload, it updates the record's hash and sets the send status to "1" (Sent).
     * 
     * @param instance The application driver instance.
     * @param access The access token for web service authentication.
     * @param fnRow The index of the attachment in the local collection.
     * @param fsOriginalFileName The original filename to use as a fallback if the new file is missing.
     * @return A {@link JSONObject} containing the upload result status.
     * @throws Exception If an error occurs during file reading, encoding, or web transmission.
     */
    public JSONObject uploadCASAttachments(GRiderCAS instance, String access, int fnRow, String fsOriginalFileName) throws Exception{       
        poJSON = new JSONObject();
        System.out.println("Uploading... : fsOriginalFileName : " + fsOriginalFileName);
        System.out.println("New File Name... : " + paAttachments.get(fnRow).getModel().getFileName());
        String hash;
        String lsFile = paAttachments.get(fnRow).getModel().getFileName();
        
        //check if new file is existing
        File file = new File(paAttachments.get(fnRow).getModel().getImagePath() + "/" + lsFile);
        if(!file.exists()){
            //check if original file is existing
            lsFile = fsOriginalFileName;
            file = new File(paAttachments.get(fnRow).getModel().getImagePath() + "/" + lsFile);
            if(!file.exists()){
                poJSON = setJSON("error", "Cannot locate file in " + paAttachments.get(fnRow).getModel().getImagePath() + "/" + lsFile
                                        + ".\nContact system administrator for assistance.");
                return poJSON;  
            }
        }

        //check if file hash is not empty
        hash = paAttachments.get(fnRow).getModel().getMD5Hash();
        if(paAttachments.get(fnRow).getModel().getMD5Hash() == null || "".equals(paAttachments.get(fnRow).getModel().getMD5Hash())){
            hash = MiscReplUtil.md5Hash(paAttachments.get(fnRow).getModel().getImagePath() + "/" + lsFile);
        }

        JSONObject result = WebFile.UploadFile(getAccessToken(access)
                                , "0032"
                                , ""
                                , paAttachments.get(fnRow).getModel().getFileName()
                                , instance.getBranchCode()
                                , hash
                                , encodeFileToBase64Binary(file)
                                , paAttachments.get(fnRow).getModel().getSourceCode()
                                , paAttachments.get(fnRow).getModel().getSourceNo()
                                , "");

        if("error".equalsIgnoreCase((String) result.get("result"))){
            System.out.println("Upload Error : " + result.toJSONString());
            System.out.println("Upload Error : " + paAttachments.get(fnRow).getModel().getFileName());
                poJSON = setJSON("error", "System error while uploading file "+ paAttachments.get(fnRow).getModel().getFileName()
                                    + ".\nContact system administrator for assistance.");
            return poJSON;
        }
        paAttachments.get(fnRow).getModel().setMD5Hash(hash);
        paAttachments.get(fnRow).getModel().setSendStatus("1");
        System.out.println("Upload Success : " + paAttachments.get(fnRow).getModel().getFileName());
        poJSON.put("result", "success");
        return poJSON;
    }
    
    /**
     * Converts a file's content into a Base64 encoded string.
     * 
     * @param file The file object to be encoded.
     * @return A Base64 string representation of the file.
     * @throws Exception If an I/O error occurs during file reading.
     */
    private static String encodeFileToBase64Binary(File file) throws Exception{
         FileInputStream fileInputStreamReader = new FileInputStream(file);
         byte[] bytes = new byte[(int)file.length()];
         fileInputStreamReader.read(bytes);
         return new String(Base64.encodeBase64(bytes), "UTF-8");
     }
         
    /**
     * Retrieves a valid access token, refreshing it if it has expired.
     * <p>
     * The token is considered stale if it was created more than 25 minutes ago. 
     * If expired, it triggers an external request to update the token file before 
     * returning the new access key.
     * 
     * @param access The file path to the JSON formatted token storage.
     * @return The access key string, or {@code null} if the file cannot be read or parsed.
     */
    private static String getAccessToken(String access){
        try {
            JSONParser oParser = new JSONParser();
            if(token == null){
                token = (JSONObject)oParser.parse(new FileReader(access));
            }
            
            Calendar current_date = Calendar.getInstance();
            current_date.add(Calendar.MINUTE, -25);
            Calendar date_created = Calendar.getInstance();
            date_created.setTime(SQLUtil.toDate((String) token.get("created") , SQLUtil.FORMAT_TIMESTAMP));
            
            //Check if token is still valid within the time frame
            //Request new access token if not in the current period range
            if(current_date.after(date_created)){
                String[] xargs = new String[] {(String) token.get("parent"), access};
                RequestAccess.main(xargs);
                token = (JSONObject)oParser.parse(new FileReader(access));
            }
            
            return (String)token.get("access_key");
        } catch (IOException ex) {
            return null;
        } catch (ParseException ex) {
            return null;
        }
    }
    
    public String getCompany() throws SQLException, GuanzonException{
        Model_Company loObject = new ParamModels(poGRider).Company();
        loObject.initialize();
        loObject.openRecord(poGRider.getCompnyId());
        
        return loObject.getCompanyName();
    }
    
    /**
    * Creates a JSONObject with "result" and "message" fields.
    *
    * @param fsResult  The result value (e.g., "success", "error")
    * @param fsMessage The message describing the result
    * @return JSONObject containing the result and message
    */
    private JSONObject setJSON(String fsResult, String fsMessage) {
        JSONObject loJSON = new JSONObject();
        loJSON.put("result", fsResult);
        loJSON.put("message", fsMessage);
        return loJSON;
    }

    /**
     * Checks whether a JSONObject indicates a successful result.
     *
     * Returns true if the "result" field equals "success" or is not "error".
     *
     * @param foJSON The JSONObject to check
     * @return true if successful, false otherwise
     */
    public boolean isJSONSuccess(JSONObject foJSON) {
        return ("success".equals((String) foJSON.get("result")) || !"error".equals((String) foJSON.get("result")));
    }
}
