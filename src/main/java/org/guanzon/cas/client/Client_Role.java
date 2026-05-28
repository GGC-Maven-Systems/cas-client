/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.client;

import java.sql.SQLException;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.cas.client.model.Model_Corporate_Role;
import org.guanzon.cas.client.services.ClientModels;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class Client_Role extends Parameter{
    
    private Model_Corporate_Role poModel;

    @Override
    public void initialize() throws SQLException, GuanzonException  {
        super.initialize();
        
        psRecdStat = Logical.YES;
       
        ClientModels model = new ClientModels(poGRider);
        poModel = model.ClientRole();
    }
    
    @Override
    public Model_Corporate_Role getModel() {
        return poModel;
    }
    
    @Override
    public String getSQ_Browse() {
        return 
                "SELECT " +
                    "sRoleIDxx, " +
                    "sRoleDesc " +
                   "FROM " +
                    "Corporate_Role " +
                   "WHERE " +
                    "cRecdStat= '1'"; 
    }
    @Override
    public JSONObject isEntryOkay() throws SQLException, GuanzonException, CloneNotSupportedException {
       poJSON = new JSONObject();
          if (poModel.getsRoleDesc() == null || poModel.getsRoleDesc().isEmpty()) {
               poJSON.put("result", "error");
               poJSON.put("message", "Role Description must not be empty.");
               return poJSON;
           }
       poJSON.put("result", "success");
       return poJSON;
    }
    @Override
    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException {
        
        if (value == null) value = "";
        
        String lsRoleIDxx = value;
        
        //show dialog (search by description)
        if (!byCode) {
            
            poJSON = ShowDialogFX.Search(poGRider,
                            getSQ_Browse(),
                            value,
                            "ID»Role",
                            "sRoleIDxx»sRoleDesc",
                            "sRoleIDxx»sRoleDesc",
                            byCode ? 0 : 1);
            
            if (poJSON == null) {
                poJSON = new JSONObject();
                poJSON.put("result", "error");
                poJSON.put("message", "No record loaded.");
                
                return poJSON;
            }
            
            //get selected role id
            lsRoleIDxx = poJSON.get("sRoleIDxx") == null ? "" : (String) poJSON.get("sRoleIDxx");
        }

        poJSON = poModel.openRecord(lsRoleIDxx);
        if (poJSON == null) {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");

            return poJSON;
        }
        return poJSON;
    }
    
}
