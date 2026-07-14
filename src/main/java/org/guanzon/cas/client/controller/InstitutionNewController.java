package org.guanzon.cas.client.controller;

import com.ibm.icu.impl.Assert;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.ClientType;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.client.ClientGUI;
import org.guanzon.cas.client.ClientInfo;
import org.guanzon.cas.client.services.ClientControllers;
import org.guanzon.cas.client.table.models.ModelAddress;
import org.json.simple.JSONObject;
import org.guanzon.cas.client.table.models.ModelContactPerson;

public class InstitutionNewController implements Initializable {

    @FXML
    private AnchorPane AnchorMain;

    @FXML
    private TextField txtField01;

    @FXML
    private TextField txtField02;

    @FXML
    private TextArea txtField03;

    @FXML
    private TabPane TabPane;

    @FXML
    private Tab Company;

    @FXML
    private AnchorPane anchorAddress;
    @FXML
    private GridPane gridAddress;

    @FXML
    private TableView<ModelAddress> tblAddress;

    @FXML
    private TableColumn<ModelAddress, String> indexAddress01;

    @FXML
    private TableColumn<ModelAddress, String> indexAddress02;

    @FXML
    private TableColumn<ModelAddress, String> indexAddress03;

    @FXML
    private TableColumn<ModelAddress, String> indexAddress04;

    @FXML
    private TableColumn<ModelAddress, String> indexAddress05;

    @FXML
    private TextField txtAddress03;

    @FXML
    private TextField txtAddress04;

    @FXML
    private TextField txtAddress05;

    @FXML
    private TextField txtAddress01;

    @FXML
    private TextField txtAddress02;

    @FXML
    private TextField txtAddress06;

    @FXML
    private CheckBox cbAddress01;

    @FXML
    private CheckBox cbAddress02;

    @FXML
    private CheckBox cbAddress08;

    @FXML
    private Button btnAddAddress;

    @FXML
    private Label lblClientStatus;
    @FXML
    private TextField txtAddress00;

    @FXML
    private Tab Contact;

    @FXML
    private AnchorPane anchorSocMed;
    @FXML
    private GridPane gridSocMed;

    @FXML
    private CheckBox cbContact01;

    @FXML
    private CheckBox cbContact02;

    @FXML
    private CheckBox cbContact00; //payee

    @FXML
    private Button btnAddContact, btnAddClient, btnAddRole;

    @FXML
    private TextField txtContact00; //fullname

    @FXML
    private TextField txtContact01; //position

    @FXML
    private TextField txtContact02; //mobile no

    @FXML
    private TextField txtContact03; //landline

    @FXML
    private TextField txtContact04; //fax no

    @FXML
    private TextField txtContact05; //email

    @FXML
    private TextField txtContact06; //job title

    @FXML
    private TextField txtContact07; //department

    @FXML
    private TextField txtContact08; //role

    @FXML
    private TableView<ModelContactPerson> tblSocMed;

    @FXML
    private TableColumn<ModelContactPerson, String> indexSocMed01; //index

    @FXML
    private TableColumn<ModelContactPerson, String> indexSocMed02; //name

    @FXML
    private TableColumn<ModelContactPerson, String> indexSocMed03; //role

    @FXML
    private TableColumn<ModelContactPerson, String> indexSocMed04; //status

    @FXML
    private HBox hbButtons;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private AnchorPane draggablePane;

    @FXML
    private Button btnExit;

    @FXML
    private FontAwesomeIconView glyphExit;

    private final String MODULE = "Institution Controller";
    private GRiderCAS poGRider;
    private LogWrapper poWrapper;

    private ClientInfo poClient;
    private String psClientID;

    private int pnEditMode;
    private int pnCompany;
    private int pnContactPerson;

    private ObservableList<ModelAddress> company_data = FXCollections.observableArrayList();
    private ObservableList<ModelContactPerson> contactPerson_data = FXCollections.observableArrayList();

    private JSONObject poJSON;
    private boolean pbLoaded;
    private boolean pbCancelled;
    private boolean pbLoadingData;

    private String psCategory;

    public void setGRider(GRiderCAS griderCAS) {
        poGRider = griderCAS;
    }

    public void setLogWrapper(LogWrapper wrapper) {
        poWrapper = wrapper;
    }

    public void setClientId(String clientId) {
        psClientID = clientId;
    }

    public String getClientId() {
        return psClientID;
    }

    public ClientInfo getClient() {
        return poClient;
    }

    public boolean isCancelled() {
        return pbCancelled;
    }

