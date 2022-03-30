// Decompiled Using: FrontEnd Plus v2.03 and the JAD Engine
// Available From: http://www.reflections.ath.cx
// Decompiler options: packimports(3) 
// Source File Name:   IOAction.java

package com.cgtsi.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.DynaValidatorActionForm;

import com.cgtsi.actionform.IOFormBean;
import com.cgtsi.admin.Administrator;
import com.cgtsi.admin.User;
import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.common.MessageException;
import com.cgtsi.common.NoDataException;
import com.cgtsi.inwardoutward.IODAO;
import com.cgtsi.inwardoutward.IOProcessor;
import com.cgtsi.inwardoutward.Inward;
import com.cgtsi.inwardoutward.Outward;
import com.cgtsi.knowledge.Document;
import com.cgtsi.knowledge.KnowledgeManager;
import com.cgtsi.knowledge.SearchCriteria;
import com.cgtsi.util.DBConnection;
import com.cgtsi.util.PropertyLoader;

// Referenced classes of package com.cgtsi.action:
//            BaseAction

public class IOAction extends BaseAction
{

    public ActionForward inwardNew(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        DynaActionForm dynaForm = (DynaActionForm)form;
        User user = getUserInformation(request);
        String userId = user.getUserId();
        String bankId = user.getBankId();
        String zoneId = user.getZoneId();
        String branchId = user.getBranchId();
        String memberId = bankId.concat(zoneId).concat(branchId);
        ArrayList members = (ArrayList)getMemberList();             
        dynaForm.set("getAllMembers", members);
        ArrayList userNames = getUserNames(memberId);
        dynaForm.set("getUserNames", userNames);
        members = null;
        userNames = null;
        dynaForm.set("bankNames", "");
        dynaForm.set("section", "");
        dynaForm.set("assignedTo", "");//assignedTo[i]
        dynaForm.set("txnType","");
        return mapping.findForward("success");
    }

