package org.guanzon.cas.client.controller;

import com.ibm.icu.impl.Assert;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
import javafx.stage.Stage;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.StringHelper;
import org.guanzon.appdriver.constant.ClientType;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.client.ClientInfo;
import org.guanzon.cas.client.services.ClientControllers;
import org.guanzon.cas.client.table.models.ModelAddress;
import org.guanzon.cas.client.table.models.ModelEmail;
import org.guanzon.cas.client.table.models.ModelMobile;
import org.guanzon.cas.client.table.models.ModelSocialMedia;
import org.guanzon.cas.parameter.Country;
import org.guanzon.cas.parameter.Province;
import org.guanzon.cas.parameter.TownCity;
import org.json.simple.JSONObject;
import javafx.util.StringConverter;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;

public class IndividualNewController implements Initializable {

    @FXML
    private AnchorPane AnchorMain;
    @FXML
    private AnchorPane draggablePane;
    @FXML
    private Button btnExit;
    @FXML
    private FontAwesomeIconView glyphExit;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
    @FXML
    private TextArea txtField03;
    @FXML
    private TabPane TabPane;
    @FXML
    private Tab PersonalInfo;
    @FXML
    private TextField txtPersonal02;
    @FXML
    private TextField txtPersonal03;
    @FXML
    private TextField txtPersonal04;
    @FXML
    private TextField txtPersonal05;
    @FXML
    private TextField txtPersonal06;
    @FXML
    private DatePicker txtPersonal07;
    @FXML
    private TextField txtPersonal08;
    @FXML
    private ComboBox cmbPersonal09;
    @FXML
    private ComboBox cmbPersonal10;
    @FXML
    private TextField txtPersonal11;
    @FXML
    private TextField txtPersonal12;
    @FXML
    private TextField txtPersonal13;
    @FXML
    private TextField txtPersonal15;
    @FXML
    private TextField txtPersonal14;
    @FXML
    private Label lblClientStatus;
    @FXML
    private Tab Address;
    @FXML
    private AnchorPane anchorPersonal;
    @FXML
    private AnchorPane anchorAddress;
    @FXML
    private AnchorPane anchorMobile;
    @FXML
    private AnchorPane anchorEmail;
    @FXML
    private AnchorPane anchorSocMed;
    @FXML
    private GridPane gridAddress;
    @FXML
    private GridPane gridMobile;
    @FXML
    private GridPane gridEmail;
    @FXML
    private GridPane gridSocMed;
    @FXML
    private TableView tblAddress;
    @FXML
    private TableColumn indexAddress01;
    @FXML
    private TableColumn indexAddress02;
    @FXML
    private TableColumn indexAddress03;
    @FXML
    private TableColumn indexAddress04;
    @FXML
    private TableColumn indexAddress05;
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
    private TextField txtAddress07;
    @FXML
    private CheckBox cbAddress01;
    @FXML
    private CheckBox cbAddress02;
    @FXML
    private CheckBox cbAddress03;
    @FXML
    private CheckBox cbAddress04;
    @FXML
    private CheckBox cbAddress05;
    @FXML
    private CheckBox cbAddress06;
    @FXML
    private CheckBox cbAddress07;
    @FXML
    private CheckBox cbAddress08;
    @FXML
    private Button btnAddAddress;
    @FXML
    private Tab Mobile;
    @FXML
    private ComboBox cmbMobile01;
    @FXML
    private ComboBox cmbMobile02;
    @FXML
    private TextField txtMobile01;
    @FXML
    private CheckBox cbMobileNo01;
    @FXML
    private CheckBox cbMobileNo02;
    @FXML
    private Button btnAddMobile;
    @FXML
    private TableView tblMobile;
    @FXML
    private TableColumn indexMobileNo01;
    @FXML
    private TableColumn indexMobileNo02;
    @FXML
    private TableColumn indexMobileNo03;
    @FXML
    private TableColumn indexMobileNo04;
    @FXML
    private Tab Email;
    @FXML
    private ComboBox cmbEmail01;
    @FXML
    private TextField txtEmail01;
    @FXML
    private CheckBox cbEmail01;
    @FXML
    private CheckBox cbEmail02;
    @FXML
    private Button btnAddEmail;
    @FXML
    private TableView tblEmail;
    @FXML
    private TableColumn indexEmail01;
    @FXML
    private TableColumn indexEmail02;
    @FXML
    private TableColumn indexEmail03;
    @FXML
    private Tab SocialMedia;
    @FXML
    private ComboBox cmbSocMed01;
    @FXML
    private TextField txtSocial01;
    @FXML
    private TextArea txtSocial02;
    @FXML
    private CheckBox cbSocMed01;
    @FXML
    private Button btnAddSocMed;
    @FXML
    private TableView tblSocMed;
    @FXML
    private TableColumn indexSocMed01;
    @FXML
    private TableColumn indexSocMed02;
    @FXML
    private TableColumn indexSocMed03;
    @FXML
    private TableColumn indexSocMed04;

    private final String MODULE = "Client Controller";
    private GRiderCAS poGRider;
    private LogWrapper poWrapper;

    private ClientInfo poClient;
    private String psClientID;
    private String psCategorycode;

    private int pnEditMode;
    private int pnAddress;
    private int pnMobile;
    private int pnEmail;
    private int pnSocmed;

    private ObservableList<ModelAddress> address_data = FXCollections.observableArrayList();
    private ObservableList<ModelMobile> mobile_data = FXCollections.observableArrayList();
    private ObservableList<ModelEmail> email_data = FXCollections.observableArrayList();
    private ObservableList<ModelSocialMedia> socialmedia_data = FXCollections.observableArrayList();

    private JSONObject poJSON;
    private boolean pbLoaded;
    private boolean pbCancelled;
    private boolean pbLoadingData;

    ObservableList<String> gender = FXCollections.observableArrayList("Male", "Female");
    ObservableList<String> civilstatus = FXCollections.observableArrayList("Single", "Married", "Separated", "Widowed", "Single Parent", "Single Parent w/ Live-in Partner");

    ObservableList<String> mobileOwn = ModelMobile.mobileOwn;
    ObservableList<String> mobileType = ModelMobile.mobileType;
    ObservableList<String> emailOwn = ModelEmail.emailOwn;
    ObservableList<String> socialTyp = ModelSocialMedia.socialTyp;

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

    public void setCategory(String categorycode) {
        psCategorycode = categorycode;
    }

    public String getCategory() {
        return psCategorycode;
    }