    public void setCategoryCode(String categoryCd) {
        psCategory = categoryCd;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            if (poGRider == null) {
                ShowMessageFX.Warning(getStage(), "Application driver is not set.", "Warning", MODULE);
                getStage().close();
            }

            initFields();

            poClient = new ClientControllers(poGRider, poWrapper).ClientInfo();
            poClient.setClientType(ClientType.INSTITUTION);
            poClient.setCategory(psCategory);
            poClient.setRecordStatus("1");

            loadRecord();

            pbLoaded = true;
        } catch (SQLException | GuanzonException e) {
            ShowMessageFX.Error(getStage(), e.getMessage(), "Error", MODULE);
            getStage().close();
        }
    }

    private void cmdButton_Click(ActionEvent event) {
        try {
            switch (((Button) event.getSource()).getId()) {
                case "btnExit":
                case "btnCancel":
                    psClientID = "";
                    pbCancelled = true;
                    getStage().close();
                    break;
                case "btnSave":
                    if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                        poJSON = poClient.saveRecord();
                        System.out.println(poJSON.toJSONString());
                        if (!"success".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                        } else {
                            ShowMessageFX.Information(getStage(), (String) poJSON.get("message"), "Success", MODULE);
                            psClientID = poClient.getModel().getClientId();
                            pbCancelled = false;
                            getStage().close();
                        }
                    }
                    break;
                case "btnAddAddress":

                    JSONObject addObjAddress;
                    addObjAddress = poClient.addAddress();

                    if ("error".equals((String) addObjAddress.get("result"))) {
                        ShowMessageFX.Information(getStage(), (String) addObjAddress.get("message"), "Computerized Acounting System", MODULE);
                        break;
                    } else {
                        poClient.Address(pnCompany).setClientId(poClient.getModel().getClientId());
                        pnCompany = poClient.getAddressCount() - 1;
                        tblAddress.getSelectionModel().select(pnCompany + 1);

                        loadRecordAddress();

                        txtAddress03.requestFocus();
                    }
                    break;
                case "btnDelMobile":
                    break;
                case "btnAddClient":
                    addContactPerson();
                    break;
                case "btnAddRole":
                    addContactRole();
                    break;
                case "btnAddContact":

                    JSONObject addObj = poClient.addInstiContact();

                    if ("error".equals((String) addObj.get("result"))) {
                        ShowMessageFX.Information(getStage(), (String) addObj.get("message"), "Computerized Acounting System", MODULE);
                        break;
                    } else {

                        poClient.InstiContact(pnContactPerson).setCategoryCode(psCategory);
                        poClient.InstiContact(pnContactPerson).setClientId(poClient.getModel().getClientId());
                        pnContactPerson = poClient.getInstiContactCount() - 1;

                        tblSocMed.getSelectionModel().select(pnContactPerson + 1);

                        loadContactPerson();

                        txtContact00.requestFocus();
                    }
                    break;

            }

        } catch (CloneNotSupportedException | SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
            ShowMessageFX.Error(getStage(), MiscUtil.getException(ex), "Warning", MODULE);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(e), e);
            ShowMessageFX.Error(getStage(), MiscUtil.getException(e), "Warning", MODULE);
        }

    }

    private void company_Clicked(MouseEvent event) {
        pnCompany = tblAddress.getSelectionModel().getSelectedIndex();
        if (pnCompany >= 0) {
            getSelectedAddress();
        }
    }

    private void contactPerson_Clicked(MouseEvent event) {
        pnContactPerson = tblSocMed.getSelectionModel().getSelectedIndex();
        if (pnContactPerson >= 0) {
            getSelectedContactPerson();
        }
    }

    private void txtAddress_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        int lnIndex = Integer.parseInt(txtField.getId().substring(10, 12));

        String lsValue = txtField.getText();

        try {
            if (event.getCode() == F3 || event.getCode() == ENTER) {
                switch (lnIndex) {

                    case 3: //province
                        poJSON = poClient.searchProvince(pnCompany, lsValue, false);

                        if ("success".equals((String) poJSON.get("result"))) {
                            txtField.setText(poClient.Address(pnCompany).Town().Province().getDescription());
                            loadRecordAddress();
                            CommonUtils.SetNextFocus(txtField);
                            event.consume();
                        }
                        break;
                    case 4: //town
                        poJSON = poClient.searchTown(pnCompany, lsValue, false);

                        if ("success".equals((String) poJSON.get("result"))) {
                            txtField.setText(poClient.Address(pnCompany).Town().getDescription());
                            txtAddress03.setText(poClient.Address(pnCompany).Town().Province().getDescription());
                            loadRecordAddress();
                            CommonUtils.SetNextFocus(txtField);
                            event.consume();
                        }
                        break;
                    case 5: //barangay
                        poJSON = poClient.searchBarangay(pnCompany, lsValue, false);

                        if ("success".equals((String) poJSON.get("result"))) {
                            txtField.setText(poClient.Address(pnCompany).Barangay().getBarangayName());
                            txtAddress04.setText(poClient.Address(pnCompany).Town().getDescription());
                            txtAddress03.setText(poClient.Address(pnCompany).Town().Province().getDescription());
                            loadRecordAddress();
                            CommonUtils.SetNextFocus(txtField);
                            event.consume();
                        }
                        break;
                }
            }
        } catch (SQLException | GuanzonException e) {
            ShowMessageFX.Error(getStage(), e.getMessage(), "Error", MODULE);
            getStage().close();
        }

        switch (event.getCode()) {
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField);
                break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        }
    }

    private void txtContactPerson_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();

        String lsValue = txtField.getText() == null ? "" : txtField.getText();
        int lnIndex = Integer.parseInt(txtField.getId().substring(10, 12));

        switch (event.getCode()) {
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField);
                break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
                break;
            case F3:
                
                try {

                switch (lnIndex) {
                    case 0: //client name

                        if (poClient.getInstiContactCount() <= 0) {
                            ShowMessageFX.Error(getStage(), "Please add row before proceeding!", MODULE, null);
                            return;
                        }
                        searchContactPerson(lsValue);
                        break;
                    case 8: //contact role

                        if (poClient.getInstiContactCount() <= 0) {
                            ShowMessageFX.Error(getStage(), "Please add row before proceeding!", MODULE, null);
                            return;
                        }
                        searchContactRole(lsValue);
                        break;
                }
            } catch (Exception e) {
                poWrapper.severe(e.getMessage());
            }
            break;
        }
    }

    final ChangeListener<? super Boolean> txtAddress_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(10, 12));

        String lsValue = txtField.getText();

        if (lsValue == null) {
            lsValue = "";
        }

        if (!nv) {//lost focus
            switch (lnIndex) {
                case 0: //company name
                    poJSON = poClient.getModel().setCompanyName(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                        return;
                    }

                    txtField.setText(poClient.getModel().getCompanyName());
                    txtField02.setText(poClient.getModel().getCompanyName());
                    break;

                case 6: //tax id number
                    String lsTinPattern = "^\\d{3}-\\d{3}-\\d{3}-\\d{3}$";
                    if (!lsValue.matches(lsTinPattern)) {
                        ShowMessageFX.Warning(getStage(), "TIN Number is invalid", "Warning", MODULE);
                        return;
                    }
                    poJSON = poClient.getModel().setTaxIdNumber(lsValue);
                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                        return;
                    }
                    txtField.setText(poClient.getModel().getTaxIdNumber());
                    break;
                case 1: //house no
                    poJSON = poClient.Address(pnCompany).setHouseNo(lsValue);
                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                        return;
                    }

                    txtField.setText(poClient.Address(pnCompany).getHouseNo());
                    break;
                case 2: //address
                    poJSON = poClient.Address(pnCompany).setAddress(lsValue);
                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                        return;
                    }

                    txtField.setText(poClient.Address(pnCompany).getAddress());
                    break;
                case 3: //province
                    if (lsValue.isEmpty() && poClient.Address(pnCompany).getEditMode() == EditMode.ADDNEW) {
                        try {
                            poClient.Address(pnCompany).setTownId("");
                            poClient.Address(pnCompany).setBarangayId("");
                            //Reset town model
                            poClient.Address(pnCompany).Town().initialize();
                        } catch (SQLException | GuanzonException ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                        }
                    }
                    break;
                case 4: //town
                    if (lsValue.isEmpty() && poClient.Address(pnCompany).getEditMode() == EditMode.ADDNEW) {
                        try {
                            poClient.Address(pnCompany).setBarangayId("");
                            poClient.Address(pnCompany).setTownId("");
                            poClient.Address(pnCompany).Town().setTownId("");
                            poClient.Address(pnCompany).Town().setDescription("");
                        } catch (SQLException | GuanzonException ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                        }
                    }
                    break;
                case 5: //brgy
                    if (lsValue.isEmpty() && poClient.Address(pnCompany).getEditMode() == EditMode.ADDNEW) {
                        poClient.Address(pnCompany).setBarangayId("");
                    }
                    break;
            }

            loadRecordAddress();
        } else {//got focus
            txtField.selectAll();
        }
    };

    final ChangeListener<? super Boolean> txtContactPerson_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(10, 12));

        String lsValue = txtField.getText();

        if (lsValue == null) {
            return;
        }

        if (!nv) {//lost focus

            switch (lnIndex) {
                case 1: //Position
                    poJSON = poClient.InstiContact(pnContactPerson).setContactPersonPosition(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(poClient.InstiContact(pnContactPerson).getContactPersonPosition());
                    break;
                case 6: //Job Title
                    poJSON = poClient.InstiContact(pnContactPerson).setContactJobTitle(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(poClient.InstiContact(pnContactPerson).getContactJobTitle());
                    break;
                case 7: //Department
                    poJSON = poClient.InstiContact(pnContactPerson).setsDeprtmnt(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(poClient.InstiContact(pnContactPerson).getsDeprtmnt());
                    break;
                case 8: //Role
                    if (lsValue == null || "".equals(lsValue)) {
                        poJSON = poClient.InstiContact(pnContactPerson).setsRoleIDxx(lsValue);
                        if (!"success".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                        }
                        txtContact08.setText("");
                    }

                    break;

            }

            loadContactPerson();
        } else {//got focus
            txtField.selectAll();
        }
    };

    public void initCompanyGrid() {
        company_data.clear();

        indexAddress01.setStyle("-fx-alignment: CENTER;");
        indexAddress02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexAddress03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexAddress04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexAddress05.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");

        indexAddress01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        indexAddress02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        indexAddress03.setCellValueFactory(new PropertyValueFactory<>("index03"));
        indexAddress04.setCellValueFactory(new PropertyValueFactory<>("index04"));
        indexAddress05.setCellValueFactory(new PropertyValueFactory<>("index05"));

        tblAddress.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblAddress.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });

        tblAddress.setItems(company_data);
        tblAddress.getSelectionModel().select(pnCompany + 1);
        tblAddress.autosize();
    }

    private void initContactPersonGrid() {
        contactPerson_data.clear();

        indexSocMed01.setStyle("-fx-alignment: CENTER;");
        indexSocMed02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexSocMed03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexSocMed04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");

        indexSocMed01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        indexSocMed02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        indexSocMed03.setCellValueFactory(new PropertyValueFactory<>("index03"));
        indexSocMed04.setCellValueFactory(new PropertyValueFactory<>("index04"));

        tblSocMed.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblSocMed.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        tblSocMed.setItems(contactPerson_data);
        tblSocMed.autosize();
    }

    private void initFields() {
        applyMask("XXX-XXX-XXX-XXX", txtAddress06);
        txtAddress00.focusedProperty().addListener(txtAddress_Focus);
        txtAddress01.focusedProperty().addListener(txtAddress_Focus);
        txtAddress02.focusedProperty().addListener(txtAddress_Focus);
        txtAddress03.focusedProperty().addListener(txtAddress_Focus);
        txtAddress04.focusedProperty().addListener(txtAddress_Focus);
        txtAddress05.focusedProperty().addListener(txtAddress_Focus);
        txtAddress06.focusedProperty().addListener(txtAddress_Focus);

        txtAddress01.setOnKeyPressed(this::txtAddress_KeyPressed);
        txtAddress02.setOnKeyPressed(this::txtAddress_KeyPressed);
        txtAddress03.setOnKeyPressed(this::txtAddress_KeyPressed);
        txtAddress04.setOnKeyPressed(this::txtAddress_KeyPressed);
        txtAddress05.setOnKeyPressed(this::txtAddress_KeyPressed);

        txtContact02.focusedProperty().addListener(txtContactPerson_Focus);
        txtContact03.focusedProperty().addListener(txtContactPerson_Focus);
        txtContact04.focusedProperty().addListener(txtContactPerson_Focus);
        txtContact05.focusedProperty().addListener(txtContactPerson_Focus);
        txtContact06.focusedProperty().addListener(txtContactPerson_Focus);
        txtContact01.focusedProperty().addListener(txtContactPerson_Focus);
        txtContact07.focusedProperty().addListener(txtContactPerson_Focus);
        txtContact08.focusedProperty().addListener(txtContactPerson_Focus);

        txtContact00.setOnKeyPressed(this::txtContactPerson_KeyPressed);
        txtContact02.setOnKeyPressed(this::txtContactPerson_KeyPressed);
        txtContact03.setOnKeyPressed(this::txtContactPerson_KeyPressed);
        txtContact04.setOnKeyPressed(this::txtContactPerson_KeyPressed);
        txtContact05.setOnKeyPressed(this::txtContactPerson_KeyPressed);
        txtContact06.setOnKeyPressed(this::txtContactPerson_KeyPressed);
        txtContact01.setOnKeyPressed(this::txtContactPerson_KeyPressed);
        txtContact07.setOnKeyPressed(this::txtContactPerson_KeyPressed);
        txtContact08.setOnKeyPressed(this::txtContactPerson_KeyPressed);

        btnExit.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnAddAddress.setOnAction(this::cmdButton_Click);
        btnAddContact.setOnAction(this::cmdButton_Click);
        btnAddClient.setOnAction(this::cmdButton_Click);
        btnAddRole.setOnAction(this::cmdButton_Click);

        tblAddress.setOnMouseClicked(this::company_Clicked);
        tblSocMed.setOnMouseClicked(this::contactPerson_Clicked);

        initCompanyCheckbox();
        initContactPersonCheckbox();

        clearfields();
    }

    private void clearfields() {
        txtField01.setText("");
        txtField02.setText("");
        txtField03.setText("");

        txtAddress00.setText("");
        txtAddress01.setText("");
        txtAddress02.setText("");
        txtAddress03.setText("");
        txtAddress04.setText("");
        txtAddress05.setText("");
        txtAddress06.setText("");

        cbAddress01.selectedProperty().set(false);
        cbAddress02.selectedProperty().set(false);
        cbAddress08.selectedProperty().set(false);

        txtContact00.setText("");
        txtContact01.setText("");
        txtContact02.setText("");
        txtContact03.setText("");
        txtContact04.setText("");
        txtContact05.setText("");
        txtContact06.setText("");
        txtContact07.setText("");
        txtContact08.setText("");
        cbContact01.selectedProperty().set(false);
        cbContact02.selectedProperty().set(false);
        cbContact00.selectedProperty().set(false);

        pnCompany = 0;
        pnContactPerson = 0;

        initCompanyGrid();
        initContactPersonGrid();
    }

    private void loadRecord() {
        try {

            if (psClientID.isEmpty()) {
                poJSON = poClient.newRecord();
                if (!"success".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    return;
                }

                anchorAddress.setDisable(false);
                gridAddress.setDisable(false);
                anchorSocMed.setDisable(false);
                gridSocMed.setDisable(false);

//                poClient.getModel().setCompanyName(psClientID);
//                txtField02.setText(psClientID);
//                txtAddress00.setText(psClientID);
                lblClientStatus.setText("***NEW INSTITUTION");
            } else {
                poJSON = poClient.openClientRecord(psClientID);
                if (!"success".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    return;
                }

                //for update record, add new row to avoid null pointer exception on missing records from old entries -Guillier 2026/03/24
                poJSON = poClient.updateRecord();
                if (!"success".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    return;
                }
                pnEditMode = poClient.getEditMode();
                boolean lbShow = pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE;//allow to modify contact on update
                anchorSocMed.setDisable(!lbShow);
                gridSocMed.setDisable(!lbShow);

                //allow to modify address on update
                anchorAddress.setDisable(!lbShow);
                gridAddress.setDisable(!lbShow);
                lblClientStatus.setText("***REPEAT INSTITUTION");
            }

            txtField01.setText(poClient.getModel().getClientId());
            txtField02.setText(poClient.getModel().getCompanyName());
            txtAddress00.setText(poClient.getModel().getCompanyName());
            txtAddress06.setText(poClient.getModel().getTaxIdNumber());
            txtField03.setText(""); //todo: put full address here      

            loadRecordAddress();
            loadContactPerson();

            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                txtAddress00.requestFocus();
                txtAddress00.selectAll();
            } else {
                btnSave.requestFocus();
            }
        } catch (SQLException | GuanzonException | CloneNotSupportedException e) {
            e.printStackTrace();
            ShowMessageFX.Error(getStage(), e.getMessage(), "Error", MODULE);
            getStage().close();
        }
    }

    private void loadRecordAddress() {
        try {

            int lnCtr;
            company_data.clear();

            if (poClient.getAddressCount() >= 0) {

                for (lnCtr = 0; lnCtr < poClient.getAddressCount(); lnCtr++) {

                    company_data.add(new ModelAddress(String.valueOf(lnCtr + 1),
                            (String) poClient.Address(lnCtr).getHouseNo(),
                            (String) poClient.Address(lnCtr).getAddress(),
                            (String) poClient.Address(lnCtr).Town().getDescription(),
                            (String) poClient.Address(lnCtr).Barangay().getBarangayName()));
                    
                    //comment aldrich 5/27/2026 , conflicting to if 1 remaining item is inactive, then its primary must be false
                    //set primary address the first item, only if size is 1
//                    if (poClient.getAddressCount() == 1 && lnCtr == 0) {
//                        poClient.Address(0).isPrimaryAddress(true);
//                    }
                }
            }
            loadMasterAddress();

            if (pnCompany < 0 || pnCompany >= company_data.size()) {

                if (!company_data.isEmpty()) {

                    /* FOCUS ON FIRST ROW */
                    tblAddress.getSelectionModel().select(0);
                    tblAddress.getFocusModel().focus(0);
                    pnCompany = tblAddress.getSelectionModel().getSelectedIndex();

                    getSelectedAddress();
                }
            } else {

                /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                tblAddress.getSelectionModel().select(pnCompany);
                tblAddress.getFocusModel().focus(pnCompany);

                getSelectedAddress();
            }
        } catch (SQLException | GuanzonException e) {
            e.printStackTrace();
        }
    }

    public void loadMasterAddress() throws SQLException, GuanzonException {
        String address;
        boolean primaryAddressExists = false;

        for (int i = 0; i < poClient.getAddressCount(); i++) {

            if (poClient.Address(i).isPrimaryAddress()) {

//                address = poClient.Address(i).getHouseNo() == null || poClient.Address(i).getHouseNo().isEmpty() ? "" : poClient.Address(i).getHouseNo() + " "
//                        + poClient.Address(i).getAddress() == null || poClient.Address(i).getAddress().isEmpty() ? "" : poClient.Address(i).getAddress() + " "
//                        + poClient.Address(i).Barangay().getBarangayName() == null || poClient.Address(i).Barangay().getBarangayName().isEmpty() ? "" : poClient.Address(i).Barangay().getBarangayName() + " "
//                        + poClient.Address(i).Town().getDescription() == null || poClient.Address(i).Town().getDescription().isEmpty() ? "" : poClient.Address(i).Town().getDescription() + ", "
//                        + poClient.Address(i).Town().Province().getDescription() == null || poClient.Address(i).Town().Province().getDescription().isEmpty() ? "" : poClient.Address(i).Town().Province().getDescription();
//
//                txtField03.setText(address.trim());
                txtField03.setText(poClient.getFullAddress(i));

                primaryAddressExists = true; // Mark as found
                break; // Exit the loop since a primary address is found
            }
        }

        if (!primaryAddressExists) {
            txtField03.setText("");
        }
    }

    private void getSelectedAddress() {
        pbLoadingData = true;
        try {
            if (poClient.getAddressCount() > 0) {
                txtAddress01.setText(poClient.Address(pnCompany).getHouseNo());
                txtAddress02.setText(poClient.Address(pnCompany).getAddress());
                txtAddress03.setText(poClient.Address(pnCompany).Town().Province().getDescription());
                txtAddress04.setText(poClient.Address(pnCompany).Town().getDescription());
                txtAddress05.setText(poClient.Address(pnCompany).Barangay().getBarangayName());

                cbAddress01.setSelected(("1".equals((String) poClient.Address(pnCompany).getRecordStatus())));
                cbAddress02.setSelected(poClient.Address(pnCompany).isPrimaryAddress());
                cbAddress08.setSelected(poClient.Address(pnCompany).isLTMSAddress());
            }
        } catch (SQLException | GuanzonException e) {
            e.printStackTrace();
        }
        pbLoadingData = false;
    }

    private void loadContactPerson() {

        try {

            contactPerson_data.clear();

            if (poClient.getInstiContactCount() > 0) {
                for (int lnCtr = 0; lnCtr < poClient.getInstiContactCount(); lnCtr++) {

                    contactPerson_data.add(new ModelContactPerson(
                            String.valueOf(lnCtr + 1),
                            poClient.InstiContact(lnCtr).getContactPersonName(),
                            poClient.InstiContact(lnCtr).ContactRole().getsRoleDesc(),
                            poClient.InstiContact(lnCtr).getRecordStatus().equalsIgnoreCase("0") ? "Inactive" : "Active"
                    ));
                    //comment aldrich 5/27/2026 , conflicting to if 1 remaining item is inactive, then its primary must be false
                    //set primary contact the first item, only if size is 1
//                    if (poClient.getInstiContactCount() == 1 && lnCtr == 0) {
//                        poClient.InstiContact(0).isPrimaryContactPersion(true);
//                    }
                }
            }
            if (pnContactPerson < 0 || pnContactPerson >= contactPerson_data.size()) {

                if (!contactPerson_data.isEmpty()) {
                    /* FOCUS ON FIRST ROW */
                    tblSocMed.getSelectionModel().select(pnContactPerson);
                    tblSocMed.getFocusModel().focus(pnContactPerson);
                    pnContactPerson = tblSocMed.getSelectionModel().getSelectedIndex();
                }
                getSelectedContactPerson();
            } else {
                /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                tblSocMed.getSelectionModel().select(pnContactPerson);
                tblSocMed.getFocusModel().focus(pnContactPerson);

                getSelectedContactPerson();
            }

        } catch (SQLException e) {
            poWrapper.severe(e.getMessage());
        } catch (GuanzonException e) {
            poWrapper.severe(e.getMessage());
        }
    }

    private void getSelectedContactPerson() {

        try {

            pbLoadingData = true;

            if (poClient.getInstiContactCount() > 0) {
                if (poClient.InstiContact(pnContactPerson).getEditMode() == EditMode.READY || poClient.InstiContact(pnContactPerson).getEditMode() == EditMode.UPDATE) {
                    txtContact00.setDisable(true);
                } else {
                    txtContact00.setDisable(false);
                }
                txtContact00.setText(poClient.InstiContact(pnContactPerson).getContactPersonName().trim());
                txtContact01.setText(poClient.InstiContact(pnContactPerson).getContactPersonPosition());
                txtContact02.setText(poClient.InstiContact(pnContactPerson).getMobileNo());
                txtContact03.setText(poClient.InstiContact(pnContactPerson).getLandlineNo());
                txtContact04.setText(poClient.InstiContact(pnContactPerson).getFaxNo());
                txtContact05.setText(poClient.InstiContact(pnContactPerson).getMailAddress());

                txtContact06.setText(poClient.InstiContact(pnContactPerson).getContactJobTitle());
                txtContact07.setText(poClient.InstiContact(pnContactPerson).getsDeprtmnt());

                cbContact01.setSelected(("1".equals(poClient.InstiContact(pnContactPerson).getRecordStatus())));
                cbContact02.setSelected(poClient.InstiContact(pnContactPerson).isPrimaryContactPersion());
                cbContact00.setSelected(poClient.InstiContact(pnContactPerson).getcPayeexxx().equalsIgnoreCase("1"));

                if (poClient.InstiContact(pnContactPerson).getsRoleIDxx() == null || poClient.InstiContact(pnContactPerson).getsRoleIDxx().isEmpty()) {
                    txtContact08.clear();
                    pbLoadingData = false;
                    return;
                }

                poJSON = poClient.Role().searchRecord(poClient.InstiContact(pnContactPerson).getsRoleIDxx(), true);
                if (poJSON != null) {
                    if ("success".equals((String) poJSON.get("result"))) {
                        txtContact08.setText(poClient.Role().getModel().getsRoleDesc());
                    } else {
                        txtContact08.clear();
                        System.out.print(poJSON.get("message"));
                    }
                } else {
                    txtContact08.clear();
                    System.out.print("No record loaded");
                }

            }
            pbLoadingData = false;

        } catch (Exception e) {
            poWrapper.severe(e.getMessage());
        }
    }

    private void initCompanyCheckbox() {
        CheckBox[] cbAddressCheckboxes = {cbAddress01, cbAddress02, cbAddress08};

        for (CheckBox checkbox : cbAddressCheckboxes) {
            // Capture the current checkbox
            checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (!pbLoaded) {
                        return;
                    }

                    JSONObject loJSON;
                    String id = checkbox.getId();
                    String numberPart = id.substring(id.length() - 2);

                    try {
                        int number = Integer.parseInt(numberPart);
                        switch (number) {
                            case 1: // Active
                                loJSON = poClient.Address(pnCompany).setRecordStatus(newValue ? "1" : "0");
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                } else {
                                    if (!checkbox.isSelected()) {
                                        poClient.Address(pnCompany).isPrimaryAddress(false);
                                    }
                                }
                                getSelectedAddress();
                                break;
                            case 2: // Primary Address || Restricted to 1 Primary Address
                                loJSON = poClient.Address(pnCompany).isPrimaryAddress(newValue);
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                } else {
                                    if (checkbox.isSelected()) {
                                        poClient.Address(pnCompany).setRecordStatus("1");
                                    }
                                }
                                //initialize full primary address to client master
                                String lshouseno = poClient.Address(pnCompany).getHouseNo() == null || poClient.Address(pnCompany).getHouseNo().isEmpty() ? "" : poClient.Address(pnCompany).getHouseNo() + " ";
                                String lsaddress = poClient.Address(pnCompany).getAddress() == null || poClient.Address(pnCompany).getAddress().isEmpty() ? "" : poClient.Address(pnCompany).getAddress();
                                String lsbrgy = poClient.Address(pnCompany).Barangay().getBarangayName() == null || poClient.Address(pnCompany).Barangay().getBarangayName().isEmpty() ? "" : ", " + poClient.Address(pnCompany).Barangay().getBarangayName();
                                String lscity = poClient.Address(pnCompany).Town().getDescription() == null || poClient.Address(pnCompany).Town().getDescription().isEmpty() ? " " : ", " + poClient.Address(pnCompany).Town().getDescription();
                                String lsprovince = poClient.Address(pnCompany).Town().Province().getDescription() == null || poClient.Address(pnCompany).Town().Province().getDescription().isEmpty() ? " " : " " + poClient.Address(pnCompany).Town().Province().getDescription();

                                poClient.getModel().setAdditionalInfo(lshouseno + lsaddress + lsbrgy + lscity + lsprovince);

                                if (!pbLoadingData) {
                                    for (int in = 0; in < poClient.getAddressCount(); in++) {
                                        if (in != pnCompany) {
                                            poClient.Address(in).isPrimaryAddress(false);
                                        }
                                    }
                                }
                                loadRecordAddress();
                                break;

                            case 8: // LTMS
                                loJSON = poClient.Address(pnCompany).isLTMSAddress(newValue);
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                }
                                break;
                            default:
                                System.out.println("Unknown checkbox selected");
                                break;
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(InstitutionNewController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (GuanzonException ex) {
                        Logger.getLogger(InstitutionNewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
    }

    private void initContactPersonCheckbox() {

        CheckBox[] cbMobileCheckboxes = {cbContact01, cbContact02, cbContact00};

        for (CheckBox checkbox : cbMobileCheckboxes) {

            // Capture the current checkbox
            checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (!pbLoaded) {
                        return;
                    }

                    JSONObject loJSON;
                    String id = checkbox.getId();
                    String numberPart = id.substring(id.length() - 2);

                    try {
                        int number = Integer.parseInt(numberPart);
                        switch (number) {
                            case 0: //Payee
                                loJSON = poClient.InstiContact(pnContactPerson).setcPayeexxx(checkbox.isSelected() ? "1" : "0");
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                }
                                break;

                            case 1: // Active
                                loJSON = poClient.InstiContact(pnContactPerson).setRecordStatus(checkbox.isSelected() ? "1" : "0");
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                } else {
                                    if (!checkbox.isSelected()) {
                                        poClient.InstiContact(pnContactPerson).isPrimaryContactPersion(false);
                                    }
                                }
                                loadContactPerson();
                                break;

                            case 2: // Primary
                                loJSON = poClient.InstiContact(pnContactPerson).isPrimaryContactPersion(checkbox.isSelected() ? true : false);
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                } else {
                                    if (checkbox.isSelected()) {
                                        poClient.InstiContact(pnContactPerson).setRecordStatus("1");
                                    }
                                }

                                if (!pbLoadingData) {
                                    for (int in = 0; in < poClient.getInstiContactCount(); in++) {
                                        if (in != pnContactPerson) {
                                            poClient.InstiContact(in).isPrimaryContactPersion(false);
                                        }
                                    }
                                }
                                loadContactPerson();
                                break;

                            default:
                                System.out.println("Unknown checkbox selected");
                                break;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            });
        }
    }

    private void searchContactPerson(String lsValue) throws Exception {

        //change client type temporary for searching client individual
        poClient.setClientType(ClientType.INDIVIDUAL);

        poJSON = poClient.searchContactPerson(lsValue, false);
        if (poJSON == null) {
            ShowMessageFX.Warning(getStage(), "No record to load", MODULE, "");
            return;
        }

        if ("success".equalsIgnoreCase(poJSON.get("result").toString())) {
            if (poClient.ContactPerson().getModel().getClientId() == null || poClient.ContactPerson().getModel().getClientId().isEmpty()) {
                ShowMessageFX.Warning(getStage(), "No record loaded", MODULE, "");
                return;
            }

            poClient.openContactRecord(poClient.ContactPerson().getModel().getClientId(), pnContactPerson);

            loadContactPerson();
        } else {
            ShowMessageFX.Warning(getStage(), (String) poJSON.get("message"), MODULE, "");
        }

        //change back to institution
        poClient.setClientType(ClientType.INSTITUTION);

    }

    private void addContactPerson() throws Exception {
        String lsClientId = poClient.InstiContact(pnContactPerson).getcCPrsonID();

        //initialize Client GUI
        ClientGUI loClient = new ClientGUI();

        loClient.setGRider(poGRider);
        loClient.setLogWrapper(null);
        loClient.setCategoryCode((String) psCategory);

        //filter client type 
        loClient.setClientType(ClientType.INDIVIDUAL);

        //set search by code
        loClient.setByCode(false);

        //initialize empty client to create new entry, else, load client id
        loClient.setClientId(lsClientId);

        // Get screen bounds
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        //re align current form to left
        getStage().setX((getStage().getWidth() / 5));

        //show second form to right side
        loClient.setStagePosition((screenBounds.getMaxX() - (getStage().getX() + getStage().getWidth())), getStage().getY());

        //load record
        CommonUtils.showModal(loClient);

        //load if button 
        if (!loClient.isCancelled()) {

            //if closed, re center form
            getStage().centerOnScreen();

            poClient.openContactRecord(loClient.getClient().getModel().getClientId(), pnContactPerson);

            loadContactPerson();
            return;
        } else {

        }

        //if closed, re center form
        getStage().centerOnScreen();

    }

    private void searchContactRole(String lsValue) throws Exception {

        poJSON = poClient.Role().searchRecord(lsValue, false);
        if (poJSON == null) {
            ShowMessageFX.Warning(getStage(), "No record to load", "Search Role", null);
            return;
        }
        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
            ShowMessageFX.Warning(getStage(), poJSON.get("message").toString(), "Search Role", null);
            return;
        }

        if (poClient.Role().getModel().getRoleIDxx() == null || poClient.Role().getModel().getRoleIDxx().isEmpty()) {
            ShowMessageFX.Warning(getStage(), "No record loaded", "Search Role", null);
            return;
        }

        poClient.InstiContact(pnContactPerson).setsRoleIDxx(poClient.Role().getModel().getRoleIDxx());

        loadContactPerson();

    }

    private void addContactRole() throws Exception {
        String lsRoleId = poClient.InstiContact(pnContactPerson).getsRoleIDxx();
        txtContact08.setText("");
        //initialize Client GUI
        ClientGUI loClient = new ClientGUI();

        loClient.setGRider(poGRider);
        loClient.setLogWrapper(null);
        loClient.setCategoryCode((String) psCategory);

        //Opens Contact Role Entry Form
        loClient.setClientType("2");

        //set search by code
        loClient.setByCode(false);

        //initialize empty role to create new entry, else, load role id
        loClient.setRoleID("");

        // Get screen bounds
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        //re align current form to left
        getStage().setX((getStage().getWidth() / 5));

        //show second form to right side
        loClient.setStagePosition((screenBounds.getMaxX() - (getStage().getX() + getStage().getWidth())), (screenBounds.getMaxY() - (getStage().getY() + getStage().getHeight())));

        //load record
        CommonUtils.showModal(loClient);

        //load if button 
        if (!loClient.isCancelled()) {

            //if closed, re center form
            getStage().centerOnScreen();
            poClient.InstiContact(pnContactPerson).setsRoleIDxx(loClient.getRole().getModel().getRoleIDxx());

            loadContactPerson();
            return;
        } else {
            poClient.InstiContact(pnContactPerson).setsRoleIDxx(lsRoleId);
            loadContactPerson();
        }

        //if closed, re center form
        getStage().centerOnScreen();

    }

    public Stage getStage() {
        return (Stage) AnchorMain.getScene().getWindow();
    }

    public static void applyMask(final String format,
            final TextField textField) {

        final boolean[] isUpdating = {false};

        /*
     * Backspace handling
         */
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
                int caret = textField.getCaretPosition();
                String text = textField.getText();
                if (caret > 0 && caret <= text.length()) {
                    if (text.charAt(caret - 1) == '-') {
                        int newPos = caret - 1;
                        StringBuilder sb = new StringBuilder(text);
                        sb.deleteCharAt(caret - 1);
                        if (newPos - 1 >= 0) {
                            sb.deleteCharAt(newPos - 1);
                            newPos--;
                        }

                        isUpdating[0] = true;
                        textField.setText(formatText(sb.toString(), format));
                        textField.positionCaret(Math.max(newPos, 0));
                        isUpdating[0] = false;
                        event.consume();
                    }
                }
            }
        });

        /*
     * Realtime formatting
         */
        textField.textProperty().addListener((obs,
                oldValue,
                newValue) -> {

            if (isUpdating[0]) {
                return;
            }

            String formatted = formatText(newValue, format);

            if (!formatted.equals(newValue)) {
                int caret = textField.getCaretPosition();
                isUpdating[0] = true;
                textField.setText(formatted);

                if (caret > formatted.length()) {
                    caret = formatted.length();
                }

                textField.positionCaret(caret);

                isUpdating[0] = false;
            }
        });

        /*
     * Lost focus formatting
     * ONLY THIS TEXTFIELD WILL TRIGGER
         */
        textField.focusedProperty().addListener((obs,
                oldFocused,
                newFocused) -> {
            /*
         * Trigger only when THIS field lost focus
             */
            if (oldFocused && !newFocused) {
                String formatted = formatText(textField.getText(), format);
                isUpdating[0] = true;
                textField.setText(formatted);
                isUpdating[0] = false;
            }
        });
    }

    private static String formatText(String value,
            String format) {

        String digitsOnly = value.replaceAll("[^0-9]", "");

        StringBuilder formatted = new StringBuilder();

        int digitIndex = 0;
        for (int i = 0; i < format.length(); i++) {
            char maskChar = format.charAt(i);
            if (maskChar == 'X') {
                if (digitIndex < digitsOnly.length()) {
                    formatted.append(digitsOnly.charAt(digitIndex));
                    digitIndex++;
                } else {
                    break;
                }

            } else if (maskChar == '-') {
                if (digitIndex > 0 && digitIndex < digitsOnly.length()) {
                    formatted.append("-");
                }
            }
        }
        return formatted.toString();
    }
}
