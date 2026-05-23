/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.guanzon.cas.client.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.client.Client_Role;
import org.guanzon.cas.client.services.ClientControllers;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */

public class ClientRoleController implements Initializable {
    
    private final String MODULE = "Client Controller";
    
    private GRiderCAS poGRider;
    private LogWrapper poWrapper;
    
    private Client_Role poRole;
    
    private String psRoleIDxx;
    private JSONObject poJSON = new JSONObject();
    
    private int pnEditMode;
    private boolean pbCancelled, pbLoaded;

    public void setGRider(GRiderCAS griderCAS){
        poGRider = griderCAS;
    }
    
    public void setLogWrapper(LogWrapper wrapper){
        poWrapper = wrapper;
    }
    
    public void setRoleIDxx(String fsRoleIDxx){
        psRoleIDxx = fsRoleIDxx;
    }
    
    public boolean isCancelled(){
        return pbCancelled;
    }
    
    public Client_Role getRole(){
        return poRole;
    }
    
    @FXML
    AnchorPane apMain;
    
    @FXML
    Label lblRoleStatus;
    
    @FXML
    TextField tfRoleIDxx;
    
    @FXML
    TextField tfDescript;
    
    @FXML
    CheckBox chckActive;
    
    @FXML
    private Button btnExit;
    
    @FXML
    Button btnSave;
    
    @FXML
    Button btnCancel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            if (poGRider == null) {
                ShowMessageFX.Warning(getStage(), "Application driver is not set.", "Warning", MODULE);
                getStage().close();
            }

            poRole = new ClientControllers(poGRider, poWrapper).ClientRole();
            
            initFields();
            loadRecord();
            
            pbLoaded = true;
        } catch (SQLException | GuanzonException e) {
            ShowMessageFX.Error(getStage(), e.getMessage(), "Error", MODULE);
            getStage().close();
        }
        
    }
    
    final ChangeListener<? super Boolean> txtRole_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsID = txtField.getId();
        String lsValue = txtField.getText() == null ? "" : txtField.getText();

        if (lsValue == null) {
            return;
        }

        if (!nv) {//lost focus

            switch (lsID) {
                case "tfDescript":
                    poJSON = poRole.getModel().setsRoleDesc(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }
                    txtField.setText(poRole.getModel().getsRoleDesc());
                    break;
            }
        } else {//got focus
            txtField.selectAll();
        }
    };
    
    private void cmdButton_Click(ActionEvent event){
        try {
            switch (((Button) event.getSource()).getId()) {
                case "btnExit":
                case "btnCancel":
                    psRoleIDxx = "";
                    pbCancelled = true;
                    
                    getStage().close();
                    break;
                case "btnSave":
                    
                    if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                        
                        //initialize modify value
                        poRole.getModel().setsModified(poGRider.getUserID());
                        poRole.getModel().setdModified(poGRider.getServerDate());
                        
                        poJSON = poRole.saveRecord();
                        if (!"success".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                            break;
                        }else{
                            ShowMessageFX.Information(getStage(), (String) poJSON.get("message"), "Success", MODULE);
                            psRoleIDxx = poRole.getModel().getRoleIDxx();
                            pbCancelled = false;
                            
                            getStage().close();
                        }
                    }
                    break;

            }
        }   catch (CloneNotSupportedException | SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
            ShowMessageFX.Error(getStage(),MiscUtil.getException(ex), "Warning", MODULE);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(e), e);
            ShowMessageFX.Error(getStage(),MiscUtil.getException(e), "Warning", MODULE);
        }
    }
    
    private void initFields(){
        
        tfDescript.focusedProperty().addListener(txtRole_Focus);
        
        btnExit.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        
        chckActive.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                
                if (!pbLoaded) {
                    return;
                }
                
                poJSON = poRole.getModel().setcRecdStat(newValue ? "1" : "0");
                if (poJSON == null) {
                    chckActive.setSelected(oldValue);
                    return;
                }
                
                if (poJSON.get("result").toString().equalsIgnoreCase("error")) {
                    chckActive.setSelected(oldValue);
                    poRole.getModel().setcRecdStat(oldValue ? "1" : "0");
                    
                    ShowMessageFX.Error(getStage(), poJSON.get("message").toString(), "Record Status", MODULE);
                }
            }
            
        });
        
    }
    
    private void loadRecord() throws SQLException, GuanzonException{
        
        if (psRoleIDxx == null || psRoleIDxx.isEmpty()) {
            
            poJSON = poRole.newRecord();
            if (poJSON.get("result").toString().equalsIgnoreCase("success")) {
                poRole.getModel().setcRecdStat("1");
                lblRoleStatus.setText("***New Role***");
            }else{
                disableFields(true);
                return;
            }
        }else{
            poJSON = poRole.openRecord(psRoleIDxx);
            if (poJSON.get("result").toString().equalsIgnoreCase("success")) {
                
                lblRoleStatus.setText("***Repeat Role***");
                
                poRole.updateRecord();
                if (poJSON.get("result").toString().equalsIgnoreCase("error")) {
                    btnSave.setDisable(true);
                }
            }else{
                disableFields(true);
                return;
            }
        }
        
        //initialize edit mode
        pnEditMode = poRole.getEditMode();
        
        //display details
        tfRoleIDxx.setText(poRole.getModel().getRoleIDxx() == null ? "" : poRole.getModel().getRoleIDxx());
        tfDescript.setText(poRole.getModel().getsRoleDesc() == null ? "" : poRole.getModel().getsRoleDesc());
        chckActive.setSelected((poRole.getModel().getcRecdStat() == null ? "0" : poRole.getModel().getcRecdStat()).equalsIgnoreCase("1"));
    }
    
    private void disableFields(boolean fbDisable){
        btnSave.setDisable(fbDisable);
        chckActive.setDisable(fbDisable);
        tfDescript.setDisable(fbDisable);
    }
    
    public Stage getStage() {
        return (Stage) apMain.getScene().getWindow();
    }    
    
}
