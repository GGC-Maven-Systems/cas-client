package org.guanzon.cas.client;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.ClientType;
import org.guanzon.cas.client.controller.ClientRoleController;
import org.guanzon.cas.client.controller.IndividualNewController;
import org.guanzon.cas.client.controller.InstitutionNewController;
import org.json.simple.JSONObject;

public class ClientGUI extends Application {
    private double xOffset = 0; 
    private double yOffset = 0;
    
    private static GRiderCAS poGRider;
    private static LogWrapper poWrapper;
    
    private static ClientInfo poClient;
    private static Client_Role poRole;
    
    private static String psClientId;
    private static String psRoleId;
    private static boolean pbByCode;
    private static String psClientTp;
    private static boolean pbCancelled;
    
    private static String psCategory;
    
    public void setGRider(GRiderCAS griderCAS){
        poGRider = griderCAS;
    }
    
    public void setLogWrapper(LogWrapper wrapper){
        poWrapper = wrapper;
    }
    
    public void setClientId(String clientId){
        psClientId = clientId;
    }
    
    public void setRoleID(String roleId){
        psRoleId = roleId;
    }
    
    public void setByCode(boolean byCode){
        pbByCode = byCode;
    }
    public void setClientType(String clientType){
        psClientTp = clientType;
    }
    
    public void setStagePosition(double X, double Y){
        xOffset = X;
        yOffset = Y;
    }
    
    public String getClientId() {
        return psClientId;
    }
    
    public ClientInfo getClient(){
        return poClient;
    }
    
    public Client_Role getRole(){
        return poRole;
    }
    
    public boolean isCancelled(){
        return pbCancelled;
    }
    
    public void setCategoryCode(String categoryCd){
        psCategory = categoryCd;
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        //search client record by id
        if(psClientId != null && !psClientId.isEmpty()){
            
            if (pbByCode) {
                JSONObject loJSON = searchRecord(psClientId != null ? psClientId : "", true);

                if ("success".equals((String) loJSON.get("result"))) {
                    psClientId = (String) loJSON.get("clientId") != null ? (String) loJSON.get("clientId") : "";
                }
            } else {
                JSONObject loJSON = searchRecord(psClientId != null ? psClientId : "", false);
                if ("success".equals((String) loJSON.get("result"))) {
                    psClientId = (String) loJSON.get("clientId") != null ? (String) loJSON.get("clientId") : "";
                }

            }
        }
        
        if (psClientTp.equals(ClientType.INDIVIDUAL)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/guanzon/cas/client/views/IndividualNew.fxml"));

            IndividualNewController controller = new IndividualNewController();
            controller.setGRider(poGRider);
            controller.setLogWrapper(poWrapper);
            controller.setClientId(psClientId);

            loader.setController(controller);

            Parent parent = loader.load();

            parent.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    primaryStage.setX(event.getScreenX() - xOffset);
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
            });

            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle("Client Info");
            primaryStage.centerOnScreen();
            
            if (xOffset > 0) primaryStage.setX((xOffset));
            if (yOffset > 0) primaryStage.setY((yOffset));
            
            primaryStage.showAndWait();

            pbCancelled = controller.isCancelled();
            if (!pbCancelled) {
                poClient = controller.getClient();
                psClientId = controller.getClientId();
            }
        } else if(psClientTp.equals(ClientType.INSTITUTION)) {
            
            //ensure category param is initialized before entry
            if (psCategory == null || psCategory.isEmpty()) {
                ShowMessageFX.Error("Please load category first!", "Client Entry", null);
                pbCancelled = true;
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/guanzon/cas/client/views/InstitutionNew.fxml"));

            InstitutionNewController controller = new InstitutionNewController();
            controller.setGRider(poGRider);
            controller.setLogWrapper(poWrapper);
            controller.setClientId(psClientId);
            controller.setCategoryCode(psCategory);

            loader.setController(controller);

            Parent parent = loader.load();

            parent.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    primaryStage.setX(event.getScreenX() - xOffset);
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
            });

            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle("Institution Info");
            primaryStage.centerOnScreen();
            
            if (xOffset > 0) primaryStage.setX((xOffset ));
            if (yOffset > 0) primaryStage.setY((yOffset));
            
            primaryStage.showAndWait();

            pbCancelled = controller.isCancelled();
            if (!pbCancelled) {
                poClient = controller.getClient();
                psClientId = controller.getClientId();
            }
        }else{
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/guanzon/cas/client/views/ClientRole.fxml"));
            
            ClientRoleController controller = new ClientRoleController();
            controller.setGRider(poGRider);
            controller.setLogWrapper(poWrapper);
            controller.setRoleIDxx(psRoleId);

            loader.setController(controller);

            Parent parent = loader.load();

            parent.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    primaryStage.setX(event.getScreenX() - xOffset);
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
            });

            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle("Client Role");
            primaryStage.centerOnScreen();
            
            if (xOffset > 0) primaryStage.setX((xOffset ));
            if (yOffset > 0) primaryStage.setY((yOffset));
            
            primaryStage.showAndWait();

            pbCancelled = controller.isCancelled();
            if (!pbCancelled) {
                poRole = controller.getRole();
            }
        }

    }

    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException{
        String lsSQL = getSQ_Browse();
        
        JSONObject loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "Birthday»Name»Mobile»Email",
                "dBirthDte»xFullName»xMobileNo»xEMailAdd",
                "a.dBirthDte»TRIM(CONCAT(a.sLastName, ', ', a.sFrstName, IF(a.sSuffixNm <> '', CONCAT(' ', a.sSuffixNm, ''), ''), ' ', a.sMiddName))»IFNULL(c.sMobileNo, '')»IFNULL(d.sEMailAdd, '')",
                byCode ? 0 : 1);

        if (loJSON != null) {

            lsSQL = (String) loJSON.get("sClientID");
            if (lsSQL == null) {
                loJSON = new JSONObject();
                loJSON.put("result", "error");
                loJSON.put("message", "No record loaded.");
                return loJSON;
            }
            loJSON = new JSONObject();
            loJSON.put("result", "success");
            loJSON.put("clientId", lsSQL);
            return loJSON;
        } else {
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
        }
    }
    
    private String getSQ_Browse(){      
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
                                " AND d.cPrimaryx = '1'" +
                        " WHERE a.cRecdStat = '1'";

        String lsCondition = "";
        
        if (psClientTp.length() > 1) {
            for (int lnCtr = 0; lnCtr <= psClientTp.length() - 1; lnCtr++) {
                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psClientTp.charAt(lnCtr)));
            }

            lsCondition = "a.cClientTp IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "a.cClientTp = " + SQLUtil.toSQL(psClientTp);
        }      
        
        return MiscUtil.addCondition(lsSQL, lsCondition);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
