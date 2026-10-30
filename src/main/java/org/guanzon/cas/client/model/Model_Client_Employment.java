package org.guanzon.cas.client.model;

import java.sql.SQLException;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;

public class Model_Client_Employment extends Model{
    Model_Client_Master poClient;
    
    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());
            
            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            
            //assign default values
            poEntity.updateObject("nAddrYrsx", 0.0);
            poEntity.updateObject("nDependnt", 0);
            poEntity.updateObject("nWorkYrsx", 0.0);
            poEntity.updateObject("nGrossInc", 0.00);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = poEntity.getMetaData().getColumnLabel(1);

            //poClient is intentionally NOT constructed here - see Client() below, which builds
            //it lazily on first access. NOTE: Client() has no FK-driven fetch logic today (it
            //never called openRecord() even before this change) - only construction was moved.

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
        
    public JSONObject setClientId(String clientId){
        return setValue("sClientID", clientId);
    }

    public String getClientId(){
        return (String) getValue("sClientID");
    }
    
    public JSONObject setAddressYears(Double addressYears) {
        return setValue("nAddrYrsx", addressYears);
    }

    public Double getAddressYears() {
        if (getValue("nAddrYrsx") == null || "".equals(getValue("nAddrYrsx"))) {
            return 0.0000;
        }
        return Double.valueOf(getValue("nAddrYrsx").toString());
    }

    public JSONObject setIncomeSource(String incomeSource){
        return setValue("sIncomSrc", incomeSource);
    }

    public String getIncomeSource(){
        return (String) getValue("sIncomSrc");
    }

    public JSONObject setDependents(int dependents) {
        return setValue("nDependnt", dependents);
    }

    public int getDependents() {
        if (getValue("nDependnt") == null || "".equals(getValue("nDependnt"))) {
            return 0;
        }
        return (int) getValue("nDependnt");
    }

    public JSONObject setEmployerName(String employerName){
        return setValue("sEmployNm", employerName);
    }

    public String getEmployerName(){
        return (String) getValue("sEmployNm");
    }

    public JSONObject setBusinessAddress(String businessAddress){
        return setValue("sBusAddrs", businessAddress);
    }

    public String getBusinessAddress(){
        return (String) getValue("sBusAddrs");
    }

    public JSONObject setOfficeEmail(String officeEmail){
        return setValue("sOffEmail", officeEmail);
    }

    public String getOfficeEmail(){
        return (String) getValue("sOffEmail");
    }

    public JSONObject setPosition(String position){
        return setValue("sPosition", position);
    }

    public String getPosition(){
        return (String) getValue("sPosition");
    }
    
    public JSONObject setWorkYears(Double workYears) {
        return setValue("nWorkYrsx", workYears);
    }

    public Double getWorkYears() {
        if (getValue("nWorkYrsx") == null || "".equals(getValue("nWorkYrsx"))) {
            return 0.0000;
        }
        return Double.valueOf(getValue("nWorkYrsx").toString());
    }
    
    public JSONObject setGrossIncome(Double grossIncome) {
        return setValue("nGrossInc", grossIncome);
    }

    public Double getGrossIncome() {
        if (getValue("nGrossInc") == null || "".equals(getValue("nGrossInc"))) {
            return 0.0000;
        }
        return Double.valueOf(getValue("nGrossInc").toString());
    }
    
    @Override
    public String getNextCode(){
        return "";
    }
    
    public Model_Client_Master Client() throws SQLException, GuanzonException{
        if (poClient == null) {
            poClient = new Model_Client_Master();
            poClient.setApplicationDriver(poGRider);
            poClient.setXML("Model_Client_Master");
            poClient.setTableName("Client_Master");
            poClient.initialize();
        }
        return poClient;
    }
}