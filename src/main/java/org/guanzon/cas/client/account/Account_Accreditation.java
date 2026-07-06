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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import org.apache.commons.codec.binary.Base64;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.agent.systables.SysTableContollers;
import org.guanzon.appdriver.agent.systables.TransactionAttachment;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscReplUtil;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.base.WebFile;
import org.guanzon.appdriver.constant.ClientType;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.appdriver.iface.GValidator;
import org.guanzon.appdriver.token.RequestAccess;
import org.guanzon.cas.client.ClientGUI;
import org.guanzon.cas.client.ClientInfo;
import org.guanzon.cas.client.constants.AccountAccreditationStatus;
import org.guanzon.cas.client.model.Model_Account_Client_Accreditation;
import org.guanzon.cas.client.model.Model_Client_Address;
import org.guanzon.cas.client.model.Model_Client_Institution_Contact;
import org.guanzon.cas.client.services.ClientControllers;
import org.guanzon.cas.client.services.ClientModels;
import org.guanzon.cas.client.validator.ClientAccreditationValidatorFactory;
import org.guanzon.cas.parameter.model.Model_Company;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ph.com.guanzongroup.cas.cashflow.model.Model_Payee;
import ph.com.guanzongroup.cas.cashflow.services.CashflowModels;

public class Account_Accreditation extends Parameter {
    private final String SOURCE_CODE = "APCL"; //Same acccount code for AP Client; Arsiela 07-03-2026
    
    private Model_Account_Client_Accreditation poModel;
    private String psValidStatus = AccountAccreditationStatus.OPEN;
    private String psApprovalUser = "";
    
    public List<TransactionAttachment> paAttachments; //Added functionality for Attachement; Arsiela 07-03-2026
    private static JSONObject token = null;

    @Override
    public void initialize() throws SQLException, GuanzonException {
        super.initialize();

        ClientModels model = new ClientModels(poGRider);
        psRecdStat = TransactionStatus.STATE_OPEN;
        poModel = model.ClientAccreditation();
        paAttachments = new ArrayList<>();
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
        if(!AccountAccreditationStatus.OPEN.equals(poModel.getRecordStatus()) || AccountAccreditationStatus.CONFIRMED.equals(psValidStatus)){
                if (poGRider.getUserLevel() <= UserRight.ENCODER) {
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
    /**
     * Prepares and validates the transaction data before committing to the database.This method performs final integrity checks, generates transaction numbers for new records,
 prunes empty detail rows, and synchronizes metadata across master, details, and 
 attachments.It also handles attachment filename collisions by renaming duplicates 
 and triggers the file upload process for unsent attachments.
     * 
     * 
     * @return A {@link JSONObject} indicating success or detailing validation/upload errors.
     * @throws SQLException, GuanzonException, CloneNotSupportedException 
     *          If an error occurs during data processing, file operations, or validation.
     * @throws org.guanzon.appdriver.base.GuanzonException
     */
    @Override
    protected JSONObject willSave() throws SQLException, GuanzonException {
        poJSON = new JSONObject();
        
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
            loJSON = new JSONObject();
            loJSON.put("result", "error");
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
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record selected.");
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
                    poGRider.rollbackTrans();
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
                        poGRider.rollbackTrans();
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
                    object.setAPClientID(getModel().getClientId()); //Supplie Client ID
                    object.setClientID(loClient.InstiContact(lnCtr).getcCPrsonID());  //Contact Person Client ID
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

    public JSONObject BlockTransaction() throws SQLException, GuanzonException, CloneNotSupportedException {
        poJSON = new JSONObject();
        
        //initliaze ongoing record status, for validator
        psValidStatus = AccountAccreditationStatus.BLOCKED;
        
        //initialize validator
        poJSON = isEntryOkay();
        if ("error".equals((String) poJSON.get("result"))) {
            return poJSON;
        }
        System.out.println(pbWthParent);
        poGRider.beginTrans("UPDATE STATUS", "BlockTransaction", SOURCE_CODE, getModel().getTransactionNo());
 
        //if Accreditation 
        if (getModel().getAccountType().equals("0")) {
            
            //initialize AP_Client_Object
            AP_Client_Master loObject = getAPClientMaster(getModel().getClientId());
            
            //make sure its onready mode
            if (loObject.getEditMode() == EditMode.UPDATE) {
                //Deactivate AP Client Master
                loObject.setWithParentClass(true);
                loObject.setWithUI(false);
                poJSON = loObject.BlockRecord();
                if ("error".equals((String) poJSON.get("result"))) {
                    poGRider.rollbackTrans();
                    return poJSON;
                }
            }
        }
        
        if(psApprovalUser == null || "".equals(psApprovalUser)){
            psApprovalUser = poGRider.getUserID();
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
        poJSON.put("message", "Transaction blocked successfully.");

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

//        String lsSQL = "UPDATE "
//                + poModel.getTable()
//                + " SET cTranStat = " + SQLUtil.toSQL("4")
//                + " WHERE sTransNox = " + SQLUtil.toSQL(getModel().getTransactionNo());
//
//        Long lnResult = poGRider.executeQuery(lsSQL,
//                getModel().getTable(),
//                poGRider.getBranchCode(), "", "");
//        if (lnResult <= 0L) {
//            poGRider.rollbackTrans();

//            poJSON = new JSONObject();
//            poJSON.put("result", "error");
//            poJSON.put("message", "Error updating the transaction status.");
//            return poJSON;
//        }

        //change status - Replace script above by status change - Arsiela - 05-28-2026
        poJSON = statusChange(getModel().getTable(), (String) getModel().getValue("sTransNox"),"", psValidStatus, false,true);
        if (!"success".equals((String) poJSON.get("result"))) {
            poGRider.rollbackTrans();
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
                    case AccountAccreditationStatus.BLOCKED:
                        crs.updateString("cRefrStat", "BLOCKED");
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
                            case AccountAccreditationStatus.BLOCKED:
                                crs.updateString("cRefrStat", "BLOCKED");
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