    public boolean isCancelled() {
        return pbCancelled;
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
            poClient.setClientType(ClientType.INDIVIDUAL);
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
                    JSONObject addObjAddress = poClient.addAddress();
                    if ("error".equals((String) addObjAddress.get("result"))) {
                        ShowMessageFX.Information(getStage(), (String) addObjAddress.get("message"), "Computerized Acounting System", MODULE);
                        break;
                    } else {
                        poClient.Address(pnAddress).setClientId(poClient.getModel().getClientId());
                        pnAddress = poClient.getAddressCount() - 1;
                        tblAddress.getSelectionModel().select(pnAddress + 1);
                        loadRecordAddress();

                        txtAddress03.requestFocus();
                    }
                    break;
                case "btnAddMobile":
                    JSONObject addObj = poClient.addMobile();
                    if ("error".equals((String) addObj.get("result"))) {
                        ShowMessageFX.Information(getStage(), (String) addObj.get("message"), "Computerized Acounting System", MODULE);
                        break;
                    } else {
                        poClient.Mobile(pnMobile).setClientId(poClient.getModel().getClientId());
                        pnMobile = poClient.getMobileCount() - 1;
                        tblMobile.getSelectionModel().select(pnMobile + 1);
                        loadRecordMobile();

                        txtMobile01.requestFocus();
                    }
                    break;
                case "btnAddEmail":
                    JSONObject addObjMail = poClient.addMail();

                    if ("error".equals((String) addObjMail.get("result"))) {
                        ShowMessageFX.Information(getStage(), (String) addObjMail.get("message"), "Computerized Acounting System", MODULE);
                        break;
                    } else {
                        poClient.Mail(pnEmail).setClientId(poClient.getModel().getClientId());
                        pnEmail = poClient.getMailCount() - 1;
                        tblEmail.getSelectionModel().select(pnEmail + 1);
                        loadRecordEmail();

                        txtEmail01.requestFocus();
                    }
                    break;
                case "btnAddSocMed":
                    JSONObject addSocMed = poClient.addSocMed();

                    if ("error".equals((String) addSocMed.get("result"))) {
                        ShowMessageFX.Information(getStage(), (String) addSocMed.get("message"), "Computerized Acounting System", MODULE);
                        break;
                    } else {
                        poClient.SocMed(pnSocmed).setClientId(poClient.getModel().getClientId());
                        pnSocmed = poClient.getSocMedCount() - 1;

                        tblSocMed.getSelectionModel().select(pnSocmed + 1);
                        loadRecordSocialMedia();

                        txtSocial01.requestFocus();
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

    private void address_Clicked(MouseEvent event) {
        pnAddress = tblAddress.getSelectionModel().getSelectedIndex();

        if (pnAddress >= 0) {
            getSelectedAddress();
        }
    }

    private void mobile_Clicked(MouseEvent event) {
        pnMobile = tblMobile.getSelectionModel().getSelectedIndex();

        if (pnMobile >= 0) {
            getSelectedMobile();
        }
    }

    private void email_Clicked(MouseEvent event) {
        pnEmail = tblEmail.getSelectionModel().getSelectedIndex();

        if (pnEmail >= 0) {
            getSelectedEmail();
        }
    }

    private void socmed_Clicked(MouseEvent event) {
        pnSocmed = tblSocMed.getSelectionModel().getSelectedIndex();

        if (pnSocmed >= 0) {
            getSelectedSocialMedia();
        }
    }

    private void txtPersonal_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        int lnIndex = Integer.parseInt(txtField.getId().substring(11, 13));

        String lsValue = txtField.getText();

        try {
            if (event.getCode() == F3 || event.getCode() == ENTER) {
                switch (lnIndex) {
                    case 6: //citizenship
                        poJSON = poClient.searcbNationality(lsValue, false);
                        if ("success".equals((String) poJSON.get("result"))) {
                            txtField.setText(poClient.getModel().Citizenship().getNationality());
                            CommonUtils.SetNextFocus(txtField);
                            event.consume();
                        }
                        break;
                    case 8: //birthplace
                        poJSON = poClient.searchBirthPlace(lsValue);
                        if ("success".equals((String) poJSON.get("result"))) {
                            txtField.setText(poClient.getModel().BirthTown().getDescription());
                            CommonUtils.SetNextFocus(txtField);
                            event.consume();
                        }
                        break;
                    case 11: //spouse
                        poJSON = poClient.searchSpouse(lsValue);
                        if ("success".equals((String) poJSON.get("result"))) {
                            txtField.setText(poClient.getSpouseName());
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

    private void txtAddress_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        int lnIndex = Integer.parseInt(txtField.getId().substring(10, 12));

        String lsValue = txtField.getText();

        try {
            if (event.getCode() == F3 || event.getCode() == ENTER) {
                switch (lnIndex) {
                    case 3: //province
                        poJSON = poClient.searchProvince(pnAddress, lsValue, false);

                        if ("success".equals((String) poJSON.get("result"))) {
                            txtField.setText(poClient.Address(pnAddress).Town().Province().getDescription());
                            loadRecordAddress();
                            CommonUtils.SetNextFocus(txtField);
                            event.consume();
                        }
                        break;
                    case 4: //town
                        poJSON = poClient.searchTown(pnAddress, lsValue, false);

                        if ("success".equals((String) poJSON.get("result"))) {
                            txtField.setText(poClient.Address(pnAddress).Town().getDescription());
                            txtAddress03.setText(poClient.Address(pnAddress).Town().Province().getDescription());
                            loadRecordAddress();
                            CommonUtils.SetNextFocus(txtField);
                            event.consume();
                        }
                        break;
                    case 5: //barangay
                        poJSON = poClient.searchBarangay(pnAddress, lsValue, false);

                        if ("success".equals((String) poJSON.get("result"))) {
                            txtField.setText(poClient.Address(pnAddress).Barangay().getBarangayName());
                            txtAddress04.setText(poClient.Address(pnAddress).Town().getDescription());
                            txtAddress03.setText(poClient.Address(pnAddress).Town().Province().getDescription());
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

    private void txtMobile_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();

        switch (event.getCode()) {
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField);
                break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        }
    }

    private void txtEmail_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();

        switch (event.getCode()) {
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField);
                break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        }
    }

    private void txtSocMed_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();

        switch (event.getCode()) {
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField);
                break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        }
    }

    private void txtAreaSocMed_KeyPressed(KeyEvent event) {
        TextArea txtField = (TextArea) event.getSource();

        switch (event.getCode()) {
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField);
                break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        }
    }

    final ChangeListener<? super Boolean> txtPersonal_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(11, 13));

        String lsValue = txtField.getText();

//        if (lsValue == null || lsValue.isEmpty()) return;
        if (!nv) {//lost focus
            if (lnIndex == 2 || lnIndex == 3 || lnIndex == 4 || lnIndex == 5) {
                if (!lsValue.isEmpty()) {
                    lsValue = CommonUtils.TitleCase(lsValue);
                }
            }

            switch (lnIndex) {
                case 2:
                    poJSON = poClient.getModel().setLastName(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(poClient.getModel().getLastName());
                    txtField02.setText(formatFullName(poClient.getModel().getLastName(), poClient.getModel().getFirstName(), poClient.getModel().getSuffixName(), poClient.getModel().getMiddleName()));
                    break;
                case 3:
                    poJSON = poClient.getModel().setFirstName(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(poClient.getModel().getFirstName());
                    txtField02.setText(formatFullName(poClient.getModel().getLastName(), poClient.getModel().getFirstName(), poClient.getModel().getSuffixName(), poClient.getModel().getMiddleName()));
                    break;
                case 4:
                    poJSON = poClient.getModel().setMiddleName(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(poClient.getModel().getMiddleName());
                    txtField02.setText(formatFullName(poClient.getModel().getLastName(), poClient.getModel().getFirstName(), poClient.getModel().getSuffixName(), poClient.getModel().getMiddleName()));
                    break;
                case 5:
                    poJSON = poClient.getModel().setSuffixName(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(poClient.getModel().getSuffixName());
                    txtField02.setText(formatFullName(poClient.getModel().getLastName(), poClient.getModel().getFirstName(), poClient.getModel().getSuffixName(), poClient.getModel().getMiddleName()));
                    break;
                case 11: //Spouse
                    if (lsValue.isEmpty()) {
                        poJSON = poClient.getModel().setSpouseId(lsValue);
                        if (!"success".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                        }

                        txtField.setText("");
                    }
                    break;
                case 12:
                    poJSON = poClient.getModel().setMothersMaidenName(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(poClient.getModel().getMothersMaidenName());
                    break;
                case 13:

                    String lsTinPattern = "^\\d{3}-\\d{2}-\\d{4}$";
                    if (!lsValue.matches(lsTinPattern)) {
                        ShowMessageFX.Warning(getStage(), "TIN Number is invalid", "Warning", MODULE);
                        return;
                    }

                    poJSON = poClient.getModel().setTaxIdNumber(lsValue);
                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(poClient.getModel().getTaxIdNumber());
                    break;
                case 14:
                    poJSON = poClient.getModel().setLTOClientId(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(poClient.getModel().getLTOClientId());
                    break;
                case 15:

                    String lsNatlIDPattern = "^\\d{3}-\\d{2}-\\d{6}$";
                    if (!lsValue.matches(lsNatlIDPattern)) {
                        ShowMessageFX.Warning(getStage(), "National ID is invalid", "Warning", MODULE);
                        return;
                    }

                    poJSON = poClient.getModel().setPhNationalId(lsValue);
                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(poClient.getModel().getPhNationalId());
                    break;
            }
        } else {//got focus
            txtField.selectAll();
        }
    };

    final ChangeListener<? super Boolean> txtAddress_Focus = (o, ov, nv) -> {
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
                case 1: //house no
                    poJSON = poClient.Address(pnAddress).setHouseNo(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(poClient.Address(pnAddress).getHouseNo());
                    break;
                case 2: //address
                    poJSON = poClient.Address(pnAddress).setAddress(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(poClient.Address(pnAddress).getAddress());
                    break;
                case 6: //latitude
                    if (!StringHelper.isNumeric(lsValue)) {
                        lsValue = "0.00";
                    }

                    poJSON = poClient.Address(pnAddress).setLatitude(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(String.valueOf(poClient.Address(pnAddress).getLatitude()));
                    break;
                case 7: //longitude
                    if (!StringHelper.isNumeric(lsValue)) {
                        lsValue = "0.00";
                    }

                    poJSON = poClient.Address(pnAddress).setLongitude(lsValue);

                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                    }

                    txtField.setText(String.valueOf(poClient.Address(pnAddress).getLongitude()));
                    break;
                case 3: //province
                    if(lsValue.isEmpty() && poClient.Address(pnAddress).getEditMode() == EditMode.ADDNEW){
                        try{
                            poClient.Address(pnAddress).setTownId("");
                            poClient.Address(pnAddress).setBarangayId("");
                            //Reset town model
                            poClient.Address(pnAddress).Town().initialize();
                        } catch (SQLException | GuanzonException ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                        }
                    }
                    break;
                case 4: //town
                    if(lsValue.isEmpty() && poClient.Address(pnAddress).getEditMode() == EditMode.ADDNEW){
                        try{
                            poClient.Address(pnAddress).setBarangayId("");
                            poClient.Address(pnAddress).setTownId("");
                            poClient.Address(pnAddress).Town().setTownId("");
                            poClient.Address(pnAddress).Town().setDescription("");
                        } catch (SQLException | GuanzonException ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                        }
                    }
                    break;
                case 5: //brgy
                    if(lsValue.isEmpty() && poClient.Address(pnAddress).getEditMode() == EditMode.ADDNEW){
                        poClient.Address(pnAddress).setBarangayId("");
                    }
                    break;
            }

            loadRecordAddress();
        } else {//got focus
            txtField.selectAll();
        }
    };

    final ChangeListener<? super Boolean> txtMobile_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(9, 11));

        String lsValue = txtField.getText();

        if (lsValue == null) {
            return;
        }
        if (!nv) { //lost focus
            switch (lnIndex) {
                case 1: //mobile
                    
                    String lsContactNo = lsValue == null
                        ? ""
                        : lsValue.replaceAll("[\\s()-]", "").trim();
                    if (!StringHelper.isNumeric(lsContactNo) && !lsValue.isEmpty()) {
                        ShowMessageFX.Warning(getStage(), "Mobile must be numeric.", "Warning", MODULE);
                        return;
//                    } else if (!lsValue.trim().matches("^(09\\d{9}|\\+639\\d{9})$") && !lsValue.isEmpty()) {
//                        ShowMessageFX.Warning(getStage(), "Mobile number is invalid", "Warning", MODULE);
//                        return; //Replace by script below to accept contact number for telephone or fax
                    } else {
                        if (!lsContactNo.isEmpty()) {
                            boolean isValid = false;
                            String lsMessage = "";
                            switch (poClient.Mobile(pnMobile).getMobileType()) {
                                // Mobile Number
                                case "0":
                                    isValid = lsContactNo.matches("^(09\\d{9}|\\+639\\d{9})$");
                                    lsMessage = "Please enter a valid mobile number.";
                                    break;
                                // Telephone
                                case "1":
                                    isValid = lsContactNo.matches("^(\\+63|0)?\\d{1,3}\\d{6,8}$");
                                    lsMessage = "Please enter a valid telephone number.";
                                    break;
                                // Fax
                                case "2":
                                    isValid = lsContactNo.matches("^(\\+63|0)?\\d{1,3}\\d{6,8}$");
                                    lsMessage = "Please enter a valid fax number.";
                                    break;
                                default:
                                    lsMessage = "Invalid contact number.";
                                    ShowMessageFX.Warning(
                                            getStage(),
                                            "Unknown contact type.",
                                            "Warning",
                                            MODULE
                                    );
                                    return;
                            }

                            if (!isValid) {
                                ShowMessageFX.Warning(
                                        getStage(),
                                        lsMessage,
                                        "Warning",
                                        MODULE
                                );
                                return;
                            }
                        }
                        
                        poJSON = poClient.Mobile(pnMobile).setMobileNo(lsValue);

                        if (!"success".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
                            return;
                        }
                    }
                    txtField.setText(String.valueOf(poClient.Mobile(pnMobile).getMobileNo()));
            }

            loadRecordMobile();
        } else { //got focus
            txtField.selectAll();
        }
    };

    final ChangeListener<? super Boolean> txtEmail_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));

        String lsValue = txtField.getText();

        if (lsValue == null) {
            return;
        }

        if (!nv) {//lost focus
            switch (lnIndex) {
                case 1: //email address
                    if (!CommonUtils.isValidEmail(lsValue)) {
                        ShowMessageFX.Warning(getStage(), "Invalid E-mail", "Computerized Acounting System", MODULE);
                        break;
                    }

                    poJSON = poClient.Mail(pnEmail).setMailAddress(lsValue);
                    if ("error".equalsIgnoreCase(poJSON.get("result").toString())) {
                        ShowMessageFX.Information(getStage(), (String) poJSON.get("message"), "Computerized Acounting System", MODULE);
                        return;
                    }
                    txtField.setText(String.valueOf(poClient.Mail(pnEmail).getMailAddress()));
                    break;
            }

            loadRecordEmail();
        } else {//got focus
            txtField.selectAll();
        }
    };

    final ChangeListener<? super Boolean> txtSocmed_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(9, 11));

        String lsValue = txtField.getText();

        if (lsValue == null) {
            return;
        }

        if (!nv) {//lost focus
            switch (lnIndex) {
                case 1:
                    if (lsValue.trim().isEmpty()) {
                        return;
                    }

                    poJSON = poClient.SocMed(pnSocmed).setAccount(lsValue);
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Information(getStage(), (String) poJSON.get("message"), "Computerized Acounting System", MODULE);
                        return;
                    }
                    txtField.setText(String.valueOf(poClient.SocMed(pnSocmed).getAccount()));
                    break;
            }

            loadRecordSocialMedia();
        } else {//got focus
            txtField.selectAll();
        }
    };

    final ChangeListener<? super Boolean> txtAreaSocmed_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextArea txtField = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(9, 11));

        String lsValue = txtField.getText();

        if (lsValue == null) {
            return;
        }

        if (!nv) {//lost focus
            switch (lnIndex) {
                case 2:
                    if (lsValue.trim().isEmpty()) {
                        return;
                    }

                    poJSON = poClient.SocMed(pnSocmed).setRemarks(lsValue.trim());
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Information(getStage(), (String) poJSON.get("message"), "Computerized Acounting System", MODULE);
                        return;
                    }
                    txtField.setText(String.valueOf(poClient.SocMed(pnSocmed).getRemarks()));
                    break;
            }

            loadRecordSocialMedia();
        } else {//got focus
            txtField.selectAll();
        }
    };

    public void initAddressGrid() {
        address_data.clear();

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

        tblAddress.setItems(address_data);
        tblAddress.getSelectionModel().select(pnAddress + 1);
        tblAddress.autosize();
    }

    private void initMobileGrid() {
        mobile_data.clear();

        indexMobileNo01.setStyle("-fx-alignment: CENTER;");
        indexMobileNo02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexMobileNo03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexMobileNo04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");

        indexMobileNo01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        indexMobileNo02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        indexMobileNo03.setCellValueFactory(new PropertyValueFactory<>("index03"));
        indexMobileNo04.setCellValueFactory(new PropertyValueFactory<>("index04"));

        tblMobile.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblMobile.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        tblMobile.setItems(mobile_data);
        tblMobile.autosize();
    }

    private void initEmailGrid() {
        email_data.clear();

        indexEmail01.setStyle("-fx-alignment: CENTER;");
        indexEmail02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexEmail03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");

        indexEmail01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        indexEmail02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        indexEmail03.setCellValueFactory(new PropertyValueFactory<>("index03"));

        tblEmail.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblEmail.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        tblEmail.setItems(email_data);
        tblEmail.autosize();
    }

    private void initSocialMediaGrid() {
        socialmedia_data.clear();

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
        tblSocMed.setItems(socialmedia_data);
        tblMobile.getSelectionModel().select(pnSocmed + 1);
        tblSocMed.autosize();
    }

    private void initFields() {
        applyMask("XXX-XX-XXXX", txtPersonal13);
        applyMask("XXX-XX-XXXXXX", txtPersonal15);

        txtPersonal02.focusedProperty().addListener(txtPersonal_Focus);
        txtPersonal03.focusedProperty().addListener(txtPersonal_Focus);
        txtPersonal04.focusedProperty().addListener(txtPersonal_Focus);
        txtPersonal05.focusedProperty().addListener(txtPersonal_Focus);
        txtPersonal06.focusedProperty().addListener(txtPersonal_Focus);
        txtPersonal08.focusedProperty().addListener(txtPersonal_Focus);
        txtPersonal11.focusedProperty().addListener(txtPersonal_Focus);
        txtPersonal12.focusedProperty().addListener(txtPersonal_Focus);
        txtPersonal13.focusedProperty().addListener(txtPersonal_Focus);
        txtPersonal14.focusedProperty().addListener(txtPersonal_Focus);
        txtPersonal15.focusedProperty().addListener(txtPersonal_Focus);

        txtPersonal02.setOnKeyPressed(this::txtPersonal_KeyPressed);
        txtPersonal03.setOnKeyPressed(this::txtPersonal_KeyPressed);
        txtPersonal04.setOnKeyPressed(this::txtPersonal_KeyPressed);
        txtPersonal05.setOnKeyPressed(this::txtPersonal_KeyPressed);
        txtPersonal06.setOnKeyPressed(this::txtPersonal_KeyPressed);
        txtPersonal08.setOnKeyPressed(this::txtPersonal_KeyPressed);
        txtPersonal11.setOnKeyPressed(this::txtPersonal_KeyPressed);
        txtPersonal12.setOnKeyPressed(this::txtPersonal_KeyPressed);
        txtPersonal14.setOnKeyPressed(this::txtPersonal_KeyPressed);

        txtAddress01.focusedProperty().addListener(txtAddress_Focus);
        txtAddress02.focusedProperty().addListener(txtAddress_Focus);
        txtAddress03.focusedProperty().addListener(txtAddress_Focus);
        txtAddress04.focusedProperty().addListener(txtAddress_Focus);
        txtAddress05.focusedProperty().addListener(txtAddress_Focus);
        txtAddress06.focusedProperty().addListener(txtAddress_Focus);
        txtAddress07.focusedProperty().addListener(txtAddress_Focus);

        txtAddress01.setOnKeyPressed(this::txtAddress_KeyPressed);
        txtAddress02.setOnKeyPressed(this::txtAddress_KeyPressed);
        txtAddress03.setOnKeyPressed(this::txtAddress_KeyPressed);
        txtAddress04.setOnKeyPressed(this::txtAddress_KeyPressed);
        txtAddress05.setOnKeyPressed(this::txtAddress_KeyPressed);
        txtAddress06.setOnKeyPressed(this::txtAddress_KeyPressed);
        txtAddress07.setOnKeyPressed(this::txtAddress_KeyPressed);

        txtMobile01.setOnKeyPressed(this::txtMobile_KeyPressed);
        txtMobile01.focusedProperty().addListener(txtMobile_Focus);

        txtEmail01.setOnKeyPressed(this::txtEmail_KeyPressed);
        txtEmail01.focusedProperty().addListener(txtEmail_Focus);

        txtSocial01.setOnKeyPressed(this::txtSocMed_KeyPressed);
        txtSocial02.setOnKeyPressed(this::txtAreaSocMed_KeyPressed);
        txtSocial01.focusedProperty().addListener(txtSocmed_Focus);
        txtSocial02.focusedProperty().addListener(txtAreaSocmed_Focus);

        btnExit.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnAddAddress.setOnAction(this::cmdButton_Click);
        btnAddMobile.setOnAction(this::cmdButton_Click);
        btnAddEmail.setOnAction(this::cmdButton_Click);
        btnAddSocMed.setOnAction(this::cmdButton_Click);

        tblAddress.setOnMouseClicked(this::address_Clicked);
        tblMobile.setOnMouseClicked(this::mobile_Clicked);
        tblEmail.setOnMouseClicked(this::email_Clicked);
        tblSocMed.setOnMouseClicked(this::socmed_Clicked);

        cmbPersonal09.setItems(gender);
        cmbPersonal10.setItems(civilstatus);
        cmbMobile01.setItems(mobileOwn);
        cmbMobile02.setItems(mobileType);
        cmbEmail01.setItems(emailOwn);
        cmbSocMed01.setItems(socialTyp);

        cmbPersonal09.setOnAction(event -> {
            poJSON = poClient.getModel().setGender(String.valueOf(cmbPersonal09.getSelectionModel().getSelectedIndex()));

            if (!"success".equals((String) poJSON.get("result"))) {
                ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
            }
        });

        cmbPersonal10.setOnAction(event -> {
            poJSON = poClient.getModel().setCivilStatus(String.valueOf(cmbPersonal10.getSelectionModel().getSelectedIndex()));
            if (!"success".equals((String) poJSON.get("result"))) {
                ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
            }
            if (Integer.parseInt(poClient.getModel().getCivilStatus()) != 1) {
                poClient.getModel().setSpouseId("");
                txtPersonal11.setText("");
                txtPersonal11.setDisable(true);
            } else {
                txtPersonal11.setDisable(false);
            }
        });

        cmbMobile01.setOnAction(event -> {
            poJSON = poClient.Mobile(pnMobile).setOwnershipType(String.valueOf(cmbMobile01.getSelectionModel().getSelectedIndex()));

            if (!"success".equals((String) poJSON.get("result"))) {
                ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
            }

            loadRecordMobile();
        });

        cmbMobile02.setOnAction(event -> {
            poJSON = poClient.Mobile(pnMobile).setMobileType(String.valueOf(cmbMobile02.getSelectionModel().getSelectedIndex()));

            if (!"success".equals((String) poJSON.get("result"))) {
                ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
            }

            loadRecordMobile();
        });

        cmbEmail01.setOnAction(event -> {
            poJSON = poClient.Mail(pnEmail).setOwnershipType(String.valueOf(cmbEmail01.getSelectionModel().getSelectedIndex()));

            if (!"success".equals((String) poJSON.get("result"))) {
                ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
            }

            loadRecordEmail();
        });

        cmbSocMed01.setOnAction(event -> {
            poJSON = poClient.SocMed(pnSocmed).setSocMedType(String.valueOf(cmbSocMed01.getSelectionModel().getSelectedIndex()));

            if (!"success".equals((String) poJSON.get("result"))) {
                ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Warning", MODULE);
            }

            loadRecordSocialMedia();
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        txtPersonal07.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });

        txtPersonal07.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Lost focus
                LocalDate selectedDate = txtPersonal07.getValue();
                System.out.println("this is date " + selectedDate);
                LocalDate localbdate = LocalDate.parse(selectedDate.toString(), formatter);
                String formattedDate = formatter.format(selectedDate);
                poClient.getModel().setBirthDate((SQLUtil.toDate(formattedDate, "yyyy-MM-dd")));
                txtPersonal07.setValue(localbdate);
            }
        });

        initAddressCheckbox();
        initMobileCheckbox();
        initEmailCheckbox();
        initSocialMediaCheckbox();

        clearfields();
    }

    private void clearfields() {
        txtField01.setText("");
        txtField02.setText("");
        txtField03.setText("");

        txtPersonal02.setText("");
        txtPersonal03.setText("");
        txtPersonal04.setText("");
        txtPersonal05.setText("");
        txtPersonal06.setText("");
        txtPersonal07.setValue(LocalDate.now());
        txtPersonal08.setText("");
        txtPersonal11.setText("");
        txtPersonal12.setText("");
        txtPersonal13.setText("");
        txtPersonal14.setText("");
        txtPersonal15.setText("");

        txtAddress01.setText("");
        txtAddress02.setText("");
        txtAddress03.setText("");
        txtAddress04.setText("");
        txtAddress05.setText("");
        txtAddress06.setText("0.00");
        txtAddress07.setText("0.00");

        cbAddress01.selectedProperty().set(false);
        cbAddress02.selectedProperty().set(false);
        cbAddress03.selectedProperty().set(false);
        cbAddress04.selectedProperty().set(false);
        cbAddress05.selectedProperty().set(false);
        cbAddress06.selectedProperty().set(false);
        cbAddress07.selectedProperty().set(false);
        cbAddress08.selectedProperty().set(false);

        txtMobile01.setText("");
        cbMobileNo01.selectedProperty().set(false);
        cbMobileNo02.selectedProperty().set(false);

        txtEmail01.setText("");
        cbEmail01.selectedProperty().set(false);
        cbEmail02.selectedProperty().set(false);

        txtSocial01.setText("");
        txtSocial02.setText("");

        pnAddress = 0;
        pnMobile = 0;
        pnEmail = 0;
        pnSocmed = 0;

        initAddressGrid();
        initMobileGrid();
        initEmailGrid();
        initSocialMediaGrid();
    }

    private void loadRecord() {
        try {
            pnEditMode = psClientID.isEmpty() ? EditMode.ADDNEW : EditMode.UPDATE;

            if (pnEditMode == EditMode.ADDNEW) {
                poJSON = poClient.newRecord();
                if (!"success".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Error", MODULE);
                    getStage().close();
                }

                anchorPersonal.setDisable(false);
                gridAddress.setDisable(false);
                gridMobile.setDisable(false);
                gridEmail.setDisable(false);
                gridSocMed.setDisable(false);

                lblClientStatus.setText("***NEW CUSTOMER");
            } else {
                poJSON = poClient.openClientRecord(psClientID);
                if (!"success".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Error", MODULE);
                    getStage().close();
                }

                poJSON = poClient.updateRecord();
                if (!"success".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Error(getStage(), (String) poJSON.get("message"), "Error", MODULE);
                    getStage().close();
                }

//                anchorPersonal.setDisable(true);
//                gridAddress.setDisable(true);
//                gridMobile.setDisable(true);
//                gridEmail.setDisable(true);
//                gridSocMed.setDisable(true);
                lblClientStatus.setText("***REPEAT CUSTOMER");
            }

            txtField01.setText(poClient.getModel().getClientId());
            txtField02.setText(poClient.getModel().getCompanyName());
            txtField03.setText(""); //todo: put full address here      

            cmbPersonal09.getSelectionModel().selectFirst();
            poClient.getModel().setGender(String.valueOf(cmbPersonal09.getSelectionModel().getSelectedIndex()));

            cmbPersonal10.getSelectionModel().selectFirst();
            poClient.getModel().setCivilStatus(String.valueOf(cmbPersonal10.getSelectionModel().getSelectedIndex()));

            cmbMobile01.getSelectionModel().selectFirst();
            poClient.Mobile(pnMobile).setOwnershipType(String.valueOf(cmbMobile01.getSelectionModel().getSelectedIndex()));

            cmbMobile02.getSelectionModel().selectFirst();
            poClient.Mobile(pnMobile).setMobileType(String.valueOf(cmbMobile02.getSelectionModel().getSelectedIndex()));

            cmbEmail01.getSelectionModel().selectFirst();
            poClient.Mail(pnEmail).setOwnershipType(String.valueOf(cmbEmail01.getSelectionModel().getSelectedIndex()));

            cmbSocMed01.getSelectionModel().selectFirst();
            poClient.SocMed(pnSocmed).setSocMedType(String.valueOf(cmbSocMed01.getSelectionModel().getSelectedIndex()));

            loadRecordPersonalInfo();
            loadRecordAddress();
            loadRecordMobile();
            loadRecordEmail();
            loadRecordSocialMedia();

            if (pnEditMode == EditMode.ADDNEW) {
                txtPersonal02.requestFocus();
                txtPersonal02.selectAll();
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
            loadMasterAddress();

            int lnCtr;
            address_data.clear();

            if (poClient.getAddressCount() >= 0) {
                for (lnCtr = 0; lnCtr < poClient.getAddressCount(); lnCtr++) {
                    address_data.add(new ModelAddress(String.valueOf(lnCtr + 1),
                            (String) poClient.Address(lnCtr).getValue("sHouseNox"),
                            (String) poClient.Address(lnCtr).getValue("sAddressx"),
                            (String) poClient.Address(lnCtr).Town().getDescription(),
                            (String) poClient.Address(lnCtr).Barangay().getBarangayName()));
                }
            }
            
            loadMasterAddress();

            if (pnAddress < 0 || pnAddress >= address_data.size()) {
                if (!address_data.isEmpty()) {
                    /* FOCUS ON FIRST ROW */
                    tblAddress.getSelectionModel().select(0);
                    tblAddress.getFocusModel().focus(0);
                    pnAddress = tblAddress.getSelectionModel().getSelectedIndex();
                    getSelectedAddress();
                }
            } else {
                /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                tblAddress.getSelectionModel().select(pnAddress);
                tblAddress.getFocusModel().focus(pnAddress);
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
//                address = poClient.Address(i).getHouseNo() + " "
//                        + poClient.Address(i).getAddress() + " "
//                        + poClient.Address(i).Barangay().getBarangayName() + " "
//                        + poClient.Address(i).Town().getDescription() + ", "
//                        + poClient.Address(i).Town().Province().getDescription();
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
                txtAddress01.setText(poClient.Address(pnAddress).getHouseNo());
                txtAddress02.setText(poClient.Address(pnAddress).getAddress());
                txtAddress03.setText(poClient.Address(pnAddress).Town().Province().getDescription());
                txtAddress04.setText(poClient.Address(pnAddress).Town().getDescription());
                txtAddress05.setText(poClient.Address(pnAddress).Barangay().getBarangayName());
                txtAddress06.setText(String.valueOf(poClient.Address(pnAddress).getLatitude()));
                txtAddress07.setText(String.valueOf(poClient.Address(pnAddress).getLongitude()));

                cbAddress01.setSelected(("1".equals((String) poClient.Address(pnAddress).getRecordStatus())));
                cbAddress02.setSelected(poClient.Address(pnAddress).isPrimaryAddress());
                cbAddress03.setSelected(poClient.Address(pnAddress).isOfficeAddress());
                cbAddress04.setSelected(poClient.Address(pnAddress).isProvinceAddress());
                cbAddress05.setSelected(poClient.Address(pnAddress).isBillingAddress());
                cbAddress06.setSelected(poClient.Address(pnAddress).isShippingAddress());
                cbAddress07.setSelected(poClient.Address(pnAddress).isCurrentAddress());
                cbAddress08.setSelected(poClient.Address(pnAddress).isLTMSAddress());
            }
        } catch (SQLException | GuanzonException e) {
            e.printStackTrace();
        }
        pbLoadingData = false;
    }

    private void loadRecordEmail() {
        int lnCtr2 = 0;
        email_data.clear();
        if (poClient.getMailCount() >= 0) {
            for (int lnCtr = 0; lnCtr < poClient.getMailCount(); lnCtr++) {
                email_data.add(new ModelEmail(String.valueOf(lnCtr + 1),
                        poClient.Mail(lnCtr2).getValue("cOwnerxxx").toString(),
                        poClient.Mail(lnCtr2).getValue("sEMailAdd").toString()
                ));
                lnCtr2 += 1;
            }
        }
        if (pnEmail < 0 || pnEmail
                >= email_data.size()) {
            if (!email_data.isEmpty()) {
                /* FOCUS ON FIRST ROW */
                tblEmail.getSelectionModel().select(0);
                tblEmail.getFocusModel().focus(0);
                pnEmail = 0;//tblEmail.getSelectionModel().getSelectedIndex();
            }
            getSelectedEmail();
        } else {
            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
            tblEmail.getSelectionModel().select(pnEmail);
            tblEmail.getFocusModel().focus(pnEmail);
            getSelectedEmail();
        }
    }

    private void getSelectedEmail() {
        pbLoadingData = true;
        if (poClient.getMailCount() > 0) {
            txtEmail01.setText(poClient.Mail(pnEmail).getMailAddress());

            int lsOwnerType = 0;
            lsOwnerType = Integer.parseInt(poClient.Mail(pnEmail).getOwnershipType());
            cbEmail01.setSelected(("1".equals(poClient.Mail(pnEmail).getRecordStatus())));
            cbEmail02.setSelected(poClient.Mail(pnEmail).isPrimaryEmail());
            cmbEmail01.getSelectionModel().select(lsOwnerType);

        }
        pbLoadingData = false;
    }

    private void loadRecordMobile() {
        int lnCtr2 = 0;
        mobile_data.clear();

        if (poClient.getMobileCount() >= 0) {
            for (int lnCtr = 0; lnCtr < poClient.getMobileCount(); lnCtr++) {
                mobile_data.add(new ModelMobile(String.valueOf(lnCtr + 1),
                        poClient.Mobile(lnCtr2).getValue("sMobileNo").toString(),
                        poClient.Mobile(lnCtr2).getValue("cOwnerxxx").toString(),
                        poClient.Mobile(lnCtr2).getValue("cMobileTp").toString()
                ));
                lnCtr2 += 1;
            }

        }

        if (pnMobile < 0 || pnMobile
                >= mobile_data.size()) {
            if (!mobile_data.isEmpty()) {
                /* FOCUS ON FIRST ROW */
                tblMobile.getSelectionModel().select(0);
                tblMobile.getFocusModel().focus(0);
                pnMobile = 0;
            }
            getSelectedMobile();
        } else {
            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
            tblMobile.getSelectionModel().select(pnMobile);
            tblMobile.getFocusModel().focus(pnMobile);
            getSelectedMobile();
        }
    }

    private void getSelectedMobile() {
        pbLoadingData = true;
        if (poClient.getMobileCount() > 0) {
            try {
                txtMobile01.setText(poClient.Mobile(pnMobile).getMobileNo());

                int lsOwnerType = 0;
                int lsMobileType = 0;

                lsOwnerType = Integer.parseInt(poClient.Mobile(pnMobile).getOwnershipType());
                lsMobileType = Integer.parseInt(poClient.Mobile(pnMobile).getMobileType());

                cmbMobile01.getSelectionModel().select(lsOwnerType);
                cmbMobile02.getSelectionModel().select(lsMobileType);

                cbMobileNo01.setSelected(("1".equals((String) poClient.Mobile(pnMobile).getRecordStatus())));
                cbMobileNo02.setSelected(poClient.Mobile(pnMobile).isPrimaryMobile());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        pbLoadingData = false;

    }

    private void loadRecordSocialMedia() {
        int lnCtr2 = 0;
        socialmedia_data.clear();
        if (poClient.getSocMedCount() >= 0) {
            for (int lnCtr = 0; lnCtr < poClient.getSocMedCount(); lnCtr++) {
                socialmedia_data.add(new ModelSocialMedia(String.valueOf(lnCtr + 1),
                        poClient.SocMed(lnCtr2).getAccount(),
                        poClient.SocMed(lnCtr2).getSocMedType(),
                        poClient.SocMed(lnCtr2).getRemarks()
                ));
                lnCtr2 += 1;
            }
        }

        if (pnSocmed < 0 || pnSocmed
                >= socialmedia_data.size()) {
            if (!socialmedia_data.isEmpty()) {
                /* FOCUS ON FIRST ROW */
                tblSocMed.getSelectionModel().select(0);
                tblSocMed.getFocusModel().focus(0);
                pnSocmed = 0;//tblSocMed.getSelectionModel().getSelectedIndex();
            }
            getSelectedSocialMedia();
        } else {
            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
            tblSocMed.getSelectionModel().select(pnSocmed);
            tblSocMed.getFocusModel().focus(pnSocmed);
            getSelectedSocialMedia();
        }
    }

    private void getSelectedSocialMedia() {
        pbLoadingData = true;

        if (poClient.getSocMedCount() > 0) {
            int lsSocMedType = Integer.parseInt(poClient.SocMed(pnSocmed).getSocMedType());
            cmbSocMed01.getSelectionModel().select(lsSocMedType);

            txtSocial01.setText(poClient.SocMed(pnSocmed).getAccount());
            txtSocial02.setText(poClient.SocMed(pnSocmed).getRemarks());
            cbSocMed01.setSelected("1".equals((String) poClient.SocMed(pnSocmed).getRecordStatus()));
        }

        pbLoadingData = false;
    }

    private void loadRecordPersonalInfo() {
        try {
            txtPersonal02.setText(poClient.getModel().getLastName());
            txtPersonal03.setText(poClient.getModel().getFirstName());
            txtPersonal04.setText(poClient.getModel().getMiddleName());
            txtPersonal05.setText(poClient.getModel().getSuffixName());

            Country loCountry = new Country();
            loCountry.setApplicationDriver(poGRider);

            loCountry.setRecordStatus("1");
            loCountry.initialize();
            loCountry.openRecord(poClient.getModel().getCitizenshipId());
            txtPersonal06.setText(loCountry.getModel().getNationality());

            if (!poClient.getModel().getBirthDate().equals("")) {
                Object lobirthdate = poClient.getModel().getBirthDate();
                if (lobirthdate == null) {
                    txtPersonal07.setValue(LocalDate.now());
                } else if (lobirthdate instanceof Timestamp) {
                    Timestamp timestamp = (Timestamp) lobirthdate;
                    LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();
                    txtPersonal07.setValue(localDate);
                } else if (lobirthdate instanceof Date) {
                    Date sqlDate = (Date) lobirthdate;
                    LocalDate localDate = sqlDate.toLocalDate();
                    txtPersonal07.setValue(localDate);
                } else {
                }
            }

            if (!poClient.getModel().getBirthPlaceId().equals("")) {

                TownCity loTownCity = new TownCity();
                loTownCity.setApplicationDriver(poGRider);
                loTownCity.setRecordStatus("1");
                loTownCity.initialize();
                loTownCity.openRecord(poClient.getModel().getBirthPlaceId());

                Province loProvince = new Province();
                loProvince.setApplicationDriver(poGRider);
                loProvince.setRecordStatus("1");
                loProvince.initialize();
                loProvince.openRecord(loTownCity.getModel().getProvinceId());

                txtPersonal08.setText(loTownCity.getModel().getDescription() + ", " + loProvince.getModel().getDescription());
            }
            int lsGender = Integer.parseInt(poClient.getModel().getGender());
            cmbPersonal09.getSelectionModel().select(lsGender);

            int lsCivilStatus = Integer.parseInt(poClient.getModel().getCivilStatus());
            cmbPersonal10.getSelectionModel().select(lsCivilStatus);

            txtPersonal11.setDisable(lsCivilStatus != 1);
            txtPersonal11.setText(poClient.getSpouseName());
            txtPersonal12.setText(poClient.getModel().getMothersMaidenName());
            txtPersonal13.setText(poClient.getModel().getTaxIdNumber());
            txtPersonal14.setText(poClient.getModel().getLTOClientId());
            txtPersonal15.setText(poClient.getModel().getPhNationalId());
        } catch (SQLException | GuanzonException e) {
            e.printStackTrace();
        }
    }

    private void initAddressCheckbox() {
        CheckBox[] cbAddressCheckboxes = {cbAddress01, cbAddress02, cbAddress03, cbAddress04, cbAddress05, cbAddress06, cbAddress07, cbAddress08};

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
                            case 1: //
                                loJSON = poClient.Address(pnAddress).setRecordStatus(newValue ? "1" : "0");
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                }
                                getSelectedAddress();
                                break;
                            case 2: // Primary Address || Restricted to 1 Primary Address
                                poClient.Address(pnAddress).isPrimaryAddress(newValue);

                                if (!pbLoadingData) {
                                    for (int in = 0; in < poClient.getAddressCount(); in++) {
                                        if (in != pnAddress) {
                                            poClient.Address(in).isPrimaryAddress(false);
                                        }
                                    }
                                }
                                
                                loadRecordAddress();
                                break;
                            case 3: //Office
                                loJSON = poClient.Address(pnAddress).isOfficeAddress(newValue);
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                }
                                break;
                            case 4: // Province
                                loJSON = poClient.Address(pnAddress).isProvinceAddress(newValue);
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                }
                                break;
                            case 5: // Billing
                                loJSON = poClient.Address(pnAddress).isBillingAddress(newValue);
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                }
                                break;
                            case 6: // Shipping
                                loJSON = poClient.Address(pnAddress).isShippingAddress(newValue);
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                }
                                break;
                            case 7: // Current
                                loJSON = poClient.Address(pnAddress).isCurrentAddress(newValue);
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                }
                                break;
                            case 8: // LTMS
                                loJSON = poClient.Address(pnAddress).isLTMSAddress(newValue);
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                }
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

    private void initMobileCheckbox() {
        CheckBox[] cbMobileCheckboxes = {cbMobileNo01, cbMobileNo02};
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
                            case 1: // Active
                                loJSON = poClient.Mobile(pnMobile).setRecordStatus(checkbox.isSelected() ? "1" : "0");
                                if ("error".equals((String) loJSON.get("result"))) {
                                    Assert.fail((String) loJSON.get("message"));
                                }
                                break;
                            case 2: // Primary Address || Restricted to 1 Primary Address
                                poClient.Mobile(pnMobile).isPrimaryMobile(newValue);

                                if (!pbLoadingData) {
                                    for (int in = 0; in < poClient.getMobileCount(); in++) {
                                        if (in != pnMobile) {
                                            poClient.Mobile(in).isPrimaryMobile(false);
                                        }
                                    }
                                }
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

    private void initEmailCheckbox() {
        CheckBox[] cbEmailCheckboxes = {cbEmail01, cbEmail02};

        for (CheckBox checkbox : cbEmailCheckboxes) {
            // Capture the current checkbox
            checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (!pbLoaded) {
                        return;
                    }

                    String id = checkbox.getId();
                    String numberPart = id.substring(id.length() - 2);

                    try {
                        int number = Integer.parseInt(numberPart);

                        switch (number) {
                            case 1:
                                poJSON = poClient.Mail(pnEmail).setRecordStatus(checkbox.isSelected() ? "1" : "0");

                                if ("error".equals((String) poJSON.get("result"))) {
                                    Assert.fail((String) poJSON.get("message"));
                                }
                                break;
                            case 2: // Primary Email
                                poClient.Mail(pnEmail).isPrimaryEmail(newValue);

                                if (!pbLoadingData) {
                                    for (int in = 0; in < poClient.getMailCount(); in++) {
                                        if (in != pnEmail) {
                                            poClient.Mail(in).isPrimaryEmail(false);
                                        }
                                    }
                                }
                                break;
                            default:
                                System.out.println("Unknown checkbox selected");
                                break;
                        }

                        getSelectedEmail();
                    } catch (NumberFormatException e) {
                    }
                }
            });
        }
    }

    private void initSocialMediaCheckbox() {
        CheckBox[] cbSocMedCheckboxes = {cbSocMed01};
        for (CheckBox checkbox : cbSocMedCheckboxes) {
            checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (!pbLoaded) {
                        return;
                    }

                    String id = checkbox.getId();

                    String numberPart = id.substring(id.length() - 2);
                    try {
                        int number = Integer.parseInt(numberPart);
                        switch (number) {
                            case 1:
                                poClient.SocMed(pnSocmed).setRecordStatus(checkbox.isSelected() ? "1" : "0");
                                break;
                            default:
                                System.out.println("Unknown checkbox selected");
                                break;
                        }
                        getSelectedSocialMedia();
                    } catch (NumberFormatException e) {
                    }
                }
            });
        }
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
                if (digitIndex > 0 && digitIndex < digitsOnly.length() + 1) {
                    formatted.append("-");
                }
            }
        }
        return formatted.toString();
    }

    public static String formatFullName(
            String surname,
            String firstName,
            String suffix,
            String middleName) {

        return (cap(surname) + ", "
                + cap(firstName)
                + (isEmpty(suffix) ? "" : " " + cap(suffix))
                + (isEmpty(middleName) ? "" : " " + cap(middleName)))
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String cap(String s) {
        if (isEmpty(s)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String word : s.trim().split("\\s+")) {
            sb.append(Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase()).append(" ");
        }
        return sb.toString().trim();
    }
}