    public ActionForward workshopEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "workshopEntry", "Entered");
        HttpSession session = request.getSession(false);
        DynaActionForm dynaForm = (DynaActionForm)form;
        dynaForm.initialize(mapping);
        Administrator admin = new Administrator();
        ArrayList mliNames = (ArrayList)getMLIList();
        dynaForm.set("mliNames", mliNames);
        ArrayList states = admin.getAllStates();
        dynaForm.set("statesList", states);
        ArrayList agencyNames = getAgenciesList();
        dynaForm.set("agencyNames", agencyNames);
        ArrayList governmentOrgsList = getGovtOrgsDtl();
        dynaForm.set("governmentOrgsList", governmentOrgsList);
        mliNames = null;
        states = null;
        agencyNames = null;
        Log.log(4, "IOAction", "workshopEntry", "Exited");
        return mapping.findForward("success");
    }

    public ActionForward insertWorkshopDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "insertWorkshopDetails", "Entered");
        DynaValidatorActionForm dynaForm = (DynaValidatorActionForm)form;
        IODAO ioDAO = new IODAO();
        User loggedInUser = getUserInformation(request);
        String loggedUserId = loggedInUser.getUserId();
        String workshopDt = (String)dynaForm.get("workshopDt");
        String bankName = (String)dynaForm.get("mliName");
        String organisedfor = (String)dynaForm.get("organisedfor");
        String agencyName = (String)dynaForm.get("agencyName");
        String targetGroup = (String)dynaForm.get("targetGroup");
        String place = (String)dynaForm.get("place");
        String stateName = (String)dynaForm.get("stateName");
        String districtName = (String)dynaForm.get("districtName");
        String city = (String)dynaForm.get("city");
        String type = (String)dynaForm.get("type");
        String topic = (String)dynaForm.get("topic");
        Integer participantsValue = (Integer)dynaForm.get("participants");
        int participants = 0;
        if(participantsValue == null || participantsValue.equals("0"))
            participants = 0;
        else
            participants = participantsValue.intValue();
        String organisation = (String)dynaForm.get("organisation");
        String name = (String)dynaForm.get("name");
        String designation = (String)dynaForm.get("designation");
        String reasons = (String)dynaForm.get("reasons");
        String mliNames = (String)dynaForm.get("bankNames");
        String nonMliName = (String)dynaForm.get("sourceName");
        String zone = (String)dynaForm.get("zone");
        String mliId = (String)dynaForm.get("mliId");
        String governmentOrgs = (String)dynaForm.get("governmentOrgs");
        ioDAO.insertWorkshopDetails(workshopDt, bankName, agencyName, targetGroup, place, stateName, districtName, city, type, topic, participants, organisation, name, designation, reasons, loggedUserId, organisedfor, nonMliName, mliNames, zone, mliId, governmentOrgs);
        dynaForm.initialize(mapping);
        String message = "Workshop details saved Successfully";
        request.setAttribute("message", message);
        return mapping.findForward("success");
    }

    public ActionForward updateInwardDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        DynaActionForm dynaForm = (DynaActionForm)form;
        dynaForm.initialize(mapping);
        User user = getUserInformation(request);
        String userId = user.getUserId();
        return mapping.findForward("success");
    }

    public ActionForward updateSchemePropagation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        DynaActionForm dynaForm = (DynaActionForm)form;
        dynaForm.initialize(mapping);
        User user = getUserInformation(request);
        String userId = user.getUserId();
        Administrator admin = new Administrator();
        ArrayList mliNames = (ArrayList)getMLIList();
        dynaForm.set("mliNames", mliNames);
        ArrayList states = admin.getAllStates();
        dynaForm.set("statesList", states);
        ArrayList agencyNames = getAgenciesList();
        dynaForm.set("agencyNames", agencyNames);
        mliNames = null;
        states = null;
        agencyNames = null;
        return mapping.findForward("success");
    }

    public ActionForward displaySchemePropagationDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        DynaActionForm dynaForm = (DynaActionForm)form;
        String workshopId = (String)dynaForm.get("workshopId");
        IOFormBean ioFormBean = new IOFormBean();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        ArrayList propagationSummary = null;
        User user = getUserInformation(request);
        String userId = user.getUserId();
        String bankId = user.getBankId();
        String zoneId = user.getZoneId();
        String branchId = user.getBranchId();
        String memberId = bankId.concat(zoneId).concat(branchId);
        System.out.println("workshopId:" + workshopId);
        Administrator admin = new Administrator();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String systemDate = dateFormat.format(date);
        if(workshopId == null || workshopId.equals(""))
        {
            throw new NoDataException("Please Enter Valid workshop Id");
        } else
        {
            ioFormBean = getSchemePropagationDtls(workshopId);
            dynaForm.set("workshopDt", ioFormBean.getWorkshopDt());
            dynaForm.set("type", ioFormBean.getType());
            dynaForm.set("mliName", ioFormBean.getMliName());
            dynaForm.set("agencyName", ioFormBean.getAgencyName());
            dynaForm.set("organisedfor", ioFormBean.getOrganisedfor());
            dynaForm.set("bankNames", ioFormBean.getBankNames());
            dynaForm.set("sourceName", ioFormBean.getSourceName());
            dynaForm.set("targetGroup", ioFormBean.getTargetGroup());
            dynaForm.set("stateName", ioFormBean.getStateName());
            ArrayList districtList = admin.getAllDistricts(ioFormBean.getStateName());
            dynaForm.set("districtList", districtList);
            dynaForm.set("districtName", ioFormBean.getDistrictName());
            dynaForm.set("city", ioFormBean.getCity());
            dynaForm.set("topic", ioFormBean.getTopic());
            dynaForm.set("participants", new Integer(ioFormBean.getParticipants()));
            dynaForm.set("name", ioFormBean.getName());
            dynaForm.set("organisation", ioFormBean.getOrganisation());
            dynaForm.set("reasons", ioFormBean.getReasons());
            ArrayList userNames = getUserNames(memberId);
            dynaForm.set("getUserNames", userNames);
            userNames = null;
            ioFormBean = null;
            return mapping.findForward("displaySchemepropagationDetails");
        }
    }

    public ActionForward afterUpdateSchemePropagationDtls(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        DynaActionForm dynaForm = (DynaActionForm)form;
        IOFormBean ioFormBean = new IOFormBean();
        IODAO ioDAO = new IODAO();
        String workshopId = (String)dynaForm.get("workshopId");
        BeanUtils.populate(ioFormBean, dynaForm.getMap());
        User user = getUserInformation(request);
        String userId = user.getUserId();
        String reasons = (String)dynaForm.get("reasons");
        ioDAO.afterUpdateSchemePropagationDtl(ioFormBean, userId);
        request.setAttribute("message", "<b>Scheme Propagation Details Modified Successfully");
        return mapping.findForward("success");
    }

    public ActionForward displayInwardDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        DynaActionForm dynaForm = (DynaActionForm)form;
        String inwardId = (String)dynaForm.get("inwardId");
        String instrumentNo = (String)dynaForm.get("sourceIds");
        IOFormBean ioFormBean = new IOFormBean();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        ArrayList inwardSummary = null;
        User user = getUserInformation(request);
        String userId = user.getUserId();
        String bankId = user.getBankId();
        String zoneId = user.getZoneId();
        String branchId = user.getBranchId();
        String memberId = bankId.concat(zoneId).concat(branchId);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String systemDate = dateFormat.format(date);
        if(inwardId == null || inwardId.equals("")){
            throw new NoDataException("Please Enter Valid Inward Id");
        }
        IODAO ioDAO = new IODAO();
        ioFormBean = ioDAO.getInwardDetails(inwardId, instrumentNo);
         dynaForm.set("instrumentNo", instrumentNo);
        dynaForm.set("inwardDts", dateFormat.format(ioFormBean.getInwardDts()));
        dynaForm.set("bankNames", ioFormBean.getBankNames());
        dynaForm.set("drawnonBank", ioFormBean.getDrawnOnBank());
        dynaForm.set("places", ioFormBean.getPlaces());
        dynaForm.set("subjects", ioFormBean.getSubjects());
        dynaForm.set("referenceIds", ioFormBean.getReferenceIds());
        dynaForm.set("ltrDt", dateFormat.format(ioFormBean.getLtrDts()));
        dynaForm.set("sourceIds", ioFormBean.getSourceIds());
        if(ioFormBean.getInstrumentDts() == null || ioFormBean.getInstrumentDts().equals(""))
            dynaForm.set("instrumentDt", "");
        else
            dynaForm.set("instrumentDt", dateFormat.format(ioFormBean.getInstrumentDts()));
        //dynaForm.set("instrumentAmt", new Double(ioFormBean.getInstrumentAmt()));
        dynaForm.set("instrumentAmtNew", new Double(ioFormBean.getInstrumentAmtNew()));
        dynaForm.set("section", ioFormBean.getSection());
        if(ioFormBean.getOutwardDt() == null || ioFormBean.getOutwardDt().equals(""))
            dynaForm.set("outwardDt", "");
        else
            dynaForm.set("outwardDt", dateFormat.format(ioFormBean.getOutwardDt()));
        dynaForm.set("outwardId", ioFormBean.getOutwardId());
        ArrayList userNames = getUserNames(memberId);
        dynaForm.set("assignedTo", ioFormBean.getAssignedTo());
        dynaForm.set("reasons", ioFormBean.getReasons());
        dynaForm.set("getUserNames", userNames);
        dynaForm.set("txnType", ioFormBean.getTxnType()); //dynaForm.set("txnType",""); may be this is for report page 21-jan-2014  added by bhuneshwar.Singh@pathinfotech.com
        
        
        ioFormBean = null;
        return mapping.findForward("displayInwardDetails");
    }

    public ActionForward afterUpdateInwardDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        DynaActionForm dynaForm = (DynaActionForm)form;
        IODAO ioDAO = new IODAO();
        String inwardId = (String)dynaForm.get("inwardId");
        String inwardDt = (String)dynaForm.get("inwardDts");
        String oldInstrumentNo = (String)dynaForm.get("instrumentNo");
        String instrumentNo = (String)dynaForm.get("sourceIds");
        String bankNames = (String)dynaForm.get("bankNames");
        String drawnonBank = (String)dynaForm.get("drawnonBank");
        String places = (String)dynaForm.get("places");
        String subjects = (String)dynaForm.get("subjects");
        String referenceIds = (String)dynaForm.get("referenceIds");
        String ltrDt = (String)dynaForm.get("ltrDt");
        String instrumentDt = (String)dynaForm.get("instrumentDt");
        //int instrumentAmt = ((Integer)dynaForm.get("instrumentAmt")).intValue();
        double instrumentAmt = ((Double)dynaForm.get("instrumentAmtNew")).doubleValue();
        String section = (String)dynaForm.get("section");
        String outwardId = (String)dynaForm.get("outwardId");
        String outwardDt = (String)dynaForm.get("outwardDt");
        User user = getUserInformation(request);
        String userId = user.getUserId();
        String assignedTo = (String)dynaForm.get("assignedTo");
        String reasons = (String)dynaForm.get("reasons");
        String txnType = (String)dynaForm.get("txnType");
        
        if(txnType == null || txnType.equals(""))
            throw new NoDataException("Please select txn type");
        ioDAO.afterUpdateInwardDetails(inwardId, oldInstrumentNo, instrumentNo, bankNames, drawnonBank, places, subjects, referenceIds, ltrDt, instrumentDt, instrumentAmt, section, userId, outwardId, outwardDt, assignedTo, reasons, inwardDt,txnType);
         //ioDAO.afterUpdateInwardDetails(inwardId, oldInstrumentNo, instrumentNo, bankNames, drawnonBank, places, subjects, referenceIds, ltrDt, instrumentDt, instrumentAmt, section, userId, outwardId, outwardDt, assignedTo, reasons, inwardDt);
         
           
        request.setAttribute("message", "<b>Inward Details Modified Successfully");
        return mapping.findForward("success");
    }

    private IOFormBean getSchemePropagationDtls(String workshopId)
        throws DatabaseException
    {
        ResultSet resultSet = null;
        IOFormBean ioFormBean = new IOFormBean();
        Connection connection = DBConnection.getConnection();
        try
        {
            CallableStatement callable = connection.prepareCall("{?=call Packgetschemepropagationdtls.funcGetSchemePropagationDtls(?,?,?)}");
            callable.registerOutParameter(1, 4);
            if(workshopId == null || workshopId.equalsIgnoreCase(""))
                callable.setNull(2, 12);
            else
                callable.setString(2, workshopId);
            callable.registerOutParameter(3, -10);
            callable.registerOutParameter(4, 12);
            callable.executeUpdate();
            int errorCode = callable.getInt(1);
            String error = callable.getString(4);
            resultSet = (ResultSet)callable.getObject(3);
            if(errorCode == 1)
            {
                callable.close();
                callable = null;
                System.out.println("Error:" + error);
                throw new DatabaseException(error);
            }
            for(; resultSet.next(); ioFormBean.setReasons(resultSet.getString(17)))
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String workshopDt = dateFormat.format(resultSet.getDate(2));
                ioFormBean.setWorkshopDt(workshopDt);
                ioFormBean.setType(resultSet.getString(3));
                ioFormBean.setMliName(resultSet.getString(4));
                ioFormBean.setAgencyName(resultSet.getString(5));
                ioFormBean.setOrganisedfor(resultSet.getString(6));
                ioFormBean.setBankNames(resultSet.getString(7));
                ioFormBean.setSourceName(resultSet.getString(8));
                ioFormBean.setTargetGroup(resultSet.getString(9));
                ioFormBean.setStateName(resultSet.getString(10));
                ioFormBean.setDistrictName(resultSet.getString(11));
                ioFormBean.setCity(resultSet.getString(12));
                ioFormBean.setTopic(resultSet.getString(13));
                ioFormBean.setParticipants(resultSet.getInt(14));
                ioFormBean.setName(resultSet.getString(15));
                ioFormBean.setOrganisation(resultSet.getString(16));
            }

            resultSet.close();
            resultSet = null;
            callable.close();
            callable = null;
        }
        catch(SQLException e)
        {
            Log.log(2, "IOAction", "getMemberList", e.getMessage());
            Log.logException(e);
            throw new DatabaseException("Failed to fetch Inward Source Names");
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        return ioFormBean;
    }

    public ActionForward addInwardSorceName(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        DynaActionForm dynaForm = (DynaActionForm)form;
        dynaForm.initialize(mapping);
        User user = getUserInformation(request);
        String userId = user.getUserId();
        return mapping.findForward("addInwardSource");
    }

    public ActionForward afterAddInwardSorceName(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        DynaActionForm dynaForm = (DynaActionForm)form;
        HttpSession session = request.getSession(false);
        User user = getUserInformation(request);
        String createdBy = user.getUserId();
        String sourceName = (String)dynaForm.get("sourceName");
        IOProcessor ioprocessor = new IOProcessor();
        ioprocessor.afterAddInwardSorceName(sourceName, createdBy);
        request.setAttribute("message", "<b>Source Name " + sourceName.toUpperCase() + " is Inserted Successfully ");
        return mapping.findForward("success");
    }

    private Collection getMemberList()
        throws DatabaseException
    {
        ArrayList memberList = new ArrayList();
        ResultSet resultSet = null;
        String inwardSourceName = "";
        Connection connection = DBConnection.getConnection();
        try
        {
            CallableStatement callable = connection.prepareCall("{?=call PackGetInwardPartyList.FuncGetInwardPartyList(?,?)}");
            callable.registerOutParameter(1, 4);
            callable.registerOutParameter(2, -10);
            callable.registerOutParameter(3, 12);
            callable.executeUpdate();
            int errorCode = callable.getInt(1);
            String error = callable.getString(3);
            resultSet = (ResultSet)callable.getObject(2);
            if(errorCode == 1)
            {
                callable.close();
                callable = null;
                throw new DatabaseException(error);
            }
            for(; resultSet.next(); memberList.add(inwardSourceName))
                inwardSourceName = resultSet.getString(1);

            resultSet.close();
            resultSet = null;
            callable.close();
            callable = null;
        }
        catch(SQLException e)
        {
            Log.log(2, "IOAction", "getMemberList", e.getMessage());
            Log.logException(e);
            throw new DatabaseException("Failed to fetch Inward Source Names");
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        return memberList;
    }

    public ArrayList getUserNames(String memberId)
        throws DatabaseException
    {
        Log.log(4, "AdminDAO", "getUsers", "Entered");
        Connection connection = DBConnection.getConnection(false);
        ArrayList userIdList = new ArrayList();
        try
        {
            CallableStatement callable = connection.prepareCall("{?=call packGetUsersForMember.funcGetUserNamesForMember(?,?,?)}");
            callable.setString(2, memberId);
            callable.registerOutParameter(1, 4);
            callable.registerOutParameter(4, 12);
            callable.registerOutParameter(3, -10);
            callable.execute();
            int functionReturn = callable.getInt(1);
            String error = callable.getString(4);
            if(functionReturn == 1)
            {
                connection.rollback();
                callable.close();
                callable = null;
                throw new DatabaseException(error);
            }
            ResultSet result;
            String userId;
            for(result = (ResultSet)callable.getObject(3); result.next(); userIdList.add(userId))
                userId = result.getString(1);

            Log.log(5, "AdminDAO", "getUsers", "userIdList" + userIdList);
            result.close();
            result = null;
            callable.close();
            callable = null;
            connection.commit();
        }
        catch(SQLException e)
        {
            try
            {
                connection.rollback();
            }
            catch(SQLException ignore)
            {
                Log.log(2, "AdminDAO", "getUsers", ignore.getMessage());
            }
            Log.log(2, "AdminDAO", "getUsers", e.getMessage());
            Log.logException(e);
            throw new DatabaseException("unable to get users");
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        Log.log(4, "AdminDAO", "getUsers", "Exited");
        return userIdList;
    }

    public ArrayList getUserNamesForSection(String section)
        throws DatabaseException
    {
        Log.log(4, "AdminDAO", "getUsers", "Entered");
        Connection connection = DBConnection.getConnection(false);
        ArrayList userIdList = new ArrayList();
        try
        {
            CallableStatement callable = connection.prepareCall("{?=call packGetUsersForMember.funcGetUserNamesForSection(?,?,?)}");
            callable.setString(2, section);
            callable.registerOutParameter(1, 4);
            callable.registerOutParameter(4, 12);
            callable.registerOutParameter(3, -10);
            callable.execute();
            int functionReturn = callable.getInt(1);
            String error = callable.getString(4);
            if(functionReturn == 1)
            {
                connection.rollback();
                callable.close();
                callable = null;
                throw new DatabaseException(error);
            }
            ResultSet result;
            String userId;
            for(result = (ResultSet)callable.getObject(3); result.next(); userIdList.add(userId))
                userId = result.getString(1);

            Log.log(5, "AdminDAO", "getUserNamesForSection", "userIdList" + userIdList);
            result.close();
            result = null;
            callable.close();
            callable = null;
            connection.commit();
        }
        catch(SQLException e)
        {
            try
            {
                connection.rollback();
            }
            catch(SQLException ignore)
            {
                Log.log(2, "AdminDAO", "getUserNamesForSection", ignore.getMessage());
            }
            Log.log(2, "AdminDAO", "getUserNamesForSection", e.getMessage());
            Log.logException(e);
            throw new DatabaseException("unable to get users");
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        Log.log(4, "AdminDAO", "getUserNamesForSection", "Exited");
        return userIdList;
    }

    private Collection getMLIList()
        throws DatabaseException
    {
        ArrayList mliList = new ArrayList();
        ResultSet resultSet = null;
        String inwardSourceName = "";
        Connection connection = DBConnection.getConnection();
        try
        {
            CallableStatement callable = connection.prepareCall("{?=call Packgetallmembersnew.funcGetAllMembersNew(?,?)}");
            callable.registerOutParameter(1, 4);
            callable.registerOutParameter(2, -10);
            callable.registerOutParameter(3, 12);
            callable.executeUpdate();
            int errorCode = callable.getInt(1);
            String error = callable.getString(3);
            resultSet = (ResultSet)callable.getObject(2);
            if(errorCode == 1)
            {
                callable.close();
                callable = null;
                throw new DatabaseException(error);
            }
            for(; resultSet.next(); mliList.add(inwardSourceName))
                inwardSourceName = resultSet.getString(3);

            resultSet.close();
            resultSet = null;
            callable.close();
            callable = null;
        }
        catch(SQLException e)
        {
            Log.log(2, "IOAction", "getMLIList", e.getMessage());
            Log.logException(e);
            throw new DatabaseException("Failed to fetch Inward Source Names");
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        return mliList;
    }

    public ArrayList getAgenciesList()
        throws DatabaseException
    {
        ArrayList agenciesList = new ArrayList();
        ResultSet resultSet = null;
        PreparedStatement pStmt = null;
        String agencyName = "";
        Connection connection = DBConnection.getConnection();
        try
        {
            String query = "SELECT INS_NAME FROM INSTITUTION_DETAIL ";
            pStmt = connection.prepareStatement(query);
            for(resultSet = pStmt.executeQuery(); resultSet.next(); agenciesList.add(agencyName))
                agencyName = resultSet.getString(1);

            resultSet.close();
            resultSet = null;
            pStmt.close();
            pStmt = null;
        }
        catch(Exception exception)
        {
            Log.logException(exception);
            throw new DatabaseException(exception.getMessage());
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        return agenciesList;
    }

    public ArrayList getGovtOrgsDtl()
        throws DatabaseException
    {
        ArrayList governmentOrgDtl = new ArrayList();
        ResultSet resultSet = null;
        PreparedStatement pStmt = null;
        String agencyName = "";
        Connection connection = DBConnection.getConnection();
        try
        {
            String query = "SELECT GOI_NAME FROM GOVERNMENT_ORGS_DETAIL ";
            pStmt = connection.prepareStatement(query);
            for(resultSet = pStmt.executeQuery(); resultSet.next(); governmentOrgDtl.add(agencyName))
                agencyName = resultSet.getString(1);

            resultSet.close();
            resultSet = null;
            pStmt.close();
            pStmt = null;
        }
        catch(Exception exception)
        {
            Log.logException(exception);
            throw new DatabaseException(exception.getMessage());
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        return governmentOrgDtl;
    }

    public ActionForward inwardInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        DynaActionForm dynaForm = (DynaActionForm)form;
        IOFormBean ioForm = new IOFormBean();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date systemDate = new Date();
        IODAO ioDAO = new IODAO();
        String sysDate = dateFormat.format(systemDate);
        User user = getUserInformation(request);
        String userId = user.getUserId();
        String inwardId = String.valueOf(ioDAO.getInwardId());
        String inwardDts[] = request.getParameterValues("inwardDts");
        String bankName = request.getParameter("bankNames");
        String places = request.getParameter("places");
        String referenceIds = request.getParameter("referenceIds");
        String ltrDts = request.getParameter("ltrDts");
        Date ltrDtsNew = null;
        if(ltrDts == null || ltrDts.equals(""))
            ltrDts = "";
        else
            try
            {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                ltrDtsNew = formatter.parse(ltrDts);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        String drawnonBank[] = request.getParameterValues("drawnonBank");
        String subject = request.getParameter("subjects");
        String sourceIds[] = request.getParameterValues("sourceIds");
        String instrumentDts[] = request.getParameterValues("instrumentDts");
        String instrumentAmt[] = request.getParameterValues("instrumentAmt");//instrumentAmt
        String section[] = request.getParameterValues("section");
        String assignedTo[] = request.getParameterValues("assignedTo");//assignedTo[i]
        String txnType[] = request.getParameterValues("txnType");
        
        int rowCount = 10;
        try
        {
            rowCount = Integer.parseInt(request.getParameter("rowcount"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Connection connection = DBConnection.getConnection(false);
        int count = 0;
        
//        for(int j=0;j<rowCount;j++){
//            System.out.println(drawnonBank[j]+"---"+sourceIds[j]+"---"+instrumentDts[j]+"---"+instrumentAmt[j]+"---"+section[j]+"---"+assignedTo[j]+"---"+txnType[j]);
//        }
        
        try
        {
            Statement str = connection.createStatement();
            for(int i = 0; i < rowCount; i++)
            {
                String instrumentDt = instrumentDts[i];
                Date instrumentDtNew = null;
                if(instrumentDt == null || instrumentDt.equals(""))
                    instrumentDt = "";
                else
                    try
                    {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        instrumentDtNew = formatter.parse(instrumentDt);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                if(instrumentDt == null || instrumentDt.equals(""))
                {
                   
                    
                    String query = "INSERT INTO INWARD_NEW ( INW_ID, INW_DT,BANK_NAME,DRAWN_ON_BANK, PLACE, SUBJECT, INSTRUMENT_NO, INSTRUMENT_DT,INSTRUMENT_AMT, LTR_REF_NO, LTR_DT,INWARD_SEC,INWARD_CREATED_BY,INWARD_CREATED_DT,INWARD_FLAG,INW_SEQ,ASSIGNED_USER,INW_CTS_TYPE) VALUES ('" + inwardId + "','" + dateFormat.format(new java.sql.Date(systemDate.getTime())) + "' ,'" + bankName + "' ,'" + drawnonBank[i] + "', '" + places + "' , '" + subject + "' , '" + sourceIds[i] + "' , " + "  '" + instrumentDt + "' , " + " '" + instrumentAmt[i] + "' , '" + referenceIds + "' , '" + dateFormat.format(new java.sql.Date(ltrDtsNew.getTime())) + "', '" + section[i] + "','" + userId + "',SYSDATE,'I','" + i + "','" + assignedTo[i] + "','"+ txnType[i] +"')";
                    
                    str.executeUpdate(query);
                    
                } else
                {
                    String query = "INSERT INTO INWARD_NEW ( INW_ID, INW_DT,BANK_NAME,DRAWN_ON_BANK, PLACE, SUBJECT, INSTRUMENT_NO, INSTRUMENT_DT,INSTRUMENT_AMT, LTR_REF_NO, LTR_DT,INWARD_SEC,INWARD_CREATED_BY,INWARD_CREATED_DT,INWARD_FLAG,INW_SEQ,ASSIGNED_USER,INW_CTS_TYPE) VALUES ('" + inwardId + "','" + dateFormat.format(new java.sql.Date(systemDate.getTime())) + "' ,'" + bankName + "' ,'" + drawnonBank[i] + "', '" + places + "' , '" + subject + "' , '" + sourceIds[i] + "' , " + "  '" + dateFormat.format(new java.sql.Date(instrumentDtNew.getTime())) + "' , " + " '" + instrumentAmt[i] + "' , '" + referenceIds + "' , '" + dateFormat.format(new java.sql.Date(ltrDtsNew.getTime())) + "', '" + section[i] + "','" + userId + "',SYSDATE,'I','" + i + "','" + assignedTo[i] + "' , '"+ txnType[i] +"' )";
                    str.executeUpdate(query);
                }
            }

            updateInwardSeq(inwardId);
            str.close();
            str = null;
            //connection.rollback();
        }
        catch(Exception exception)
        {
            Log.logException(exception);
            throw new DatabaseException(exception.getMessage());
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        request.setAttribute("message", "<b>Inward Details are Successfully Saved and Inward Number Is : " + inwardId);
         
        return mapping.findForward("success");
    }

    public void updateInwardSeq(String inwardId)
        throws DatabaseException
    {
        Connection connection = DBConnection.getConnection();
        int inwardSeq = Integer.parseInt(inwardId);
        Calendar calendar = Calendar.getInstance();
        Date fromDate = null;
        int year = calendar.get(1);
        int month = calendar.get(2);
        int day = calendar.get(5);
        calendar.set(5, 1);
        calendar.set(2, 3);
        if(month >= 0 && month <= 2)
            year--;
        calendar.set(1, year);
        fromDate = calendar.getTime();
        int count = 0;
        try
        {
            String query = "UPDATE INWARD_SEQUENCE SET INWARDNUMBER=? WHERE INWARDYEAR=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, inwardSeq);
            pstmt.setDate(2, new java.sql.Date(fromDate.getTime()));
            count = pstmt.executeUpdate();
            if(count == 0)
            {
                connection.commit();
            } else
            {
                pstmt.close();
                pstmt = null;
                connection.rollback();
            }
        }
        catch(Exception exception)
        {
            try
            {
                connection.rollback();
            }
            catch(SQLException ignore) { }
            Log.logException(exception);
            throw new DatabaseException(exception.getMessage());
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
    }

    public ActionForward addInward(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "addInward", "Entered");
        String ht = null;
        DynaActionForm dynaForm = (DynaActionForm)form;
        FormFile file = (FormFile)dynaForm.get("filePathInward");
        Inward inward = new Inward();
        String path = "";
        if(file != null && !file.toString().equals(""))
        {
            String contextPath = request.getSession().getServletContext().getRealPath("");
            path = PropertyLoader.changeToOSpath(contextPath + "/" + "WEB-INF/FileUpload" + File.separator + file.getFileName());
            String fileName = file.getFileName();
            int index = fileName.lastIndexOf(".");
            String name = fileName.substring(0, index);
            String type = fileName.substring(index);
            BeanUtils.populate(inward, dynaForm.getMap());
            inward.setFilePath(path);
            String memberId = null;
            ArrayList OfmemberIds = new ArrayList();
            IOProcessor ioprocessor = new IOProcessor();
            String mappedOutward = inward.getMappedOutwardID();
            ArrayList outwardIds = new ArrayList();
            outwardIds = ioprocessor.getAllOutwardIds();
            int count = 0;
            String tempId = "";
            if(mappedOutward != null && !mappedOutward.equals(""))
            {
                for(StringTokenizer stringTokenizer = new StringTokenizer(mappedOutward, ","); stringTokenizer.hasMoreTokens();)
                {
                    memberId = stringTokenizer.nextToken();
                    String newMemberId1 = memberId.trim();
                    String newMemberId2 = newMemberId1.toUpperCase();
                    if(!OfmemberIds.contains(newMemberId2))
                        OfmemberIds.add(newMemberId2);
                }

                int OfmemberIdsSize = OfmemberIds.size();
                for(int i = 0; i < OfmemberIdsSize; i++)
                {
                    String id = (String)OfmemberIds.get(i);
                    String newId = id.trim();
                    String newOutwardId = newId.toUpperCase();
                    if(outwardIds.contains(newOutwardId) && ++count == OfmemberIdsSize)
                    {
                        for(int j = 0; j < OfmemberIdsSize; j++)
                        {
                            String mappedInwardId = (String)OfmemberIds.get(j);
                            String newMappedInwardId = mappedInwardId.trim();
                            String mappedInwardId1 = newMappedInwardId.toUpperCase();
                            String mappedInwardId2 = mappedInwardId1 + ",";
                            tempId = tempId + mappedInwardId2;
                        }

                        inward.setMappedOutwardID(tempId);
                        User creatingUser = getUserInformation(request);
                        String user = creatingUser.getUserId();
                        inward.setProcessedBy(user);
                        ht = ioprocessor.addInwardDetail(inward);
                        int index1 = ht.indexOf("/");
                        int index2 = ht.lastIndexOf("/");
                        String part1 = ht.substring(0, index1);
                        String part2 = ht.substring(++index1, index2);
                        String part3 = ht.substring(++index2);
                        String inwardId = part1 + part2 + part3;
                        String nameOfFile = name + inwardId + type;
                        uploadFile(file, contextPath, nameOfFile);
                        if(ht != null)
                            dynaForm.set("inwardId", ht);
                        creatingUser = null;
                    }
                }

            } else
            {
                User creatingUser = getUserInformation(request);
                String user = creatingUser.getUserId();
                inward.setProcessedBy(user);
                ht = ioprocessor.addInwardDetail(inward);
                int index1 = ht.indexOf("/");
                int index2 = ht.lastIndexOf("/");
                String part1 = ht.substring(0, index1);
                String part2 = ht.substring(++index1, index2);
                String part3 = ht.substring(++index2);
                String inwardId = part1 + part2 + part3;
                String nameOfFile = name + inwardId + type;
                uploadFile(file, contextPath, nameOfFile);
                if(ht != null)
                    dynaForm.set("inwardId", ht);
                creatingUser = null;
            }
            inward = null;
            ioprocessor = null;
            Log.log(4, "IOAction", "addInward", "Exited");
        } else
        {
            BeanUtils.populate(inward, dynaForm.getMap());
            String memberId = null;
            ArrayList OfmemberIds = new ArrayList();
            IOProcessor ioprocessor = new IOProcessor();
            String mappedOutward = inward.getMappedOutwardID();
            ArrayList outwardIds = new ArrayList();
            outwardIds = ioprocessor.getAllOutwardIds();
            int count = 0;
            String tempId = "";
            if(mappedOutward != null && !mappedOutward.equals(""))
            {
                for(StringTokenizer stringTokenizer = new StringTokenizer(mappedOutward, ","); stringTokenizer.hasMoreTokens();)
                {
                    memberId = stringTokenizer.nextToken();
                    String newMemberId1 = memberId.trim();
                    String newMemberId2 = newMemberId1.toUpperCase();
                    if(!OfmemberIds.contains(newMemberId2))
                        OfmemberIds.add(newMemberId2);
                }

                int OfmemberIdsSize = OfmemberIds.size();
                for(int i = 0; i < OfmemberIdsSize; i++)
                {
                    String id = (String)OfmemberIds.get(i);
                    String newId = id.trim();
                    String newOutwardId = newId.toUpperCase();
                    if(outwardIds.contains(newOutwardId) && ++count == OfmemberIdsSize)
                    {
                        for(int j = 0; j < OfmemberIdsSize; j++)
                        {
                            String mappedInwardId = (String)OfmemberIds.get(j);
                            String newMappedInwardId = mappedInwardId.trim();
                            String mappedInwardId1 = newMappedInwardId.toUpperCase();
                            String mappedInwardId2 = mappedInwardId1 + ",";
                            tempId = tempId + mappedInwardId2;
                        }

                        inward.setMappedOutwardID(tempId);
                        User creatingUser = getUserInformation(request);
                        String user = creatingUser.getUserId();
                        inward.setProcessedBy(user);
                        ht = ioprocessor.addInwardDetail(inward);
                        if(ht != null)
                            dynaForm.set("inwardId", ht);
                        creatingUser = null;
                    }
                }

            } else
            {
                User creatingUser = getUserInformation(request);
                String user = creatingUser.getUserId();
                inward.setProcessedBy(user);
                ht = ioprocessor.addInwardDetail(inward);
                if(ht != null)
                    dynaForm.set("inwardId", ht);
                creatingUser = null;
            }
        }
        Log.log(4, "IOAction", "addInward", "Exited");
        return mapping.findForward("success");
    }

    public ActionForward showAddInward(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "showAddInward", "Entered");
        ArrayList Documents = new ArrayList();
        IOProcessor ioprocessor = new IOProcessor();
        DynaActionForm dynaForm = (DynaActionForm)form;
        dynaForm.initialize(mapping);
        Documents = ioprocessor.getAllDocumentTypes();
        dynaForm.set("documentTypes", Documents);
        Documents = null;
        ioprocessor = null;
        Log.log(4, "IOAction", "showAddInward", "Exited");
        return mapping.findForward("success");
    }

    public ActionForward showAddOutward(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "showAddOutward", "Entered");
        ArrayList Documents = new ArrayList();
        IOProcessor ioprocessor = new IOProcessor();
        DynaActionForm dynaForm = (DynaActionForm)form;
        dynaForm.initialize(mapping);
        Documents = ioprocessor.getAllDocumentTypes();
        dynaForm.set("documentTypes", Documents);
        Documents = null;
        ioprocessor = null;
        Log.log(4, "IOAction", "showAddOutward", "Exited");
        return mapping.findForward("success");
    }

    public ActionForward addOutward(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "addOutward", "Entered");
        DynaActionForm dynaForm = (DynaActionForm)form;
        FormFile file = (FormFile)dynaForm.get("filePathOutward");
        Outward outward = new Outward();
        String outwardId = null;
        String path = "";
        if(file != null && !file.toString().equals(""))
        {
            String contextPath = request.getSession().getServletContext().getRealPath("");
            path = PropertyLoader.changeToOSpath(contextPath + "/" + "WEB-INF/FileUpload" + File.separator + file.getFileName());
            String fileName = file.getFileName();
            int index = fileName.lastIndexOf(".");
            String name = fileName.substring(0, index);
            String type = fileName.substring(index);
            BeanUtils.populate(outward, dynaForm.getMap());
            outward.setFilePath(path);
            IOProcessor ioprocessor = new IOProcessor();
            ArrayList inwardIds = new ArrayList();
            inwardIds = ioprocessor.getAllInwardIds();
            String memberId = null;
            ArrayList OfmemberIds = new ArrayList();
            String mappedInward = outward.getMappedInward();
            int count = 0;
            String tempId = "";
            if(mappedInward != null && !mappedInward.equals(""))
            {
                for(StringTokenizer stringTokenizer = new StringTokenizer(mappedInward, ","); stringTokenizer.hasMoreTokens();)
                {
                    memberId = stringTokenizer.nextToken();
                    String newMemberId1 = memberId.trim();
                    String newMemberId2 = newMemberId1.toUpperCase();
                    if(!OfmemberIds.contains(newMemberId2))
                        OfmemberIds.add(newMemberId2);
                }

                int OfmemberIdsSize = OfmemberIds.size();
                for(int i = 0; i < OfmemberIdsSize; i++)
                {
                    String id = (String)OfmemberIds.get(i);
                    String newId = id.trim();
                    String newInwardId = newId.toUpperCase();
                    if(inwardIds.contains(newInwardId) && ++count == OfmemberIdsSize)
                    {
                        for(int j = 0; j < OfmemberIdsSize; j++)
                        {
                            String mappedInwardId = (String)OfmemberIds.get(j);
                            String newMappedInwardId = mappedInwardId.trim();
                            String mappedInwardId1 = newMappedInwardId.toUpperCase();
                            String mappedInwardId2 = mappedInwardId1 + ",";
                            tempId = tempId + mappedInwardId2;
                        }

                        outward.setMappedInward(tempId);
                        User creatingUser = getUserInformation(request);
                        String user = creatingUser.getUserId();
                        outward.setProcessedBy(user);
                        outwardId = ioprocessor.addOutwardDetail(outward);
                        int index1 = outwardId.indexOf("/");
                        int index2 = outwardId.lastIndexOf("/");
                        String part1 = outwardId.substring(0, index1);
                        String part2 = outwardId.substring(++index1, index2);
                        String part3 = outwardId.substring(++index2);
                        String inwardId = part1 + part2 + part3;
                        String nameOfFile = name + inwardId + type;
                        uploadFile(file, contextPath, nameOfFile);
                        dynaForm.set("outwardId", outwardId);
                        creatingUser = null;
                    }
                }

            } else
            {
                User creatingUser = getUserInformation(request);
                String user = creatingUser.getUserId();
                outward.setProcessedBy(user);
                outwardId = ioprocessor.addOutwardDetail(outward);
                int index1 = outwardId.indexOf("/");
                int index2 = outwardId.lastIndexOf("/");
                String part1 = outwardId.substring(0, index1);
                String part2 = outwardId.substring(++index1, index2);
                String part3 = outwardId.substring(++index2);
                String inwardId = part1 + part2 + part3;
                String nameOfFile = name + inwardId + type;
                uploadFile(file, contextPath, nameOfFile);
                if(outwardId != null)
                    dynaForm.set("outwardId", outwardId);
                creatingUser = null;
            }
            inwardIds = null;
            outward = null;
            ioprocessor = null;
            Log.log(4, "IOAction", "addOutward", "Exited");
        } else
        {
            BeanUtils.populate(outward, dynaForm.getMap());
            outward.setFilePath(path);
            IOProcessor ioprocessor = new IOProcessor();
            ArrayList inwardIds = new ArrayList();
            inwardIds = ioprocessor.getAllInwardIds();
            String memberId = null;
            ArrayList OfmemberIds = new ArrayList();
            String mappedInward = outward.getMappedInward();
            int count = 0;
            String tempId = "";
            if(mappedInward != null && !mappedInward.equals(""))
            {
                for(StringTokenizer stringTokenizer = new StringTokenizer(mappedInward, ","); stringTokenizer.hasMoreTokens();)
                {
                    memberId = stringTokenizer.nextToken();
                    String newMemberId1 = memberId.trim();
                    String newMemberId2 = newMemberId1.toUpperCase();
                    if(!OfmemberIds.contains(newMemberId2))
                        OfmemberIds.add(newMemberId2);
                }

                int OfmemberIdsSize = OfmemberIds.size();
                for(int i = 0; i < OfmemberIdsSize; i++)
                {
                    String id = (String)OfmemberIds.get(i);
                    String newId = id.trim();
                    String newInwardId = newId.toUpperCase();
                    if(inwardIds.contains(newInwardId) && ++count == OfmemberIdsSize)
                    {
                        for(int j = 0; j < OfmemberIdsSize; j++)
                        {
                            String mappedInwardId = (String)OfmemberIds.get(j);
                            String newMappedInwardId = mappedInwardId.trim();
                            String mappedInwardId1 = newMappedInwardId.toUpperCase();
                            String mappedInwardId2 = mappedInwardId1 + ",";
                            tempId = tempId + mappedInwardId2;
                        }

                        outward.setMappedInward(tempId);
                        User creatingUser = getUserInformation(request);
                        String user = creatingUser.getUserId();
                        outward.setProcessedBy(user);
                        outwardId = ioprocessor.addOutwardDetail(outward);
                        dynaForm.set("outwardId", outwardId);
                        creatingUser = null;
                    }
                }

            } else
            {
                User creatingUser = getUserInformation(request);
                String user = creatingUser.getUserId();
                outward.setProcessedBy(user);
                outwardId = ioprocessor.addOutwardDetail(outward);
                if(outwardId != null)
                    dynaForm.set("outwardId", outwardId);
                creatingUser = null;
            }
        }
        Log.log(4, "IOAction", "addOutward", "Exited");
        return mapping.findForward("success");
    }

    public ActionForward searchResult(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "searchResult", "Entered");
        KnowledgeManager knowledgeManager = new KnowledgeManager();
        HashMap searchResult = new HashMap();
        DynaActionForm dynaForm = (DynaActionForm)form;
        SearchCriteria searchCriteria = new SearchCriteria();
        BeanUtils.populate(searchCriteria, dynaForm.getMap());
        searchResult = knowledgeManager.searchForDocument(searchCriteria);
        dynaForm.set("documentDetails", searchResult);
        if(searchResult == null || searchResult.size() == 0)
        {
            throw new NoDataException("No Data is available for the values entered, Please Enter Any Other Value ");
        } else
        {
            searchResult = null;
            knowledgeManager = null;
            Log.log(4, "IOAction", "searchResult", "Exited");
            return mapping.findForward("success");
        }
    }

    public ActionForward uploadDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "uploadDocument", "Entered");
        ArrayList Documents = new ArrayList();
        IOProcessor ioprocessor = new IOProcessor();
        DynaActionForm dynaForm = (DynaActionForm)form;
        dynaForm.initialize(mapping);
        Documents = ioprocessor.getAllDocumentTypes();
        dynaForm.set("documentTypes", Documents);
        Documents = null;
        ioprocessor = null;
        Log.log(4, "IOAction", "uploadDocument", "Exited");
        return mapping.findForward("success");
    }

    public ActionForward uploadResult(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "uploadResult", "Entered");
        DynaActionForm dynaForm = (DynaActionForm)form;
        FormFile file = (FormFile)dynaForm.get("filePath");
        Document document = new Document();
        if(file != null)
        {
            String contextPath = request.getSession().getServletContext().getRealPath("");
            String path = PropertyLoader.changeToOSpath(contextPath + "/" + "WEB-INF/FileUpload" + File.separator + file.getFileName());
            BeanUtils.populate(document, dynaForm.getMap());
            document.setDocumentPath(path);
            uploadFile(file, contextPath);
        }
        KnowledgeManager knowledgeManager = new KnowledgeManager();
        User creatingUser = getUserInformation(request);
        String user = creatingUser.getUserId();
        document.setUser(user);
        knowledgeManager.storeDocument(document);
        Log.log(4, "IOAction", "uploadResult", "Exited");
        return mapping.findForward("success");
    }

    public ActionForward viewInward(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "viewInward", "Entered");
        Log.log(4, "IOAction", "viewInward", "Exited");
        return mapping.findForward("success");
    }

    public ActionForward viewOutward(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "viewOutward", "Entered");
        Log.log(4, "IOAction", "viewOutward", "Exited");
        return mapping.findForward("success");
    }

    public ActionForward showInwardSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "showInwardSummary", "Entered" + Runtime.getRuntime().freeMemory());
        IOProcessor ioprocessor = new IOProcessor();
        ArrayList inwardSummary = ioprocessor.showInwardStatusSummary();
        DynaActionForm dynaForm = (DynaActionForm)form;
        dynaForm.set("inwardSummary", inwardSummary);
        inwardSummary = null;
        ioprocessor = null;
        Log.log(4, "IOAction", "showInwardSummary", "Exited" + Runtime.getRuntime().freeMemory());
        return mapping.findForward("success");
    }

    public ActionForward showOutwardSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "showOutwardSummary", "Entered" + Runtime.getRuntime().freeMemory());
        IOProcessor ioprocessor = new IOProcessor();
        ArrayList outwardSummary = ioprocessor.showOutwardStatusSummary();
        DynaActionForm dynaForm = (DynaActionForm)form;
        dynaForm.set("outwardSummary", outwardSummary);
        outwardSummary = null;
        ioprocessor = null;
        Log.log(4, "IOAction", "showOutwardSummary", "Exited" + Runtime.getRuntime().freeMemory());
        return mapping.findForward("success");
    }

    public ActionForward documentDetailsInward(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "documentDetailsInward", "Entered" + Runtime.getRuntime().freeMemory());
        String id = request.getParameter("outwardID");
        IOProcessor ioprocessor = new IOProcessor();
        HttpSession session = request.getSession(false);
        Outward outward = null;
        if(id != null && !id.equals(""))
        {
            session.setAttribute("INWARDID", id);
            outward = ioprocessor.getOutwardDetail(id);
        } else
        {
            String sameId = (String)session.getAttribute("INWARDID");
            outward = ioprocessor.getOutwardDetail(sameId);
        }
        DynaActionForm dynaForm = (DynaActionForm)form;
        outward.setDateOfDoc(outward.getDocumentSentDate());
        BeanUtils.copyProperties(dynaForm, outward);
        outward = null;
        ioprocessor = null;
        Log.log(4, "IOAction", "documentDetailsInward", "Exited" + Runtime.getRuntime().freeMemory());
        return mapping.findForward("success");
    }

    public ActionForward documentDetailsInward1(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "documentDetailsInward1", "Entered" + Runtime.getRuntime().freeMemory());
        String id = request.getParameter("outwardID");
        IOProcessor ioprocessor = new IOProcessor();
        HttpSession session = request.getSession(false);
        Outward outward = null;
        if(id != null && !id.equals(""))
        {
            session.setAttribute("INWARDID", id);
            outward = ioprocessor.getOutwardDetail(id);
        } else
        {
            String sameId = (String)session.getAttribute("INWARDID");
            outward = ioprocessor.getOutwardDetail(sameId);
        }
        DynaActionForm dynaForm = (DynaActionForm)form;
        outward.setDateOfDoc(outward.getDocumentSentDate());
        BeanUtils.copyProperties(dynaForm, outward);
        outward = null;
        ioprocessor = null;
        Log.log(4, "IOAction", "documentDetailsInward1", "Exited" + Runtime.getRuntime().freeMemory());
        return mapping.findForward("success");
    }

    public ActionForward documentDetailsOutward(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "documentDetailsOutward", "Entered");
        String id = request.getParameter("inwardID");
        IOProcessor ioprocessor = new IOProcessor();
        HttpSession session = request.getSession(false);
        Inward inward = null;
        if(id != null && !id.equals(""))
        {
            session.setAttribute("INWARDID", id);
            inward = ioprocessor.getInwardDetail(id);
        } else
        {
            String sameId = (String)session.getAttribute("INWARDID");
            inward = ioprocessor.getInwardDetail(sameId);
        }
        DynaActionForm dynaForm = (DynaActionForm)form;
        inward.setDateOfDoc(inward.getDateOfDocument());
        BeanUtils.copyProperties(dynaForm, inward);
        inward = null;
        ioprocessor = null;
        Log.log(4, "IOAction", "documentDetailsOutward", "Exited");
        return mapping.findForward("success");
    }

    public ActionForward documentDetailsOutward1(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "documentDetailsOutward1", "Entered" + Runtime.getRuntime().freeMemory());
        String id = request.getParameter("inwardID");
        IOProcessor ioprocessor = new IOProcessor();
        HttpSession session = request.getSession(false);
        Inward inward = null;
        if(id != null && !id.equals(""))
        {
            session.setAttribute("INWARDID", id);
            inward = ioprocessor.getInwardDetail(id);
        } else
        {
            String sameId = (String)session.getAttribute("INWARDID");
            inward = ioprocessor.getInwardDetail(sameId);
        }
        DynaActionForm dynaForm = (DynaActionForm)form;
        inward.setDateOfDoc(inward.getDateOfDocument());
        BeanUtils.copyProperties(dynaForm, inward);
        inward = null;
        ioprocessor = null;
        Log.log(4, "IOAction", "documentDetailsOutward1", "Exited " + Runtime.getRuntime().freeMemory());
        return mapping.findForward("success");
    }

    public ActionForward getDistricts(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "AdministrationAction", "getDistricts", "Entered");
        Administrator admin = new Administrator();
        DynaActionForm dynaActionForm = (DynaActionForm)form;
        String state = (String)dynaActionForm.get("stateName");
        System.out.println("state:" + state);
        ArrayList districtList = admin.getAllDistricts(state);
        dynaActionForm.set("districtList", districtList);
        request.setAttribute("district", "1");
        PrintWriter out = response.getWriter();
        String test = makeOutputString(districtList);
        out.print(test);
        admin = null;
        districtList = null;
        Log.log(4, "AdministrationAction", "getDistricts", "Exited");
        return null;
    }

    public ActionForward getUserNamesForSection(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "AdministrationAction", "getUserNamesForSection", "Entered");
        Administrator admin = new Administrator();
        DynaActionForm dynaActionForm = (DynaActionForm)form;
        String section = (String)dynaActionForm.get("section");
        ArrayList getUserNamesList = getUserNamesForSection(section);
        dynaActionForm.set("getUserNames", getUserNamesList);
        request.setAttribute("district", "1");
        PrintWriter out = response.getWriter();
        String test = makeOutputString(getUserNamesList);
        out.print(test);
        admin = null;
        getUserNamesList = null;
        Log.log(4, "AdministrationAction", "getDistricts", "Exited");
        return null;
    }

    public String makeOutputString(ArrayList districtList)
    {
        String outputString = "Select;Select";
        for(int i = 0; i < districtList.size(); i++)
            outputString = outputString + "||" + districtList.get(i) + ";" + districtList.get(i);

        return outputString;
    }

    public ActionForward documentDetailsSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        Log.log(4, "IOAction", "documentDetailsSearch", "Entered");
        DynaActionForm dynaForm = (DynaActionForm)form;
        dynaForm.initialize(mapping);
        String id = request.getParameter("id");
        String fileName = null;
        HttpSession session = request.getSession(false);
        KnowledgeManager knowledgeManager = new KnowledgeManager();
        Document documentInfo = null;
        if(id != null && !id.equals(""))
        {
            session.setAttribute("INWARDID", id);
            documentInfo = knowledgeManager.getDocumentDetails(id);
        } else
        {
            String sameId = (String)session.getAttribute("INWARDID");
            documentInfo = knowledgeManager.getDocumentDetails(sameId);
        }
        IOProcessor ioprocessor = new IOProcessor();
        fileName = ioprocessor.getFile(id);
        if(fileName == null || fileName.equals(""))
        {
            dynaForm.set("search", documentInfo);
            documentInfo = null;
            knowledgeManager = null;
            Log.log(4, "IOAction", "documentDetailsSearch", "Exited");
            return mapping.findForward("success");
        }
        int length = fileName.length();
        int index2 = fileName.lastIndexOf("/");
        int i = index2 + 1;
        String file = fileName.substring(i, length);
        String contextPath1 = request.getSession(false).getServletContext().getRealPath("");
        String contextPath = PropertyLoader.changeToOSpath(contextPath1);
        String filePath = contextPath + File.separator + "Download" + File.separator + file;
        if(filePath != null)
            filePath = PropertyLoader.changeToOSpath(filePath);
        String formattedToOSPath = request.getContextPath() + File.separator + "Download" + File.separator + file;
        session.setAttribute("file", formattedToOSPath);
        File inputFile = new File(fileName);
        File outputFile = new File(filePath);
        if(outputFile.exists())
            outputFile.createNewFile();
        FileInputStream fis = new FileInputStream(inputFile);
        FileOutputStream fos = new FileOutputStream(outputFile);
        byte temp[] = null;
        for(int n = -1; (n = fis.available()) > 0;)
        {
            temp = new byte[n];
            fis.read(temp);
            fos.write(temp);
        }

        dynaForm.set("search", documentInfo);
        documentInfo = null;
        knowledgeManager = null;
        Log.log(4, "IOAction", "documentDetailsSearch", "Exited");
        return mapping.findForward("success");
    }

    public ActionForward updateRealDate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        DynaActionForm dynaForm = (DynaActionForm)form;
        dynaForm.initialize(mapping);
        User user = getUserInformation(request);
        String userId = user.getUserId();
        return mapping.findForward("success");
    }

    public ActionForward afterUpdateRealDate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        DynaActionForm dynaForm = (DynaActionForm)form;
        String memberID = (String)dynaForm.get("enterBid");
        Date fromdate = (Date)dynaForm.get("dateOfTheDocument36");
        Date todate = (Date)dynaForm.get("dateOfTheDocument34");
        Connection connection = DBConnection.getConnection();
        CallableStatement statement = null;
        try
        {
            statement = connection.prepareCall("{ ? = call Funcupdrealisationdt(?,?,?,?)}");
            statement.registerOutParameter(1, 4);
            statement.setString(2, memberID);
            statement.setDate(3, new java.sql.Date(fromdate.getTime()));
            statement.setDate(4, new java.sql.Date(todate.getTime()));
            statement.registerOutParameter(5, 12);
            statement.execute();
            statement.close();
            statement = null;
            int status = statement.getInt(1);
            String error = statement.getString(5);
            if(status == 1)
            {
                Log.log(2, "IODAO", "afterUpdateRealDate", "afterUpdateRealDate Error code is :" + error);
                statement.close();
                statement = null;
                throw new Exception(error);
            }
        }
        catch(Exception exception)
        {
            throw new DatabaseException(exception.getMessage());
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        request.setAttribute("message", "<b>Realisation Date Modified Successfully");
        return mapping.findForward("success");
    }
    
    

            //added by upchar@pathn 0n 22-05-2013

            /**
             * @param inwardId
             * @throws DatabaseException
             */
            public void updateInwardCorrSeq(String inwardId) throws DatabaseException
             {
                                Connection connection = DBConnection.getConnection();
                            //     int inwardSeq=Integer.parseInt(inwardId);
                                 
                                 Calendar calendar = Calendar.getInstance() ;
                                 java.util.Date  fromDate = null ;
                           //      java.sql.Date sqlFromDate=null;
                                 int year = calendar.get(Calendar.YEAR)  ;
                                 int month = calendar.get(Calendar.MONTH) ;
                                 int day = calendar.get(Calendar.DATE) ;
                                             calendar.set(Calendar.DATE,1);
                                 calendar.set(Calendar.MONTH, Calendar.APRIL) ;
                                  if(month >=0 && month <=2){
                                   year=year-1;
                                  } 
                                 calendar.set(Calendar.YEAR,year);
                                 fromDate = calendar.getTime();
                              //   sqlFromDate = Date.valueOf(fromDate.toString());
                                 int count=0;
                                 try{
                                 String query = "UPDATE inward_seq_corr SET INWARDNUMBER=? WHERE INWARDYEAR=?";   
                                 PreparedStatement pstmt=connection.prepareStatement(query);   
                                 pstmt.setString(1,inwardId);  
                                 pstmt.setDate(2,new java.sql.Date(fromDate.getTime()));
                                count =  pstmt.executeUpdate();
                             //    System.out.println("Query:"+query+"count:"+count);
                                 if(count==0){
                                  connection.commit();
                                 }else{
                                 pstmt.close();
                                 pstmt=null;    
                                 connection.rollback();
                                 }
                                 }//try close
                                 catch(Exception exception) {
                                     try
                                             {
                                               connection.rollback();
                                       }
                                       catch (SQLException ignore)
                                                         {
                                                               }
                                 Log.logException(exception);
                                throw new DatabaseException(exception.getMessage());
                                 
                                 }//ctach close
                                 finally {
                                 DBConnection.freeConnection(connection);
                                 }//finally close           
             }
             
            //added by upchar@path on 21-05-2013

            /**
             * @param mapping
             * @param form
             * @param request
             * @param response
             * @return
             * @throws Exception
             */
            public ActionForward inwardCorrespondence(ActionMapping mapping,ActionForm form,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) throws Exception{
                                                       
                               //         System.out.println("--entered in inwardCorrespondance-----");
                                                       
                                        DynaActionForm dynaForm=(DynaActionForm)form;
                                        User user=getUserInformation(request);
                                          String userId=user.getUserId();
                                        String bankId = user.getBankId();
                                        String zoneId = user.getZoneId();
                                        String branchId = user.getBranchId();
                                        String memberId = bankId.concat(zoneId).concat(branchId);
                                        
                                        ArrayList members=(ArrayList)getMemberList();
                                        dynaForm.set("getAllMembers",members); 
                                        
                                        ArrayList userNames = (ArrayList)getUserNames(memberId);
                                        System.out.println(userNames);
                                       java.util.Iterator i=userNames.iterator();
                                       while(i.hasNext())
                                       {
                                    	   System.out.println(i.next());
                                       }
                                        
                                        
                                        dynaForm.set("getUserNames",userNames);
                                        
                                        members=null;
                                        userNames=null;
                                        
                                        dynaForm.set("bankNames","");
                                        dynaForm.set("section","");
                                        dynaForm.set("assignedTo","");              
            
            
                             //           System.out.println("---exited from inwardCorrespondance-----");
                                        return mapping.findForward("success");
            }
            
            //added by upchar@path on 22-05-2013

            /**
             * @param mapping
             * @param form
             * @param request
             * @param response
             * @return
             * @throws Exception
             */
            public ActionForward addCorrespondence(ActionMapping mapping,ActionForm form,
                                                   HttpServletRequest request ,
                                                   HttpServletResponse response) throws Exception{
            
                              //     System.out.println("---enterd in addCorrespondence-----");
                                   
                DynaActionForm dynaForm = (DynaActionForm)form;
                
                        IOFormBean ioForm = new IOFormBean();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                        java.util.Date systemDate = new java.util.Date();
                        IODAO ioDAO = new IODAO();
                        String sysDate = dateFormat.format(systemDate);
                        User user = getUserInformation(request);
                        String userId = user.getUserId();
                        String inwardCorrId = String.valueOf(ioDAO.getInwardIdForCorr());//finish
                        String inwardDts[] = request.getParameterValues("inwardDts");
                        String bankName = request.getParameter("bankNames");
                        String places = request.getParameter("places");
                        String referenceIds = request.getParameter("referenceIds");
                        String ltrDts = request.getParameter("ltrDts");
                        java.util.Date ltrDtsNew=null;
                        
                        if(ltrDts == null || ltrDts.equals(""))
                        {
                            ltrDts = "";
                        }
                        else
                        {
                           // ltrDts = dateFormat.format(java.sql.Date.valueOf(DateHelper.stringToSQLdate(ltrDts)));
                            
                            try{
                            
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
                            ltrDtsNew = formatter.parse(ltrDts);
                            
                            }catch(Exception e){
                            e.printStackTrace(); 
                            } 
                        }
                            
                        String drawnonBank[] = request.getParameterValues("drawnonBank");
                        String subject = request.getParameter("subjects");
                        String sourceIds[] = request.getParameterValues("sourceIds");
                        String instrumentDts[] = request.getParameterValues("instrumentDts");
                        String instrumentAmt[] = request.getParameterValues("instrumentAmt");
                        String section[] = request.getParameterValues("section");
                        String assignedTo[] = request.getParameterValues("assignedTo");
                        int rowCount = 10;
                        try
                        {
                            rowCount = Integer.parseInt(request.getParameter("rowcount"));
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                        Connection connection = DBConnection.getConnection();
                        int count = 0;
                        CallableStatement stmt = null;
                        try
                        {
                            Statement str = connection.createStatement();
                            for(int i = 0; i < rowCount; i++)
                            {
                                String instrumentDt = instrumentDts[i];
                                
                                java.util.Date instrumentDtNew=null;
                                if(instrumentDt == null || instrumentDt.equals(""))
                                {
                                    instrumentDt = "";
                                }
                                else
                                {
                                    //instrumentDt = dateFormat.format(java.sql.Date.valueOf(DateHelper.stringToSQLdate(instrumentDts[i])));
                                    try{
                                  
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
                                instrumentDtNew = formatter.parse(instrumentDt);
                                   
                                    }catch(Exception e){
                                    e.printStackTrace(); 
                                    } 
                                }
                             
                       //         stmt = connection.prepareCall("{?=call packgetinwardcorrdet.funcgetinwardcorrdet(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
                             
                                if(instrumentDt == null || instrumentDt.equals(""))
                                {
                                   String query = "INSERT INTO INWARD_CORR ( INW_ID, INW_DT,BANK_NAME,DRAWN_ON_BANK, PLACE, SUBJECT, INSTRUMENT_NO, INSTRUMENT_DT,INSTRUMENT_AMT, LTR_REF_NO, LTR_DT,INWARD_SEC,INWARD_CREATED_BY,INWARD_CREATED_DT,INWARD_FLAG,INW_SEQ,ASSIGNED_USER) VALUES     ('" + inwardCorrId + "','" + dateFormat.format(new java.sql.Date(systemDate.getTime())) + "' ,'" + bankName + "' ,'" + drawnonBank[i] + "', '" + places + "' , '" + subject + "' , '" + sourceIds[i] + "' , " + "  '" + instrumentDt + "' , " + " '" + instrumentAmt[i] + "' , '" + referenceIds + "' , '" + dateFormat.format(new java.sql.Date(ltrDtsNew.getTime())) + "', '" + section[i] + "','" + userId + "',SYSDATE,'I','" + i + "','" + assignedTo[i] + "')";
                                   str.executeUpdate(query);  
           
                                   
                                }
                             else
                             {
                                    String query = "INSERT INTO INWARD_CORR ( INW_ID, INW_DT,BANK_NAME,DRAWN_ON_BANK, PLACE, SUBJECT, INSTRUMENT_NO, INSTRUMENT_DT,INSTRUMENT_AMT, LTR_REF_NO, LTR_DT,INWARD_SEC,INWARD_CREATED_BY,INWARD_CREATED_DT,INWARD_FLAG,INW_SEQ,ASSIGNED_USER) VALUES ('" + inwardCorrId + "','" + dateFormat.format(new java.sql.Date(systemDate.getTime())) + "' ,'" + bankName + "' ,'" + drawnonBank[i] + "', '" + places + "' , '" + subject + "' , '" + sourceIds[i] + "' , " + "  '" + dateFormat.format(new java.sql.Date(instrumentDtNew.getTime())) +"' , " + " '" + instrumentAmt[i] + "' , '" + referenceIds + "' , '" + dateFormat.format(new java.sql.Date(ltrDtsNew.getTime())) + "', '" + section[i] + "','" + userId + "',SYSDATE,'I','" + i + "','" + assignedTo[i] + "')";       
                                    str.executeUpdate(query);
                             }
                           //  stmt.execute();
                             
                            }

                            updateInwardCorrSeq(inwardCorrId);//finish
                            
                      //      stmt.close();
                      //      stmt = null;
                            str.close();
                            str=null;
                        }
                        catch(Exception exception)
                        {
                            Log.logException(exception);
                            throw new DatabaseException(exception.getMessage());
                        }
                        finally
                        {
                            DBConnection.freeConnection(connection);
                        }
                        request.setAttribute("message", "<b>Inward Correspondence Details are Successfully Saved and Inward Correspondence Number Is : " + inwardCorrId);
                                   
                                //   System.out.println("----exited in addCorrespondence-----");
                                   return mapping.findForward("success");
            } 
              
              
              //added by upchar@path on 23-05-2013

            /**
             * @param mapping
             * @param form
             * @param request
             * @param response
             * @return
             * @throws Exception
             */
            public ActionForward updateCorrespondenceDetails(ActionMapping mapping,ActionForm form,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws Exception
               {
               
                     DynaActionForm dynaForm=(DynaActionForm)form;
                     dynaForm.initialize(mapping);
                     User user=getUserInformation(request);
                     String userId=user.getUserId();
                     return mapping.findForward("success");
               }


            /**
             * @param mapping
             * @param form
             * @param request
             * @param response
             * @return
             * @throws Exception
             */
             public ActionForward displayCorrespondenceDetails(ActionMapping mapping,ActionForm form,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) throws Exception
             {
             
                
                DynaActionForm dynaForm=(DynaActionForm)form;
                String inwardId = (String) dynaForm.get("inwardId");
                String instrumentNo =(String) dynaForm.get("sourceIds");    
                IOFormBean ioFormBean = new IOFormBean();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                ArrayList inwardSummary = null;
                
                User user=getUserInformation(request);
                String userId=user.getUserId();
                String bankId = user.getBankId();
                String zoneId = user.getZoneId();
                String branchId = user.getBranchId();
                String memberId = bankId.concat(zoneId).concat(branchId);
                
                
                java.util.Date date  = new java.util.Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                String systemDate = dateFormat.format(date);
                  
                if(inwardId==null||inwardId.equals("")){
                     throw new NoDataException("Please Enter Valid Inward Id");
                }else{   
               //   System.out.println("Input Inward Id:"+inwardId);   
                  ioFormBean = getInwardCorrDetails(inwardId,instrumentNo);///new method
                  
                  System.out.println("ioFormBean.getInwardCorrDts()..."+ioFormBean.getInwardDts());
                  String dt = null;
                  String dt2 = null;
                  if(ioFormBean.getInwardDts()!= null){
                     dt = dateFormat.format(ioFormBean.getInwardDts());
                  }
                   if(ioFormBean.getLtrDts()!= null){
                      dt2 = dateFormat.format(ioFormBean.getLtrDts());
                   }
                  dynaForm.set("instrumentNo",instrumentNo);
                  dynaForm.set("inwardDts",dt);
                  dynaForm.set("bankNames",ioFormBean.getBankNames());
                  dynaForm.set("drawnonBank",ioFormBean.getDrawnOnBank());
                  dynaForm.set("places",ioFormBean.getPlaces());
                  dynaForm.set("subjects",ioFormBean.getSubjects());
                  dynaForm.set("referenceIds",ioFormBean.getReferenceIds());
                  dynaForm.set("ltrDt",dt2);
                  dynaForm.set("sourceIds",ioFormBean.getSourceIds());
                  if(ioFormBean.getInstrumentDts()==null || ioFormBean.getInstrumentDts().equals("")){
                    dynaForm.set("instrumentDt","");
                  } else {
                    dynaForm.set("instrumentDt",dateFormat.format(ioFormBean.getInstrumentDts()));
                  }
                  
                  dynaForm.set("instrumentAmt",new Integer(ioFormBean.getInstrumentAmt()));
                  dynaForm.set("section",ioFormBean.getSection());
                  
                   if(ioFormBean.getOutwardDt()==null || ioFormBean.getOutwardDt().equals("")){
                    dynaForm.set("outwardDt","");
                  } else {
                    dynaForm.set("outwardDt",dateFormat.format(ioFormBean.getOutwardDt()));
                  }
                  dynaForm.set("outwardId",ioFormBean.getOutwardId());
                  
                  ArrayList userNames = (ArrayList)getUserNames(memberId);
                  
                  
                  
                  dynaForm.set("assignedTo",ioFormBean.getAssignedTo());
                  dynaForm.set("reasons",ioFormBean.getReasons());
             //      if(userNames.contains(ioFormBean.getAssignedTo()))
             //      {
             //        userNames.remove(ioFormBean.getAssignedTo());
             //      }
                  dynaForm.set("getUserNames",userNames);
                  
              //    System.out.println("ioFormBean.getOutwardId():"+ioFormBean.getOutwardId());
              //    System.out.println("ioFormBean.getOutwardDt():"+ioFormBean.getOutwardDt());
              //    dynaForm.set("outwardDt",systemDate);
                  ioFormBean = null;
                  return mapping.findForward("displayInwardCorrDetails");        
                  
               }
                 
             }


            /**
             * @param mapping
             * @param form
             * @param request
             * @param response
             * @return
             * @throws Exception
             */
            public ActionForward afterUpdateCorrespondenceDetails(ActionMapping mapping,ActionForm form,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws Exception
            {
            
            DynaActionForm dynaForm=(DynaActionForm)form;
            IODAO ioDAO = new IODAO();
            String inwardId = (String) dynaForm.get("inwardId");
            // System.out.println("Inward Id:"+inwardId);
            String inwardDt  =(String) dynaForm.get("inwardDts");
            //System.out.println("Inward Dt:"+inwardDt);
            
            String oldInstrumentNo =(String) dynaForm.get("instrumentNo");    
            //  System.out.println("oldInstrumentNo:"+oldInstrumentNo);
            String instrumentNo =(String) dynaForm.get("sourceIds");   
            //  System.out.println("instrumentNo:"+instrumentNo);
            String bankNames = (String) dynaForm.get("bankNames");   
            //  System.out.println("Bank Name:"+bankNames);
            String drawnonBank = (String) dynaForm.get("drawnonBank");   
            //  System.out.println("Drawn on Bank Name:"+drawnonBank);
            String places = (String) dynaForm.get("places");   
            //  System.out.println("Place:"+places);
            String subjects = (String) dynaForm.get("subjects");   
            //  System.out.println("Subject:"+subjects);
            String referenceIds = (String) dynaForm.get("referenceIds");   
            //  System.out.println("referenceIds:"+referenceIds);
            
            String ltrDt = (String) dynaForm.get("ltrDt");   
            //  System.out.println("ltrDt:"+ltrDt);
            String instrumentDt = (String) dynaForm.get("instrumentDt");   
            //  System.out.println("instrumentDt:"+instrumentDt);
            
            
            int instrumentAmt = ((Integer)dynaForm.get("instrumentAmt")).intValue();   
            //  System.out.println("instrumentAmt:"+instrumentAmt);
            
            String section = (String) dynaForm.get("section");   
            // System.out.println("section:"+section);
            String outwardId = (String) dynaForm.get("outwardId");
            // System.out.println("outwardId:"+outwardId);
            String outwardDt = (String) dynaForm.get("outwardDt");
            // System.out.println("outwardDt:"+outwardDt);
            
            User user=getUserInformation(request);
            String userId=user.getUserId();
            
              
            String assignedTo = (String) dynaForm.get("assignedTo");
            String reasons = (String) dynaForm.get("reasons");
            
            
            
            
            
            ioDAO.afterUpdateInwardCorrDetails(inwardId,oldInstrumentNo,instrumentNo,bankNames, drawnonBank, places,subjects, referenceIds, ltrDt,instrumentDt,instrumentAmt,section,userId,outwardId,outwardDt,assignedTo,reasons,inwardDt);
            request.setAttribute("message","<b>Inward Correspondence Details Modified Successfully");
            return mapping.findForward("success");
            }
            
            
            private IOFormBean getInwardCorrDetails(String inwardNo,String instrumentNo)throws DatabaseException
                   {
             
                   //       ArrayList inwardDetails=new ArrayList();
              //    PreparedStatement pStmt = null;
                ResultSet resultSet       = null;
               IOFormBean ioFormBean = new IOFormBean();
                Connection connection     = DBConnection.getConnection();
                try
                   { 
                 CallableStatement callable=connection.prepareCall("{?=call PackGetInwardCorrDet.FuncGetInwardCorrDet(?,?,?,?)}");
                   callable.registerOutParameter(1,Types.INTEGER);
                 callable.setString(2,inwardNo);
                 if(instrumentNo==null||instrumentNo.equalsIgnoreCase("")){
                  callable.setNull(3,java.sql.Types.VARCHAR);
                 }else {
                     callable.setString(3,instrumentNo);
                 }
                
                   callable.registerOutParameter(4,Constants.CURSOR);
                           callable.registerOutParameter(5,Types.VARCHAR);
                                   callable.executeUpdate();

                                   int errorCode=callable.getInt(1);
                                   String error=callable.getString(5);
                 resultSet = (ResultSet)callable.getObject(4) ;
                 if(errorCode==Constants.FUNCTION_FAILURE)
                                   {
                   callable.close();
                                     callable=null;
                   System.out.println("Error:"+error);
                                           throw new DatabaseException(error);
                                   }
                
                 if(resultSet.next()){
                   
                    ioFormBean.setInwardDts(resultSet.getDate(2));//---------------------inward date
                    ioFormBean.setBankNames(resultSet.getString(3));//-------------------bank names
                    ioFormBean.setDrawnOnBank(resultSet.getString(4));//-----------------drawn bamk name
                    ioFormBean.setPlaces(resultSet.getString(5));//----------------------place
                    ioFormBean.setSubjects(resultSet.getString(6));//--------------------subject
                    ioFormBean.setReferenceIds(resultSet.getString(7));//----------------reference id
                    ioFormBean.setLtrDts(resultSet.getDate(8));//------------------------ltr date         
                    ioFormBean.setSourceIds(resultSet.getString(9));//-------------------source id
                    ioFormBean.setInstrumentDts(resultSet.getDate(10));//----------------instrument date
                    ioFormBean.setInstrumentAmt(resultSet.getInt(11));//-----------------instrument amount
                    ioFormBean.setSection(resultSet.getString(12));//--------------------section
                    ioFormBean.setOutwardId(resultSet.getString(13));//------------------outward id
                    ioFormBean.setOutwardDt(resultSet.getDate(14));//--------------------outward date
                    ioFormBean.setAssignedTo(resultSet.getString(15));//-----------------assign to
                    ioFormBean.setReasons(resultSet.getString(16));//--------------------reason
                         
                  }
                  resultSet.close();
                                    resultSet=null;
                      }
                            catch(SQLException e)
                           {
                                   Log.log(Log.ERROR,"IOAction","getMemberList",e.getMessage());
                                   Log.logException(e);

                                   throw new DatabaseException("Failed to fetch Inward Source Names");
                           }
                           finally
                           {
                                   DBConnection.freeConnection(connection);
                           }
                           return ioFormBean;

                   }
     
     public IOAction()
    {
    }
}