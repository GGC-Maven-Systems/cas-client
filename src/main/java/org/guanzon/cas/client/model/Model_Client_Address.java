package org.guanzon.cas.client.model;

import java.sql.SQLException;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.cas.parameter.model.Model_Barangay;
import org.guanzon.cas.parameter.model.Model_TownCity;
import org.json.simple.JSONObject;

public class Model_Client_Address extends Model{
    Model_Barangay poBarangay;
    Model_TownCity poTownCity;
    Model_Client_Master poClient;
    
    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());
            
            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            
            //assign default values
            poEntity.updateObject("nLatitude", 0.00);
            poEntity.updateObject("nLongitud", 0.00);
            poEntity.updateString("cPrimaryx", Logical.NO);
            poEntity.updateString("cOfficexx", Logical.NO);
            poEntity.updateString("cProvince", Logical.NO);
            poEntity.updateString("cBillingx", Logical.NO);
            poEntity.updateString("cShipping", Logical.NO);
            poEntity.updateString("cCurrentx", Logical.NO);
            poEntity.updateString("cLTMSAddx", Logical.NO);
            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = ("sAddrssID");
            
            //initialize other connections
            poTownCity = new Model_TownCity();
            poTownCity.setApplicationDriver(poGRider);
            poTownCity.setXML("Model_TownCity");
            poTownCity.setTableName("TownCity");
            poTownCity.initialize();
            
            poClient = new Model_Client_Master();
            poClient.setApplicationDriver(poGRider);
            poClient.setXML("Model_Client_Master");
            poClient.setTableName("Client_Master");
            poClient.initialize();
            
            poBarangay = new Model_Barangay();
            poBarangay.setApplicationDriver(poGRider);
            poBarangay.setXML("Model_Barangay");
            poBarangay.setTableName("Barangay");
            poBarangay.initialize();
            //end - initialize other connections
            
            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
    
    public JSONObject setAddressId(String addressId){
        return setValue("sAddrssID", addressId);
    }

    public String getAddressId(){
        return (String) getValue("sAddrssID");
    }
    
    public JSONObject setClientId(String clientId){
        return setValue("sClientID", clientId);
    }

    public String getClientId(){
        return (String) getValue("sClientID");
    }
    
    public JSONObject setHouseNo(String houseNo){
        return setValue("sHouseNox", houseNo);
    }

    public String getHouseNo(){
        return (String) getValue("sHouseNox");
    }
    
    public JSONObject setAddress(String address){
        return setValue("sAddressx", address);
    }

    public String getAddress(){
        return (String) getValue("sAddressx");
    }
    
    public JSONObject setBarangayId(String barangayId){
        return setValue("sBrgyIDxx", barangayId);
    }

    public String getBarangayId(){
        return (String) getValue("sBrgyIDxx");
    }
    
    public JSONObject setTownId(String townId){
        return setValue("sTownIDxx", townId);
    }    
    
    public String getTownId(){
        return (String) getValue("sTownIDxx");
    }
    
    public JSONObject setLatitude(String latitude){
        return setValue("nLatitude", Double.valueOf(latitude));
    }
    
    public Number getLatitude(){
        Object value = getValue("nLatitude");
            return ((Number) value).doubleValue();
    }
    
    public JSONObject setLongitude(String longitude){
        return setValue("nLongitud", Double.valueOf(longitude));
    }
    
    public Number getLongitude(){
        Object value = getValue("nLongitud");
        return ((Number) value).doubleValue();
    }
    
    public JSONObject isPrimaryAddress(boolean isPrimaryAddress){
        return setValue("cPrimaryx", isPrimaryAddress ? "1" : "0");
    }

    public boolean isPrimaryAddress(){
        return ((String) getValue("cPrimaryx")).equals("1");
    }
    
    public JSONObject isOfficeAddress(boolean isOfficeAddress){
        return setValue("cOfficexx", isOfficeAddress ? "1" : "0");
    }

    public boolean isOfficeAddress(){
        return ((String) getValue("cOfficexx")).equals("1");
    }
    
    public JSONObject isProvinceAddress(boolean isProvinceAddress){
        return setValue("cProvince", isProvinceAddress ? "1" : "0");
    }

    public boolean isProvinceAddress(){
        return ((String) getValue("cProvince")).equals("1");
    }
    
    public JSONObject isBillingAddress(boolean isBillingAddress){
        return setValue("cBillingx", isBillingAddress ? "1" : "0");
    }

    public boolean isBillingAddress(){
        return ((String) getValue("cBillingx")).equals("1");
    }
    
    public JSONObject isShippingAddress(boolean isShippingAddress){
        return setValue("cShipping", isShippingAddress ? "1" : "0");
    }

    public boolean isShippingAddress(){
        return ((String) getValue("cShipping")).equals("1");
    }
    
    public JSONObject isCurrentAddress(boolean isCurrentAddress){
        return setValue("cCurrentx", isCurrentAddress ? "1" : "0");
    }

    public boolean isCurrentAddress(){
        return ((String) getValue("cCurrentx")).equals("1");
    }
    
    public JSONObject isLTMSAddress(boolean isLTMSAddress){
        return setValue("cLTMSAddx", isLTMSAddress ? "1" : "0");
    }

    public boolean isLTMSAddress(){
        return ((String) getValue("cLTMSAddx")).equals("1");
    }
    
    public JSONObject setSourceCode(String sourceCode){
        return setValue("sSourceCd", sourceCode);
    }

    public String getSourceCode(){
        return (String) getValue("sSourceCd");
    }
    
    public JSONObject setSourceNo(String sourceNo){
        return setValue("sReferNox", sourceNo);
    }

    public String getSourceNo(){
        return (String) getValue("sReferNox");
    }
    
    public JSONObject setRecordStatus(String recordStatus){
        return setValue("cRecdStat", recordStatus);
    }
    
    public String getRecordStatus(){
        return (String) getValue("cRecdStat");
    }
       
    public JSONObject setModifiedDate(Date modifiedDate){
        return setValue("dModified", modifiedDate);
    }
    
    public Date getModifiedDate(){
        return (Date) getValue("dModified");
    }
    
    @Override
    public String getNextCode(){
        return MiscUtil.getNextCode(getTable(), ID, true, poGRider.getGConnection().getConnection(), poGRider.getBranchCode()); 
    }
    
    public Model_Barangay Barangay() throws SQLException, GuanzonException{
        if (!"".equals((String) getValue("sBrgyIDxx"))) {
            if (poBarangay.getEditMode() == EditMode.READY
                    && poBarangay.getBarangayId().equals((String) getValue("sBrgyIDxx"))) {
                return poBarangay;
            } else {
                poJSON = poBarangay.openRecord((String) getValue("sBrgyIDxx"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poBarangay;
                } else {
                    poBarangay.initialize();
                    return poBarangay;
                }
            }
        } else {
            poBarangay.initialize();
            return poBarangay;
        }
        
        
//        if (!"".equals(getValue("sBrgyIDxx"))) {
//            
//            this.poJSON = this.poBarangay.openRecord((String) getValue("sBrgyIDxx"));
//            if ("success".equals(this.poJSON.get("result"))) {
//                return this.poBarangay;
//            }
//        }
//        return this.poBarangay;
    }
    
    public Model_TownCity Town() throws SQLException, GuanzonException{
        if (!"".equals((String) getValue("sTownIDxx"))) {
            
            if (poTownCity.getEditMode() == EditMode.READY
                    && poTownCity.getTownId().equals((String) getValue("sTownIDxx"))) {
                return poTownCity;
            } else {
                poJSON = poTownCity.openRecord((String) getValue("sTownIDxx"));
                if ("success".equals((String) poJSON.get("result"))) {
                    return poTownCity;
                } else {
                    poTownCity.initialize();
                    return poTownCity;
                }
            }
        } else {
            return poTownCity;
        }
        
//        if (!"".equals(getValue("sTownIDxx"))) {
//            
//            this.poJSON = this.poTownCity.openRecord((String) getValue("sTownIDxx"));
//            if ("success".equals(this.poJSON.get("result"))) {
//                return this.poTownCity;
//            }
//        }
//        return this.poTownCity;
    }
    
    public Model_Client_Master Client() throws SQLException, GuanzonException{
        if (!"".equals(getValue("sClientID"))) {
            
            this.poJSON = this.poClient.openRecord((String) getValue("sClientID"));
            if ("success".equals(this.poJSON.get("result"))) {
                return this.poClient;
            }
        }
        return this.poClient;
    }
}