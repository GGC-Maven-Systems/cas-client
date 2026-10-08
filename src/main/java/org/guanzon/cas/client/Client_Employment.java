package org.guanzon.cas.client;

import java.sql.SQLException;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.agent.services.ReferenceCache;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.cas.client.model.Model_Client_Employment;
import org.json.simple.JSONObject;

public class Client_Employment  extends Parameter{
    Model_Client_Employment poModel;
    
    @Override
    public void initialize() {
        psRecdStat = Logical.YES;
        
        poModel = new Model_Client_Employment();
        poModel.setApplicationDriver(poGRider);
        poModel.setXML("Model_Client_Employment");
        poModel.setTableName("Client_Employment");
        poModel.initialize();
    }
    
    @Override
    public JSONObject isEntryOkay() {
        poJSON = new JSONObject();

        if (poModel.getClientId().isEmpty()){
            poJSON.put("result", "error");
            poJSON.put("message", "Client must not be empty.");
            return poJSON;
        }
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    @Override
    public Model_Client_Employment getModel() {
        return poModel;
    }

    @Override
    protected void saveComplete() {
        //Every lazy ClientInstitutionContact() accessor across the model layer serves repeat
        //lookups for this id from ReferenceCache - drop the stale snapshot now that the record
        //has changed.
        ReferenceCache.invalidate("Client_Employment", poModel.getClientId());
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
                    "  a.sContctID" +
                    ", a.sClientID" +
                    ", a.sCPerson1" +
                    ", a.sCPPosit1" +
                    ", a.sMobileNo" +
                    ", a.sTelNoxxx" +
                    ", a.sFaxNoxxx" +
                    ", a.sEMailAdd" +
                    ", a.sAccount1" +
                    ", a.sAccount2" +
                    ", a.sAccount3" +
                    ", a.sRemarksx" +
                    ", a.cPrimaryx" +
                    ", a.cRecdStat" +
                    ", TRIM(IF(b.cClientTp = '0', CONCAT(b.sLastName, ', ', b.sFrstName, IF(TRIM(IFNull(b.sSuffixNm, '')) = '', ' ', CONCAT(' ', b.sSuffixNm, ' ')), b.sMiddName), b.sCompnyNm)) xFullName" +
                    ", IF(a.cPrimaryx = '1', 'Yes', 'No') xPrimaryx" +
                " FROM Client_Employment a" +
                    ", Client_Master b" +
                " WHERE a.sClientID = b.sClientID";
        System.out.println("get SQ BRowse == " + lsSQL);
        return MiscUtil.addCondition(lsSQL, lsCondition);
        
    }
}