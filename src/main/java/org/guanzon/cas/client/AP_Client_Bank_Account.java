/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.client;

import java.sql.SQLException;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.cas.client.model.Model_AP_Client_Bank_Account;
import org.json.simple.JSONObject;

/**
 *
 * @author kalyptus
 */
public class AP_Client_Bank_Account  extends Parameter{
    Model_AP_Client_Bank_Account poModel;
    
    @Override
    public void initialize() {
        psRecdStat = Logical.YES;
        
        poModel = new Model_AP_Client_Bank_Account();
        poModel.setApplicationDriver(poGRider);
        poModel.setXML("Model_AP_Client_Bank_Account");
        poModel.setTableName("AP_Client_Bank_Account");
        poModel.initialize();
    }
    
    @Override
    public JSONObject isEntryOkay() {
        poJSON = new JSONObject();

        if (poModel.getClientID().isEmpty()){
            poJSON.put("result", "error");
            poJSON.put("message", "Client must not be empty.");
            return poJSON;
        }

        if (poModel.getAccountNumber().isEmpty()){
            poJSON.put("result", "error");
            poJSON.put("message", "Account Number must not be empty.");
            return poJSON;
        }

        if (poModel.getAccountName().isEmpty()){
            poJSON.put("result", "error");
            poJSON.put("message", "Account Name must not be empty.");
            return poJSON;
        }
        
        if (poModel.getBankID().isEmpty()){
            poJSON.put("result", "error");
            poJSON.put("message", "Bank must not be empty.");
            return poJSON;
        }
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    @Override
    public Model_AP_Client_Bank_Account getModel() {
        return poModel;
    }
    
    @Override
    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException{
        poJSON = ShowDialogFX.Search(poGRider,
                getSQ_Browse(),
                value,
                "Company Name»Bank Name»Account Number»Account Name",
                "xFullName»sBankName»sActNumbr»sActNamex",
                "TRIM(IF(b.cClientTp = '0', CONCAT(b.sLastName, ', ', b.sFrstName, IF(TRIM(IFNull(b.sSuffixNm, '')) = '', ' ', CONCAT(' ', b.sSuffixNm, ' ')), b.sMiddName), b.sCompnyNm))»c.sBankName»a.sActNumbr»a.sActNamex",
                byCode ? 5 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sAPBnkIDx"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    public JSONObject searchRecordbyclient(String value, boolean byCode) throws SQLException, GuanzonException{
        poJSON = ShowDialogFX.Search(poGRider,
                getSQ_Browse(),
                value,
                "Company Name»Bank Name»Account Number»Account Name",
                "xFullName»sBankName»sActNumbr»sActNamex",
                "TRIM(IF(b.cClientTp = '0', CONCAT(b.sLastName, ', ', b.sFrstName, IF(TRIM(IFNull(b.sSuffixNm, '')) = '', ' ', CONCAT(' ', b.sSuffixNm, ' ')), b.sMiddName), b.sCompnyNm))»c.sBankName»a.sActNumbr»a.sActNamex",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sAPBnkIDx"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    public JSONObject searchRecord(String value, boolean byCode, String clientId) throws SQLException, GuanzonException{
        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), "a.sClientID = " + SQLUtil.toSQL(clientId));
        
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "Company Name»Bank Name»Account Number»Account Name",
                "xFullName»sBankName»sActNumbr»sActNamex",
                "TRIM(IF(b.cClientTp = '0', CONCAT(b.sLastName, ', ', b.sFrstName, IF(TRIM(IFNull(b.sSuffixNm, '')) = '', ' ', CONCAT(' ', b.sSuffixNm, ' ')), b.sMiddName), b.sCompnyNm))»c.sBankName»a.sActNumbr»a.sActNamex",
                byCode ? 5 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sAPBnkIDx"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    //Arsiela 06-02-2026
    //Search bank account filtered by bank id
    public JSONObject searchRecordbyBanks(String value, String bankID, boolean byCode) throws SQLException, GuanzonException{
        String lsSQL = "";
        String lsCondition = "";

        if (bankID != null && !bankID.isEmpty()) {
            lsCondition = "a.sBankIDxx = " + SQLUtil.toSQL(bankID);
        }

         lsSQL = MiscUtil.addCondition(getSQ_Browse(), lsCondition);
        System.out.println("SQL : " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "Company Name»Bank Name»Account Number»Account Name",
                "xFullName»sBankName»sActNumbr»sActNamex",
                "TRIM(IF(b.cClientTp = '0', CONCAT(b.sLastName, ', ', b.sFrstName, IF(TRIM(IFNull(b.sSuffixNm, '')) = '', ' ', CONCAT(' ', b.sSuffixNm, ' ')), b.sMiddName), b.sCompnyNm))»c.sBankName»a.sActNumbr»a.sActNamex",
                byCode ? 2 : 3);
            
        if (poJSON != null) {
            System.out.println("JSON " + poJSON.toJSONString());
            return poModel.openRecord((String) poJSON.get("sAPBnkIDx"));//sBnkActID
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    @Override
    public String getSQ_Browse(){
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
                    "  a.sAPBnkIDx" +
                    ", a.sClientID" +
                    ", a.sActNumbr" +
                    ", a.sActNamex" +
                    ", a.sBankIDxx" +
                    ", a.cRecdStat" +
                    ", TRIM(IF(b.cClientTp = '0', CONCAT(b.sLastName, ', ', b.sFrstName, IF(TRIM(IFNull(b.sSuffixNm, '')) = '', ' ', CONCAT(' ', b.sSuffixNm, ' ')), b.sMiddName), b.sCompnyNm)) xFullName" +
//                    ", IF(a.cPrimaryx = '1', 'Yes', 'No') xPrimaryx" +
                    ", c.sBankName" +
                " FROM AP_Client_Bank_Account a" +
//                    ", Client_Master b" +
                "   LEFT JOIN Client_Master b ON b.sClientID = a.sClientID " +
                " LEFT JOIN Banks c ON a.sBankIDxx = c.sBankIDxx " +
                " WHERE a.sClientID = b.sClientID";
        System.out.println("get SQ BRowse == " + lsSQL);
        return MiscUtil.addCondition(lsSQL, lsCondition);
        
    }    
}
