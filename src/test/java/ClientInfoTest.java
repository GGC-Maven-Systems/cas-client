
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;

import org.h2.tools.RunScript;
import org.json.simple.JSONObject;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.cas.client.ClientInfo;
import org.guanzon.cas.client.services.ClientControllers;
import org.json.simple.parser.ParseException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ClientInfoTest {
    static GRiderCAS instance;
    static ClientInfo poController;
    static Connection conn;
    private static String psUserId = "GCO1260011";//M001250015;
    private static String psCompanyId = "M001";
    private String psTransNo = "GK0126000001";

    @BeforeClass
    public static void setUpClass() throws GuanzonException, SQLException, IOException {
        instance = new GRiderCAS();

        if (!instance.loadEnv("gRider")) {
            System.err.println(instance.getMessage());
            System.exit(1);
        }

        if (!instance.logUser("gRider", "M001250015")) {
            System.err.println(instance.getMessage());
            System.exit(1);
        }

        loadCorePrimary();

        String path;
        String tempPath;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            path = "D:/GGC_Maven_Systems";
            tempPath = "D:/temp";
        } else {
            path = "/srv/GGC_Maven_Systems";
            tempPath = "/srv/temp";
        }

        System.setProperty("sys.default.path.config", path);
        System.setProperty("sys.default.path.metadata", path + "/config/metadata/new/");
        System.setProperty("sys.default.path.temp", tempPath);

        if (!loadProperties()) {
            System.err.println("Unable to load config.");
            System.exit(1);
        }

        resetController();
    }

    @AfterClass
    public static void tearDownClass() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        System.clearProperty("sys.default.path.config");
        System.clearProperty("sys.default.path.metadata");
        System.clearProperty("sys.default.path.temp");

        System.clearProperty("sys.main.industry");
        System.clearProperty("sys.general.industry");
        System.clearProperty("sys.dept.finance");
        System.clearProperty("sys.dept.procurement");
        System.clearProperty("user.selected.industry");
        System.clearProperty("user.selected.category");
        System.clearProperty("user.selected.company");
        System.clearProperty("sys.default.client.token");
        System.clearProperty("sys.default.access.token");
        System.clearProperty("sys.default.path.temp.attachments");
        System.clearProperty("allowed.department");
    }

    private static boolean loadProperties() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(System.getProperty("sys.default.path.config") + "/config/cas.properties"));

            System.setProperty("sys.main.industry", props.getProperty("sys.main.industry"));
            System.setProperty("sys.general.industry", props.getProperty("sys.general.industry"));
            System.setProperty("sys.dept.finance", props.getProperty("sys.dept.finance"));
            System.setProperty("sys.dept.procurement", props.getProperty("sys.dept.procurement"));
            System.setProperty("user.selected.industry", props.getProperty("user.selected.industry"));
            System.setProperty("user.selected.category", props.getProperty("user.selected.category"));
            System.setProperty("user.selected.company", props.getProperty("user.selected.company"));
            System.setProperty("sys.default.client.token", System.getProperty("sys.default.path.config") + "/client.token");
            System.setProperty("sys.default.access.token", System.getProperty("sys.default.path.config") + "/access.token");
            System.setProperty("sys.default.path.temp.attachments", props.getProperty("sys.default.path.temp.attachments"));
            System.setProperty("allowed.department", props.getProperty("allowed.department"));
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    
    private static void loadCorePrimary() throws IOException, SQLException {
        conn = instance.getGConnection().getConnection();

        List<String> schemaScripts = new ArrayList<>();
        List<String> dataScripts = new ArrayList<>();

        schemaScripts.add("client_master_schema");
        schemaScripts.add("parameter_status_history_schema");
        
        schemaScripts.add("client_address_schema");
        schemaScripts.add("client_email_address_schema");
        schemaScripts.add("client_mobile_schema");
        schemaScripts.add("client_social_media_schema");
        schemaScripts.add("client_employment_schema");
        schemaScripts.add("client_institution_contact_person_schema");
        
        schemaScripts.add("barangay_schema");
        schemaScripts.add("towncity_schema");
        schemaScripts.add("province_schema");
        
        dataScripts.add("client_master_data");
        dataScripts.add("parameter_status_history_data");
        
        dataScripts.add("client_address_data");
        dataScripts.add("client_email_address_data");
        dataScripts.add("client_mobile_data");
        dataScripts.add("client_social_media_data");
        dataScripts.add("client_employment_data");
        dataScripts.add("client_institution_contact_person_data");
        dataScripts.add("barangay_data");
        dataScripts.add("towncity_data");
        dataScripts.add("province_data");

        for (String schema : schemaScripts) {
            try (FileReader schemaReader = new FileReader("test-data/" + schema + ".sql")) {
                RunScript.execute(conn, schemaReader);
            }
        }

        for (String data : dataScripts) {
            try (FileReader dataReader = new FileReader("test-data/" + data + ".sql")) {
                RunScript.execute(conn, dataReader);
            }
        }

    }
    private static void resetController() throws SQLException, GuanzonException {
        poController = new ClientControllers(instance, null).ClientInfo();
        poController.setWithUI(false);
        Assert.assertNotNull(poController);
    }

    private static void startNewTransaction() throws CloneNotSupportedException, SQLException, GuanzonException {
        JSONObject loJSON = new JSONObject();
        if (poController == null) {
            resetController();
        }
        poController.initialize();

        loJSON = poController.newRecord();
        Assert.assertEquals("success", loJSON.get("result"));
    }
    
    /*Convert Date to String*/
    private static String xsDateShort(Date fdValue) {
        if(fdValue == null){
            return "1900-01-01";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(fdValue);
        return date;
    }

    private LocalDate strToDate(String val) {
        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(val, date_formatter);
        return localDate;
    }
    
    @Test
    public void test0001(){
        try {
            JSONObject loJSON = new JSONObject();
            
            resetController();
            startNewTransaction();
            poController.setWithUI(false);
            
            loJSON = poController.getModel().setFirstName("Kyla");
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.getModel().setMiddleName("Lacandle");
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.getModel().setLastName("Almajano");
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.getModel().setBirthDate(SQLUtil.toDate(xsDateShort(instance.getServerDate()), SQLUtil.FORMAT_SHORT_DATE));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.getModel().setBirthPlaceId("test");
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.getModel().setCivilStatus("1");
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.getModel().setGender("1");
            Assert.assertEquals("success", loJSON.get("result"));
            
//            loJSON = poController.addAddress();
//            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.Address(poController.getAddressCount()-1).setAddress("Test");
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.Address(poController.getAddressCount()-1).setBarangayId("test");
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.Address(poController.getAddressCount()-1).setTownId("test");
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.Address(poController.getAddressCount()-1).isPrimaryAddress(true);
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            
//            loJSON = poController.addMobile();
//            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.Mobile(poController.getMobileCount()-1).setMobileNetwork("0");
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.Mobile(poController.getMobileCount()-1).setMobileNo("09302353252");
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.Mobile(poController.getMobileCount()-1).isPrimaryMobile(true);
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            
//            loJSON = poController.addMail();
//            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.Mail(poController.getMailCount()-1).setMailAddress("Tessst@gmail.com");
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.Mail(poController.getMailCount()-1).isPrimaryEmail(true);
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            
//            loJSON = poController.addSocMed();
//            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.SocMed(poController.getSocMedCount()-1).setAccount("Test@fb.com");
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            
            loJSON = poController.ClientEmployment().setAddressYears(5.0);
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.ClientEmployment().setIncomeSource("test");
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.ClientEmployment().setGrossIncome(50000.00);
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.ClientEmployment().setDependents(5);
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.ClientEmployment().setWorkYears(5.0);
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.ClientEmployment().setPosition("ceo");
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.ClientEmployment().setEmployerName("test");
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.ClientEmployment().setBusinessAddress("test");
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            loJSON = poController.ClientEmployment().setOfficeEmail("test");
            System.out.println("MESSAGE : " + (String) loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            
            loJSON = poController.saveRecord();
            System.out.println("MESSAGE : " + loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            
            String lsTransNo = poController.getModel().getClientId();
            loJSON = poController.openClientRecord(lsTransNo);
            System.out.println("MESSAGE : " + String.valueOf(loJSON.get("message")));
            Assert.assertEquals("success", loJSON.get("result"));
            
            loJSON = poController.updateRecord();
            System.out.println("MESSAGE : " + String.valueOf(loJSON.get("message")));
            Assert.assertEquals("success", loJSON.get("result"));
            
            loJSON = poController.getModel().setLastName("tes");
            Assert.assertEquals("success", loJSON.get("result"));
            
            loJSON = poController.saveRecord();
            System.out.println("MESSAGE : " + loJSON.get("message"));
            Assert.assertEquals("success", loJSON.get("result"));
            
            test0001History();
            
        } catch (CloneNotSupportedException | SQLException | GuanzonException  ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
            Assert.assertEquals(MiscUtil.getException(ex), MiscUtil.getException(ex));
        } 
    }
    
    
    public void test0001History() {
//        JSONObject loJSON = new JSONObject();
//        try {
//            poController.setWithUI(false);
//            poController.ShowStatusHistory();
//            
//            loJSON = poController.getEntryBy();
//            System.out.println("MESSAGE : " + loJSON.get("message"));
//            Assert.assertEquals("success", loJSON.get("result"));
//            
//        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
//            Assert.assertEquals(MiscUtil.getException(ex), MiscUtil.getException(ex));
//        } catch (Exception ex) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//            Assert.assertEquals(MiscUtil.getException(ex), MiscUtil.getException(ex));
//        } 
    }
    
//    @Test
//    public void test0003Search() {
//        JSONObject loJSON = new JSONObject();
//        try {
//            resetController();
//            startNewTransaction();
//            poController.setWithUI(false);
//            poController.setRecordStatus("0123");
//            loJSON = poController.searchRecord(psTransNo, false);
//            System.out.println("MESSAGE : " + loJSON.get("message"));
//            
//        } catch (SQLException | GuanzonException | CloneNotSupportedException  ex) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
//            Assert.assertEquals(MiscUtil.getException(ex), MiscUtil.getException(ex));
//        } 
//    }

    
}
