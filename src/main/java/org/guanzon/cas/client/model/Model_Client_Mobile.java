package org.guanzon.cas.client.model;

import java.sql.SQLException;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.constant.MobileType;
import org.guanzon.appdriver.constant.OwnershipType;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;

public class Model_Client_Mobile extends Model {

    Model_Client_Master poClient;

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);
            //assign default values
            poEntity.updateObject("nUnreachx", 0);
            poEntity.updateObject("dLastVeri", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("dInactive", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("nNoRetryx", 0);
            poEntity.updateObject("cInvalidx", Logical.NO);
            poEntity.updateObject("cConfirmd", Logical.NO);
            poEntity.updateObject("dConfirmd", SQLUtil.toDate("1900-01-01 00:00:00", SQLUtil.FORMAT_TIMESTAMP));
            poEntity.updateObject("dHoldMktg", SQLUtil.toDate("1900-01-01 00:00:00", SQLUtil.FORMAT_TIMESTAMP));
            poEntity.updateObject("dMktgMsg1", SQLUtil.toDate("1900-01-01 00:00:00", SQLUtil.FORMAT_TIMESTAMP));
            poEntity.updateObject("dMktgMsg2", SQLUtil.toDate("1900-01-01 00:00:00", SQLUtil.FORMAT_TIMESTAMP));
            poEntity.updateObject("dMktgMsg3", SQLUtil.toDate("1900-01-01 00:00:00", SQLUtil.FORMAT_TIMESTAMP));
            poEntity.updateString("cMobileTp", MobileType.MOBILE);
            poEntity.updateString("cOwnerxxx", OwnershipType.PERSONAL);
            poEntity.updateString("cPrimaryx", Logical.NO);
            poEntity.updateString("cIncdMktg", Logical.NO);
            poEntity.updateString("cNewMobil", Logical.YES);
            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);
            //end - assign default values

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

    public JSONObject setMobileId(String mobileId) {
        return setValue("sMobileID", mobileId);
    }

    public String getMobileId() {
        return (String) getValue("sMobileID");
    }

    public JSONObject setClientId(String clientId) {
        return setValue("sClientID", clientId);
    }

    public String getClientId() {
        return (String) getValue("sClientID");
    }

    public JSONObject setMobileNo(String mobileNo) {
        return setValue("sMobileNo", mobileNo);
    }

    public String getMobileNo() {
        return (String) getValue("sMobileNo");
    }

    public JSONObject setMobileType(String mobileType) {
        return setValue("cMobileTp", mobileType);
    }

    public String getMobileType() {
        return (String) getValue("cMobileTp");
    }

    public JSONObject setOwnershipType(String ownershipType) {
        return setValue("cOwnerxxx", ownershipType);
    }

    public String getOwnershipType() {
        return (String) getValue("cOwnerxxx");
    }

    public JSONObject isPrimaryMobile(boolean isPrimaryMobile) {
        return setValue("cPrimaryx", isPrimaryMobile ? "1" : "0");
    }

    public boolean isPrimaryMobile() {
        return ((String) getValue("cPrimaryx")).equals("1");
    }

    public JSONObject isAcceptingMarketing(boolean isAcceptingMarketing) {
        return setValue("cIncdMktg", isAcceptingMarketing ? "1" : "0");
    }

    public boolean isAcecptingMarketing() {
        return ((String) getValue("cIncdMktg")).equals("1");
    }

    public JSONObject setMobileNetwork(String networkType) {
        return setValue("cSubscrbr", networkType);
    }

    public String getMobileNetwork() {
        return (String) getValue("cSubscrbr");
    }

    public JSONObject isNewContact(boolean isNewContact) {
        return setValue("cNewMobil", isNewContact ? "1" : "0");
    }

    public boolean isNewContact() {
        return ((String) getValue("cNewMobil")).equals("1");
    }

    public JSONObject setSourceCode(String sourceCode) {
        return setValue("sSourceCd", sourceCode);
    }

    public String getSourceCode() {
        return (String) getValue("sSourceCd");
    }

    public JSONObject setSourceNo(String sourceNo) {
        return setValue("sReferNox", sourceNo);
    }

    public String getSourceNo() {
        return (String) getValue("sReferNox");
    }

    public JSONObject setRecordStatus(String recordStatus) {
        return setValue("cRecdStat", recordStatus);
    }

    public String getRecordStatus() {
        return (String) getValue("cRecdStat");
    }

    public JSONObject setModifiedDate(Date modifiedDate) {
        return setValue("dModified", modifiedDate);
    }

    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }

    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(getTable(), ID, true, poGRider.getGConnection().getConnection(), poGRider.getBranchCode());
    }

    public Model_Client_Master Client() throws SQLException, GuanzonException {
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
