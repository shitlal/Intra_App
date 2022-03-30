// Decompiled Using: FrontEnd Plus v2.03 and the JAD Engine
// Available From: http://www.reflections.ath.cx
// Decompiler options: packimports(3) 
// Source File Name:   IODAO.java

package com.cgtsi.inwardoutward;

import com.cgtsi.actionform.IOFormBean;
import com.cgtsi.common.Constants;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.util.DBConnection;
import com.cgtsi.util.DateHelper;
import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

// Referenced classes of package com.cgtsi.inwardoutward:
//            Outward, Inward

public class IODAO
{

    public IODAO()
    {
    }

    public String addInward(Inward inward)
        throws Exception
    {
        Log.log(4, "IODAO", "addInward", "Entered");
        Connection connection = DBConnection.getConnection(false);
        int status = -1;
        String inwardId = null;
        String knowledgeId = null;
        Date sqlDate = null;
        java.util.Date utilDate = null;
        String errorCode = null;
        try
        {
            CallableStatement inwardIdStmt = connection.prepareCall("{?=call  funcGenInwardId(?,?)}");
            inwardIdStmt.registerOutParameter(1, 4);
            inwardIdStmt.registerOutParameter(2, 12);
            inwardIdStmt.registerOutParameter(3, 12);
            inwardIdStmt.execute();
            int valueReturned = inwardIdStmt.getInt(1);
            if(valueReturned == 1)
            {
                String error = inwardIdStmt.getString(3);
                Log.log(2, "IODAO", "addInward", "SP returns a 1. Error code is :" + error);
                inwardIdStmt.close();
                inwardIdStmt = null;
            } else
            if(valueReturned == 0)
            {
                String id = inwardIdStmt.getString(2);
                inwardIdStmt.close();
                inwardIdStmt = null;
                if(inward != null)
                {
                    CallableStatement addInwardStmt = connection.prepareCall("{?=call funcInsertInward(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
                    addInwardStmt.registerOutParameter(1, 4);
                    addInwardStmt.setString(2, id);
                    String sourceType = inward.getSourceType();
                    addInwardStmt.setString(3, sourceType);
                    String sourceId = inward.getSourceId();
                    addInwardStmt.setString(4, sourceId);
                    String sourceName = inward.getSourceName();
                    addInwardStmt.setString(5, sourceName);
                    String sourceRef = inward.getSourceRef();
                    addInwardStmt.setString(6, sourceRef);
                    String documentType = inward.getDocumentType();
                    addInwardStmt.setString(7, documentType);
                    String modeOfReceipt = inward.getModeOfReceipt();
                    addInwardStmt.setString(8, modeOfReceipt);
                    utilDate = inward.getDateOfDocument();
                    sqlDate = new Date(utilDate.getTime());
                    addInwardStmt.setDate(9, sqlDate);
                    String Language = inward.getLanguage();
                    addInwardStmt.setString(10, Language);
                    String Subject = inward.getSubject();
                    addInwardStmt.setString(11, Subject);
                    int index1 = id.indexOf("/");
                    int index2 = id.lastIndexOf("/");
                    String part1 = id.substring(0, index1);
                    String part2 = id.substring(++index1, index2);
                    String part3 = id.substring(++index2);
                    String newInwardId = part1 + part2 + part3;
                    String filePath = inward.getFilePath();
                    if(filePath != null && !filePath.equals(""))
                    {
                        int index = filePath.lastIndexOf(".");
                        String name = filePath.substring(0, index);
                        String type = filePath.substring(index);
                        String newFilePath = name + newInwardId + type;
                        addInwardStmt.setString(12, newFilePath);
                    } else
                    {
                        addInwardStmt.setString(12, null);
                    }
                    String mappedOutwardID = inward.getMappedOutwardID();
                    addInwardStmt.setString(13, mappedOutwardID);
                    String Remarks = inward.getRemarks();
                    addInwardStmt.setString(14, Remarks);
                    String UserId = inward.getProcessedBy();
                    addInwardStmt.setString(15, UserId);
                    addInwardStmt.registerOutParameter(16, 12);
                    addInwardStmt.registerOutParameter(17, 12);
                    addInwardStmt.registerOutParameter(18, 12);
                    addInwardStmt.execute();
                    status = addInwardStmt.getInt(1);
                    if(status == 1)
                    {
                        connection.rollback();
                        String errorCode1 = addInwardStmt.getString(18);
                        Log.log(2, "IODAO", "addInward", "SP returns a 1. Error code is :" + errorCode1);
                        addInwardStmt.close();
                    } else
                    if(status == 0)
                    {
                        inwardId = addInwardStmt.getString(16);
                        knowledgeId = addInwardStmt.getString(17);
                        addInwardStmt.close();
                        addInwardStmt = null;
                    }
                }
            }
            connection.commit();
        }
        catch(SQLException exception)
        {
            try
            {
                connection.rollback();
            }
            catch(SQLException ignore) { }
            throw new DatabaseException(exception.getMessage());
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        Log.log(4, "IODAO", "addInward", "Exited");
        return inwardId;
    }

    public int getInwardId()
        throws Exception
    {
        Log.log(4, "IODAO", "getInwardId", "Entered");
        Connection connection = DBConnection.getConnection(false);
        int inwardId = 0;
        try
        {
            CallableStatement inwardIdStmt = connection.prepareCall("{?=call  funcGetInwardId(?,?)}");
            inwardIdStmt.registerOutParameter(1, 4);
            inwardIdStmt.registerOutParameter(2, 4);
            inwardIdStmt.registerOutParameter(3, 12);
            inwardIdStmt.execute();
            int valueReturned = inwardIdStmt.getInt(1);
            if(valueReturned == 1)
            {
                String error = inwardIdStmt.getString(3);
                System.out.println("error:" + error);
                Log.log(2, "IODAO", "getInwardId", "SP returns a 1. Error code is :" + error);
                inwardIdStmt.close();
                inwardIdStmt = null;
            } else
            if(valueReturned == 0)
                inwardId = inwardIdStmt.getInt(2);
        }
        catch(SQLException exception)
        {
            try
            {
                connection.rollback();
            }
            catch(SQLException ignore) { }
            throw new DatabaseException(exception.getMessage());
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        Log.log(4, "IODAO", "getInwardId", "Exited");
        return inwardId;
    }

    public void afterAddInwardSorceName(String sourceName, String createdBy)
        throws DatabaseException
    {
        Log.log(4, "IODAO", "afterAddInwardSorceName", "Entered");
        Connection connection = DBConnection.getConnection(false);
        try
        {
            CallableStatement callableStmt = connection.prepareCall("{?=call  FuncInsInwardParty(?,?,?)}");
            callableStmt.registerOutParameter(1, 4);
            callableStmt.setString(2, sourceName);
            callableStmt.setString(3, createdBy);
            callableStmt.registerOutParameter(4, 12);
            callableStmt.execute();
            int valueReturned = callableStmt.getInt(1);
            if(valueReturned == 1)
            {
                String error = callableStmt.getString(4);
                System.out.println("error:" + error);
                Log.log(2, "IODAO", "afterAddInwardSorceName", "SP returns a 1. Error code is :" + error);
                callableStmt.close();
                callableStmt = null;
            } else
            if(valueReturned == 0)
            {
                callableStmt.close();
                callableStmt = null;
            }
            connection.commit();
        }
        catch(SQLException exception)
        {
            try
            {
                connection.rollback();
            }
            catch(SQLException ignore) { }
            throw new DatabaseException(exception.getMessage());
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        Log.log(4, "IODAO", "afterAddInwardSorceName", "Exited");
    }

    public String addOutward(Outward outward)
        throws DatabaseException
    {
        Log.log(4, "IODAO", "addOutward", "Entered");
        Connection connection = DBConnection.getConnection(false);
        Date sqlDate = null;
        java.util.Date utilDate = null;
        String outwardId = null;
        String knowledgeId1 = null;
        String error = null;
        int status = 0;
        try
        {
            CallableStatement outwardIdStmt = connection.prepareCall("{?=call  funcGenOutwardId(?,?)}");
            outwardIdStmt.registerOutParameter(1, 4);
            outwardIdStmt.registerOutParameter(2, 12);
            outwardIdStmt.registerOutParameter(3, 12);
            outwardIdStmt.execute();
            int valueReturned = outwardIdStmt.getInt(1);
            String errorCode = outwardIdStmt.getString(3);
            if(valueReturned == 1)
            {
                Log.log(2, "IODAO", "addOutward", "SP returns a 1. Error code is :" + errorCode);
                outwardIdStmt.close();
                outwardIdStmt = null;
            } else
            if(valueReturned == 0)
            {
                String id = outwardIdStmt.getString(2);
                outwardIdStmt.close();
                outwardIdStmt = null;
                if(outward != null)
                {
                    CallableStatement addOutwardStmt = connection.prepareCall("{? = call funcInsertOutward(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
                    addOutwardStmt.registerOutParameter(1, 4);
                    addOutwardStmt.setString(2, id);
                    String destinationType = outward.getDestinationType();
                    addOutwardStmt.setString(3, destinationType);
                    String referenceId = outward.getReferenceId();
                    addOutwardStmt.setString(4, referenceId);
                    String destinationName = outward.getDestinationName();
                    addOutwardStmt.setString(5, destinationName);
                    String destinationRef = outward.getdestinationRef();
                    addOutwardStmt.setString(6, destinationRef);
                    String documentType = outward.getDocumentType();
                    addOutwardStmt.setString(7, documentType);
                    String modeOfDelivery = outward.getModeOfDelivery();
                    addOutwardStmt.setString(8, modeOfDelivery);
                    utilDate = outward.getDocumentSentDate();
                    sqlDate = new Date(utilDate.getTime());
                    addOutwardStmt.setDate(9, sqlDate);
                    String Language = outward.getLanguage();
                    addOutwardStmt.setString(10, Language);
                    String Subject = outward.getSubject();
                    addOutwardStmt.setString(11, Subject);
                    int index1 = id.indexOf("/");
                    int index2 = id.lastIndexOf("/");
                    String part1 = id.substring(0, index1);
                    String part2 = id.substring(++index1, index2);
                    String part3 = id.substring(++index2);
                    String newOutwardId = part1 + part2 + part3;
                    String filePath = outward.getFilePath();
                    if(filePath != null && !filePath.equals(""))
                    {
                        int index = filePath.lastIndexOf(".");
                        String name = filePath.substring(0, index);
                        String type = filePath.substring(index);
                        String newFilePath = name + newOutwardId + type;
                        addOutwardStmt.setString(12, newFilePath);
                    } else
                    {
                        addOutwardStmt.setString(12, null);
                    }
                    String mappedInward = outward.getMappedInward();
                    addOutwardStmt.setString(13, mappedInward);
                    String Remarks = outward.getRemarks();
                    addOutwardStmt.setString(14, Remarks);
                    String userId = outward.getProcessedBy();
                    addOutwardStmt.setString(15, userId);
                    addOutwardStmt.registerOutParameter(16, 12);
                    addOutwardStmt.registerOutParameter(17, 12);
                    addOutwardStmt.registerOutParameter(18, 12);
                    addOutwardStmt.execute();
                    status = addOutwardStmt.getInt(1);
                    error = addOutwardStmt.getString(18);
                    if(status == 1)
                    {
                        connection.commit();
                        Log.log(2, "IODAO", "addOutward", "SP returns a 1. Error code is :" + error);
                        addOutwardStmt.close();
                        addOutwardStmt = null;
                    } else
                    if(status == 0)
                    {
                        outwardId = addOutwardStmt.getString(16);
                        knowledgeId1 = addOutwardStmt.getString(17);
                        addOutwardStmt.close();
                        addOutwardStmt = null;
                    }
                }
            }
            connection.commit();
        }
        catch(SQLException exception)
        {
            try
            {
                connection.rollback();
            }
            catch(SQLException ignore) { }
            throw new DatabaseException(exception.getMessage());
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        Log.log(4, "IODAO", "addOutward", "Exited");
        return outwardId;
    }

    public void insertWorkshopDetails(String workshopDt, String bankName, String agencyName, String targetGroup, String place, String stateName, String districtName, 
            String city, String type, String topic, int participants, String organisation, String name, String designation, 
            String reasons, String loggedUserId, String organisedfor, String nonMliName, String mliNames, String zone, String mliId, 
            String governmentOrgs)
        throws DatabaseException
    {
        Log.log(4, "IODAO", "insertWorkshopDetails", "Entered");
        Connection connection = DBConnection.getConnection();
        CallableStatement workshopDetailsStmt = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        int count = 0;
        PreparedStatement pStmt = null;
        try
        {
            workshopDetailsStmt = connection.prepareCall("{? = call Funcinsertworkshopdetails(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            workshopDetailsStmt.registerOutParameter(1, 4);
            workshopDetailsStmt.setDate(2, Date.valueOf(DateHelper.stringToSQLdate(workshopDt)));
            workshopDetailsStmt.setString(3, bankName);
            workshopDetailsStmt.setString(4, agencyName);
            workshopDetailsStmt.setString(5, targetGroup);
            workshopDetailsStmt.setString(6, place);
            workshopDetailsStmt.setString(7, stateName);
            workshopDetailsStmt.setString(8, districtName);
            workshopDetailsStmt.setString(9, city);
            workshopDetailsStmt.setString(10, type);
            workshopDetailsStmt.setString(11, topic);
            workshopDetailsStmt.setInt(12, participants);
            workshopDetailsStmt.setString(13, organisation);
            workshopDetailsStmt.setString(14, name);
            workshopDetailsStmt.setString(15, designation);
            workshopDetailsStmt.setString(16, reasons);
            workshopDetailsStmt.setString(17, loggedUserId);
            workshopDetailsStmt.setString(18, organisedfor);
            workshopDetailsStmt.setString(19, nonMliName);
            workshopDetailsStmt.setString(20, mliNames);
            workshopDetailsStmt.setString(21, zone);
            workshopDetailsStmt.setString(22, mliId);
            workshopDetailsStmt.setString(23, governmentOrgs);
            workshopDetailsStmt.registerOutParameter(24, 12);
            workshopDetailsStmt.executeQuery();
            int status = workshopDetailsStmt.getInt(1);
            String errorCode = workshopDetailsStmt.getString(24);
            if(status == 1)
            {
                Log.log(2, "IODAO", "insertWorkshopDetails", "SP returns a 1. Error code is :" + errorCode);
                System.out.println("errorCode:" + errorCode);
                workshopDetailsStmt.close();
                workshopDetailsStmt = null;
                throw new DatabaseException(errorCode);
            }
            workshopDetailsStmt.close();
            workshopDetailsStmt = null;
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
        Log.log(4, "IODAO", "insertWorkshopDetails", "Exited");
    }

    public ArrayList getAllOutwardIds()
        throws DatabaseException
    {
        Log.log(4, "IODAO", "getAllOutwardIds", "Entered");
        ResultSet resultSetOutwardIds = null;
        ArrayList ArrayListOfOutwardIds = new ArrayList();
        String errorCode = null;
        Connection connection = DBConnection.getConnection();
        try
        {
            CallableStatement outwardIdsStmt = connection.prepareCall("{? = call packGetAllOutwardIds.funcGetAllOutwardIds(?,?)}");
            outwardIdsStmt.registerOutParameter(1, 4);
            outwardIdsStmt.registerOutParameter(2, -10);
            outwardIdsStmt.registerOutParameter(3, 12);
            outwardIdsStmt.execute();
            int status = outwardIdsStmt.getInt(1);
            errorCode = outwardIdsStmt.getString(3);
            if(status == 1)
            {
                Log.log(2, "IODAO", "getAllOutwardIds", "SP returns a 1. Error code is :" + errorCode);
                outwardIdsStmt.close();
                outwardIdsStmt = null;
            } else
            if(status == 0)
            {
                for(resultSetOutwardIds = (ResultSet)outwardIdsStmt.getObject(2); resultSetOutwardIds.next(); ArrayListOfOutwardIds.add(resultSetOutwardIds.getObject(1)));
                resultSetOutwardIds.close();
                resultSetOutwardIds = null;
                outwardIdsStmt.close();
                outwardIdsStmt = null;
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
        Log.log(4, "IODAO", "getAllOutwardIds", "Exited");
        return ArrayListOfOutwardIds;
    }

    public ArrayList getAllInwardIds()
        throws DatabaseException
    {
        Log.log(4, "IODAO", "getAllInwardIds", "Entered");
        ResultSet resultSetInwardIds = null;
        ArrayList ArrayListOfInwardIds = new ArrayList();
        String errorCode = null;
        Connection connection = DBConnection.getConnection();
        try
        {
            CallableStatement inwardIdsStmt = connection.prepareCall("{? = call packGetAllInwardIds.funcGetAllInwardIds(?,?)}");
            inwardIdsStmt.registerOutParameter(1, 4);
            inwardIdsStmt.registerOutParameter(2, -10);
            inwardIdsStmt.registerOutParameter(3, 12);
            inwardIdsStmt.execute();
            int status = inwardIdsStmt.getInt(1);
            errorCode = inwardIdsStmt.getString(3);
            if(status == 1)
            {
                Log.log(2, "IODAO", "getAllInwardIds", "SP returns a 1. Error code is :" + errorCode);
                inwardIdsStmt.close();
                inwardIdsStmt = null;
            } else
            if(status == 0)
            {
                for(resultSetInwardIds = (ResultSet)inwardIdsStmt.getObject(2); resultSetInwardIds.next(); ArrayListOfInwardIds.add((String)resultSetInwardIds.getObject(1)));
                inwardIdsStmt.close();
                inwardIdsStmt = null;
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
        Log.log(4, "IODAO", "getAllInwardIds", "Exited");
        return ArrayListOfInwardIds;
    }

    public Inward getInwardDetail(String sInwardId)
        throws DatabaseException
    {
        Log.log(4, "IODAO", "getInwardDetail", "Entered");
        Connection connection = DBConnection.getConnection();
        Inward inward = null;
        try
        {
            CallableStatement inwardDetailStmt = connection.prepareCall("{? = call funcGetInwardDetailsForInward(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            inwardDetailStmt.registerOutParameter(1, 4);
            inwardDetailStmt.setString(2, sInwardId);
            inwardDetailStmt.registerOutParameter(3, 12);
            inwardDetailStmt.registerOutParameter(4, 12);
            inwardDetailStmt.registerOutParameter(5, 12);
            inwardDetailStmt.registerOutParameter(6, 12);
            inwardDetailStmt.registerOutParameter(7, 12);
            inwardDetailStmt.registerOutParameter(8, 12);
            inwardDetailStmt.registerOutParameter(9, 91);
            inwardDetailStmt.registerOutParameter(10, 12);
            inwardDetailStmt.registerOutParameter(11, 12);
            inwardDetailStmt.registerOutParameter(12, 12);
            inwardDetailStmt.registerOutParameter(13, 12);
            inwardDetailStmt.registerOutParameter(14, 12);
            inwardDetailStmt.registerOutParameter(15, 12);
            inwardDetailStmt.execute();
            int status = inwardDetailStmt.getInt(1);
            String error = inwardDetailStmt.getString(15);
            if(status == 1)
            {
                Log.log(2, "IODAO", "getInwardDetail", "SP returns a 1. Error code is :" + error);
                inwardDetailStmt.close();
                inwardDetailStmt = null;
            } else
            if(status == 0)
            {
                inward = new Inward();
                inward.setSourceType(inwardDetailStmt.getString(3));
                inward.setSourceId(inwardDetailStmt.getString(4));
                inward.setSourceName(inwardDetailStmt.getString(5));
                inward.setSourceRef(inwardDetailStmt.getString(6));
                inward.setDocumentType(inwardDetailStmt.getString(7));
                inward.setModeOfReceipt(inwardDetailStmt.getString(8));
                inward.setDateOfDocument(inwardDetailStmt.getDate(9));
                inward.setLanguage(inwardDetailStmt.getString(10));
                inward.setSubject(inwardDetailStmt.getString(11));
                inward.setRemarks(inwardDetailStmt.getString(12));
                inward.setInwardId(inwardDetailStmt.getString(13));
                inward.setProcessedBy(inwardDetailStmt.getString(14));
                inwardDetailStmt.close();
                inwardDetailStmt = null;
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
        Log.log(4, "IODAO", "getInwardDetail", "Exited");
        return inward;
    }

    public Outward getOutwardDetail(String sOutwardId)
        throws DatabaseException
    {
        Log.log(4, "IODAO", "getOutwardDetail", "Entered");
        Connection connection = DBConnection.getConnection();
        Outward outward = null;
        try
        {
            CallableStatement outwardDetailStmt = connection.prepareCall("{ ? = call funcGetOutwardDetForOutward(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            outwardDetailStmt.registerOutParameter(1, 4);
            outwardDetailStmt.setString(2, sOutwardId);
            outwardDetailStmt.registerOutParameter(3, 12);
            outwardDetailStmt.registerOutParameter(4, 12);
            outwardDetailStmt.registerOutParameter(5, 12);
            outwardDetailStmt.registerOutParameter(6, 12);
            outwardDetailStmt.registerOutParameter(7, 12);
            outwardDetailStmt.registerOutParameter(8, 91);
            outwardDetailStmt.registerOutParameter(9, 12);
            outwardDetailStmt.registerOutParameter(10, 12);
            outwardDetailStmt.registerOutParameter(11, 12);
            outwardDetailStmt.registerOutParameter(12, 12);
            outwardDetailStmt.registerOutParameter(13, 12);
            outwardDetailStmt.registerOutParameter(14, 12);
            outwardDetailStmt.execute();
            int status = outwardDetailStmt.getInt(1);
            String error = outwardDetailStmt.getString(14);
            if(status == 1)
            {
                Log.log(2, "IODAO", "getOutwardDetail", "SP returns a 1. Error code is :" + error);
                outwardDetailStmt.close();
                outwardDetailStmt = null;
            } else
            if(status == 0)
            {
                outward = new Outward();
                outward.setDestinationType(outwardDetailStmt.getString(3));
                outward.setDestinationName(outwardDetailStmt.getString(4));
                outward.setDocumentType(outwardDetailStmt.getString(5));
                outward.setModeOfDelivery(outwardDetailStmt.getString(6));
                outward.setdestinationRef(outwardDetailStmt.getString(7));
                outward.setDocumentSentDate(outwardDetailStmt.getDate(8));
                outward.setLanguage(outwardDetailStmt.getString(9));
                outward.setSubject(outwardDetailStmt.getString(10));
                outward.setRemarks(outwardDetailStmt.getString(11));
                outward.setOutwardId(outwardDetailStmt.getString(12));
                outward.setProcessedBy(outwardDetailStmt.getString(13));
                outwardDetailStmt.close();
                outwardDetailStmt = null;
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
        Log.log(4, "IODAO", "getOutwardDetail", "Exited");
        return outward;
    }

    public ArrayList getAllOutwardsForAnInwardId(String inwardId)
        throws DatabaseException
    {
        Log.log(4, "IODAO", "getAllOutwardsForAnInwardId", "Entered");
        ResultSet resultSetOutwardIds = null;
        ArrayList ArrayListOfOutwardIds = new ArrayList();
        String errorCode = null;
        Connection connection = DBConnection.getConnection();
        try
        {
            CallableStatement outwardIdsStmt = connection.prepareCall("{? = call  packGetOutwardsForInward.funcGetOutwardsForInward(?,?,?)}");
            outwardIdsStmt.registerOutParameter(1, 4);
            outwardIdsStmt.setString(2, inwardId);
            outwardIdsStmt.registerOutParameter(3, -10);
            outwardIdsStmt.registerOutParameter(4, 12);
            outwardIdsStmt.execute();
            int status = outwardIdsStmt.getInt(1);
            errorCode = outwardIdsStmt.getString(4);
            if(status == 1)
            {
                Log.log(2, "IODAO", "getAllOutwardsForAnInwardId", "SP returns a 1. Error code is :" + errorCode);
                outwardIdsStmt.close();
                outwardIdsStmt = null;
            } else
            if(status == 0)
            {
                for(resultSetOutwardIds = (ResultSet)outwardIdsStmt.getObject(3); resultSetOutwardIds.next(); ArrayListOfOutwardIds.add(resultSetOutwardIds.getObject(1)));
                resultSetOutwardIds.close();
                resultSetOutwardIds = null;
                outwardIdsStmt.close();
                outwardIdsStmt = null;
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
        Log.log(4, "IODAO", "getAllOutwardsForAnInwardId", "Exited");
        return ArrayListOfOutwardIds;
    }

    public ArrayList getAllInwardsForAnOutwardId(String sOutwardId)
        throws DatabaseException
    {
        Log.log(4, "IODAO", "getAllInwardsForAnOutwardId", "Entered");
        String errorCode = null;
        ResultSet resultSetInwardIds = null;
        ArrayList ArrayListOfInwardIds = new ArrayList();
        Connection connection = DBConnection.getConnection();
        try
        {
            CallableStatement inwardIdsStmt = connection.prepareCall("{? = call packGetInwardsForOutward.funcGetInwardsForOutward(?,?,?)}");
            inwardIdsStmt.registerOutParameter(1, 4);
            inwardIdsStmt.setString(2, sOutwardId);
            inwardIdsStmt.registerOutParameter(3, -10);
            inwardIdsStmt.registerOutParameter(4, 12);
            inwardIdsStmt.execute();
            int status = inwardIdsStmt.getInt(1);
            errorCode = inwardIdsStmt.getString(4);
            if(status == 1)
            {
                Log.log(2, "IODAO", "getAllInwardsForAnOutwardId", "SP returns a 1. Error code is :" + errorCode);
                inwardIdsStmt.close();
                inwardIdsStmt = null;
            } else
            if(status == 0)
            {
                for(resultSetInwardIds = (ResultSet)inwardIdsStmt.getObject(3); resultSetInwardIds.next(); ArrayListOfInwardIds.add(resultSetInwardIds.getObject(1)));
                resultSetInwardIds.close();
                resultSetInwardIds = null;
                inwardIdsStmt.close();
                inwardIdsStmt = null;
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
        Log.log(4, "IODAO", "getAllInwardsForAnOutwardId", "Exited");
        return ArrayListOfInwardIds;
    }

    public ArrayList getAllDocumentTypes()
        throws DatabaseException
    {
        Log.log(4, "IODAO", "getAllDocumentTypes", "Entered");
        ArrayList documentTypes = new ArrayList();
        Connection connection = DBConnection.getConnection();
        CallableStatement statement = null;
        int status = -1;
        ResultSet docTypesResultSet = null;
        String error = null;
        try
        {
            statement = connection.prepareCall("{? = call packGetAllDocumentType.funcGetDocType(?,?)}");
            statement.registerOutParameter(1, 4);
            statement.registerOutParameter(2, -10);
            statement.registerOutParameter(3, 12);
            statement.execute();
            status = statement.getInt(1);
            error = statement.getString(3);
            if(status == 1)
            {
                Log.log(2, "IODAO", "getAllDocumentTypes", "SP returns a 1. Error code is :" + error);
                statement.close();
                statement = null;
            } else
            if(status == 0)
            {
                for(docTypesResultSet = (ResultSet)statement.getObject(2); docTypesResultSet.next(); documentTypes.add(docTypesResultSet.getObject(1)));
                docTypesResultSet.close();
                docTypesResultSet = null;
                statement.close();
                statement = null;
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
        Log.log(4, "IODAO", "getAllDocumentTypes", "Exited");
        return documentTypes;
    }
    
    
    public IOFormBean getInwardDetails(String inwardNo, String instrumentNo)
    throws DatabaseException
    {
    ResultSet resultSet = null;
    IOFormBean ioFormBean = new IOFormBean();
    Connection connection = DBConnection.getConnection();
    try
    {
        CallableStatement callable = connection.prepareCall("{?=call PackGetInwardInstrDet.FuncGetInwardInstrDet(?,?,?,?)}");
        callable.registerOutParameter(1, 4);
        callable.setString(2, inwardNo);
        if(instrumentNo == null || instrumentNo.equalsIgnoreCase(""))
            callable.setNull(3, 12);
        else
            callable.setString(3, instrumentNo);
        callable.registerOutParameter(4, -10);
        callable.registerOutParameter(5, 12);
        callable.executeUpdate();
        int errorCode = callable.getInt(1);
        String error = callable.getString(5);
        resultSet = (ResultSet)callable.getObject(4);
        if(errorCode == 1)
        {
            callable.close();
            callable = null;
            System.out.println("Error:" + error);
            throw new DatabaseException(error);
        }
       // for(; resultSet.next(); ioFormBean.setReasons(resultSet.getString(16)))
       while(resultSet.next())
        {
            ioFormBean.setInwardDts(resultSet.getDate(2));
            ioFormBean.setBankNames(resultSet.getString(3));
            ioFormBean.setDrawnOnBank(resultSet.getString(4));
            ioFormBean.setPlaces(resultSet.getString(5));
            ioFormBean.setSubjects(resultSet.getString(6));
            ioFormBean.setReferenceIds(resultSet.getString(7));
            ioFormBean.setLtrDts(resultSet.getDate(8));
            ioFormBean.setSourceIds(resultSet.getString(9));
            ioFormBean.setInstrumentDts(resultSet.getDate(10));
            //ioFormBean.setInstrumentAmt(resultSet.getInt(11));
            ioFormBean.setInstrumentAmtNew(resultSet.getDouble(11));
            ioFormBean.setSection(resultSet.getString(12));
            ioFormBean.setOutwardId(resultSet.getString(13));
            ioFormBean.setOutwardDt(resultSet.getDate(14));
            ioFormBean.setAssignedTo(resultSet.getString(15));
            ioFormBean.setReasons(resultSet.getString(16));
            ioFormBean.setTxnType(resultSet.getString(17));
        }

        resultSet.close();
        resultSet = null;
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
    

    public void afterUpdateInwardDetails(String inwardId, String oldInstrumentNo, String instrumentNo, String bankNames, String drawnonBank, String places, String subjects, 
            String referenceIds, String ltrDt, String instrumentDt, double instrumentAmt, String section, String userId, String outwardId, 
            String outwardDt, String assignedTo, String reasons, String inwardDt,String txnType)
        throws DatabaseException
    {
        String methodName = "afterUpdateInwardDetails";
        
        Log.log(4, "IODAO", methodName, "Entered");
        boolean newConn = false;
        Connection connection = DBConnection.getConnection(false);

                
        if(connection == null)
        {
            connection = DBConnection.getConnection(false);
            newConn = true;
        }
        String error = null;
        try
        {
            CallableStatement callable = null;
    
            callable = connection.prepareCall("{? = call funcUpdateInward(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");  // one ? mark added for txnType that was 20th Question mark  21-jan-2014 bhuneshwar.singh@pathinfotech.com

            
            callable.registerOutParameter(1, 4);//errorcode
            callable.setString(2, inwardId);//inwardid
            if(oldInstrumentNo == null || oldInstrumentNo.equals(""))
                callable.setNull(3, 12);
            else
                callable.setString(3, oldInstrumentNo);//instrument number
            callable.setString(4, bankNames);//bank name
            callable.setString(5, drawnonBank);//drown on bank
            callable.setString(6, places);//place
            callable.setString(7, subjects);//subject
            callable.setString(8, referenceIds);//ltr reference
            if(ltrDt == null || ltrDt.equals(""))
            {
                callable.setNull(9, 91);
            } else
            {
                java.util.Date ltrDt1 = null;
                try
                {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                    ltrDt1 = formatter.parse(ltrDt);
                    
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                callable.setDate(9, new Date(ltrDt1.getTime()));//ltr date
            }
            callable.setString(10, instrumentNo);//instrument number
            if(instrumentDt == null || instrumentDt.equals(""))
            {
                callable.setNull(11, 91);
            } else
            {
                Date instmtdt = null;
                java.util.Date dt1 = null;
                try
                {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    dt1 = formatter.parse(instrumentDt);
                    instmtdt = new Date(dt1.getTime());
                    
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                callable.setDate(11, new Date(dt1.getTime()));//instrument date
            }
            callable.setDouble(12, instrumentAmt);//instrument amount
            callable.setString(13, section);//section
            callable.setString(14, userId);//user id
            if(outwardId == null || outwardId.equals(""))
                callable.setNull(15, 12);
            else
                callable.setString(15, outwardId);//outward id
            if(outwardDt == null || outwardDt.equals(""))
            {
                callable.setNull(16, 91);
            } else
            {
                Date outwardDate = null;
                java.util.Date outwarddt = null;
                try
                {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    outwarddt = formatter.parse(outwardDt);
                    outwardDate = new Date(outwarddt.getTime());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                callable.setDate(16, new Date(outwarddt.getTime()));//outward date
            }
            if(assignedTo == null || assignedTo.equals(""))
                callable.setNull(17, 12);
            else
                callable.setString(17, assignedTo);//assigned to
            if(reasons == null || reasons.equals(""))
                callable.setNull(18, 12);
            else
                callable.setString(18, reasons);//reason
            if(inwardDt == null || inwardDt.equals(""))
            {
                callable.setNull(19, 91);
            } else
            {
                Date inwardDate = null;
                java.util.Date inwDt = null;
                try
                {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    inwDt = formatter.parse(inwardDt);
                    inwardDate = new Date(inwDt.getTime());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                callable.setDate(19, new Date(inwDt.getTime()));//inward date
            }
            callable.setString(20,txnType);//cts type
            
            callable.registerOutParameter(21, 12);//error message
            callable.execute();
            
            int errorCode = callable.getInt(1);
            
             error = callable.getString(21);
            
            //Log.log(5, "IODAO", methodName, "error code and error" + errorCode + "," + error);
            if(errorCode == 1)
            {
                Log.log(2, "IODAO", methodName, error);
                callable.close();
                callable = null;
                connection.rollback();
                throw new DatabaseException(error);
            }
            callable.close();
            callable = null;
            connection.commit();
        }
        catch(SQLException e)
        {
            Log.log(2, "IODAO", methodName, e.getMessage());
            Log.logException(e);
            if(newConn)
                try
                {
                    connection.rollback();
                }
                catch(SQLException ignore) { }
            //throw new DatabaseException("Unable to update Inward Details."+error);
             e.printStackTrace();;
        }
        finally
        {
            if(newConn)
                DBConnection.freeConnection(connection);
        }
        Log.log(4, "IODAO", methodName, "Exited");
    }

    public void afterUpdateSchemePropagationDtl(IOFormBean ioFormBean, String userId)
        throws DatabaseException
    {
        String methodName = "afterUpdateSchemePropagationDtl";
        Log.log(4, "IODAO", methodName, "Entered");
        boolean newConn = false;
        Connection connection = DBConnection.getConnection(false);
        if(connection == null)
        {
            connection = DBConnection.getConnection(false);
            newConn = true;
        }
        try
        {
            CallableStatement callable = null;
            callable = connection.prepareCall("{?=call funcUpdateSchemePropagationDtl (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            callable.registerOutParameter(1, 4);
            String workshopId = ioFormBean.getWorkshopId();
            if(workshopId == null || workshopId.equals(""))
                callable.setNull(2, 12);
            else
                callable.setString(2, workshopId);
            String workshopDt = ioFormBean.getWorkshopDt();
            if(workshopDt == null || workshopDt.equals(""))
                callable.setNull(3, 91);
            else
                callable.setDate(3, Date.valueOf(DateHelper.stringToSQLdate(workshopDt)));
            callable.setString(4, ioFormBean.getType());
            callable.setString(5, ioFormBean.getMliName());
            callable.setString(6, ioFormBean.getAgencyName());
            callable.setString(7, ioFormBean.getOrganisedfor());
            callable.setString(8, ioFormBean.getBankNames());
            callable.setString(9, ioFormBean.getSourceName());
            callable.setString(10, ioFormBean.getTargetGroup());
            callable.setString(11, ioFormBean.getStateName());
            callable.setString(12, ioFormBean.getDistrictName());
            callable.setString(13, ioFormBean.getCity());
            callable.setString(14, ioFormBean.getTopic());
            callable.setInt(15, ioFormBean.getParticipants());
            callable.setString(16, ioFormBean.getName());
            callable.setString(17, ioFormBean.getOrganisation());
            callable.setString(18, ioFormBean.getReasons());
            callable.setString(19, userId);
            callable.registerOutParameter(20, 12);
            callable.execute();
            int errorCode = callable.getInt(1);
            String error = callable.getString(20);
            Log.log(5, "IODAO", methodName, "error code and error" + errorCode + "," + error);
            if(errorCode == 1)
            {
                Log.log(2, "IODAO", methodName, error);
                callable.close();
                callable = null;
                connection.rollback();
                throw new DatabaseException(error);
            }
            callable.close();
            callable = null;
            connection.commit();
        }
        catch(SQLException e)
        {
            Log.log(2, "IODAO", methodName, e.getMessage());
            Log.logException(e);
            if(newConn)
                try
                {
                    connection.rollback();
                }
                catch(SQLException ignore) { }
            throw new DatabaseException("Unable to update Scheme Propagation Details.");
        }
        finally
        {
            if(newConn)
                DBConnection.freeConnection(connection);
        }
        Log.log(4, "IODAO", methodName, "Exited");
    }

    public String getFile(String inward)
        throws Exception
    {
        Log.log(4, "IODAO", "getFile", "Entered");
        String fileName = null;
        PreparedStatement inwardStmt = null;
        Connection connection = DBConnection.getConnection();
        try
        {
            String query = "select a.KNW_FILE_PATH from knowledge_mgmt a where a.KNW_ID = ?";
            inwardStmt = connection.prepareStatement(query);
            inwardStmt.setString(1, inward);
            ResultSet inwardResult;
            for(inwardResult = inwardStmt.executeQuery(); inwardResult.next();)
                fileName = inwardResult.getString(1);

            inwardResult.close();
            inwardResult = null;
            inwardStmt.close();
            inwardStmt = null;
        }
        catch(Exception exception)
        {
            throw new DatabaseException(exception.getMessage());
        }
        finally
        {
            DBConnection.freeConnection(connection);
        }
        Log.log(4, "IODAO", "getFile", "Exited");
        return fileName;
    }
    
    
    //added by upchar@path on 22-05-2013

       /**
        * @return
        * @throws Exception
        */
       public String getInwardIdForCorr() throws Exception
          {
                       Log.log(Log.INFO,"IODAO","getInwardId","Entered");
                       CallableStatement addInwardStmt;
                       CallableStatement inwardIdStmt;
                 Connection connection=DBConnection.getConnection(false);      
                 String inwardIdForCorr = null;
                       try
                       {
                               inwardIdStmt=connection.prepareCall("{?=call  funcGetInwardIdForCorr(?,?)}");
                               inwardIdStmt.registerOutParameter(1,java.sql.Types.INTEGER);
                               inwardIdStmt.registerOutParameter(2,java.sql.Types.VARCHAR);
                               inwardIdStmt.registerOutParameter(3,java.sql.Types.VARCHAR);
                               inwardIdStmt.execute();
                               int valueReturned = inwardIdStmt.getInt(1);                 
                               if(valueReturned == Constants.FUNCTION_FAILURE)
                               {
                                       String error = inwardIdStmt.getString(3);
               
                                       Log.log(Log.ERROR,"IODAO","getInwardIdForCorr","SP returns a 1." +
                                               " Error code is :" + error);
                                       inwardIdStmt.close();
                                       inwardIdStmt = null;
                               }else if(valueReturned == Constants.FUNCTION_SUCCESS){
              inwardIdForCorr = inwardIdStmt.getString(2);   
            //  System.out.println("Inward Id:"+inwardId);
             } 
             inwardIdStmt.close();
             inwardIdStmt = null;
                                 }
                                catch(SQLException exception)
                            {
                               // exception.printStackTrace();
                                       try
                                       {
                                                 connection.rollback();
                                       }
                                        
                                       catch (SQLException ignore)
                                   {
                                       }
                               throw new DatabaseException(exception.getMessage());
                           }
                               finally
                               {
                                         DBConnection.freeConnection(connection);
                               }
                               
                        Log.log(Log.INFO,"IODAO","getInwardIdForCorr","Exited");
                   return inwardIdForCorr;            
               }
            
          //added by upchar@path on 23-05-2013  

       /**
        * @param inwardId
        * @param oldInstrumentNo
        * @param instrumentNo
        * @param bankNames
        * @param drawnonBank
        * @param places
        * @param subjects
        * @param referenceIds
        * @param ltrDt
        * @param instrumentDt
        * @param instrumentAmt
        * @param section
        * @param userId
        * @param outwardId
        * @param outwardDt
        * @param assignedTo
        * @param reasons
        * @param inwardDt
        * @throws DatabaseException
        */
       public void afterUpdateInwardCorrDetails(String inwardId,String oldInstrumentNo,String instrumentNo,
                                                String bankNames,String drawnonBank,String places,
                                                String subjects,String referenceIds,String ltrDt,
                                                String instrumentDt,int instrumentAmt,String section,String userId,
                                                String outwardId,String outwardDt,
                                                String assignedTo,String reasons,String inwardDt) throws DatabaseException
                {
                        String methodName = "afterUpdateInwardDetails" ;

                        Log.log(Log.INFO, "IODAO", methodName, "Entered") ;

                        boolean newConn = false;
           Connection connection=DBConnection.getConnection(false);
           
                        if (connection == null)
                        {
                                connection=DBConnection.getConnection(false);
                                newConn=true;
                        }
                        try
                        {
                                CallableStatement callable=null;
                        int errorCode;
                                String error;
                                
                        callable=connection.prepareCall("{?=call Funcupdateinwardcorr (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

                                callable.registerOutParameter(1,Types.INTEGER);
                                callable.setString(2,inwardId);
              if(oldInstrumentNo==null||oldInstrumentNo.equals("")){
                               callable.setNull(3,Types.VARCHAR); 
              } else {
                               callable.setString(3,oldInstrumentNo); 
              }
              
                              callable.setString(4,bankNames); 
                              callable.setString(5,drawnonBank); 
                              callable.setString(6,places); 
                              callable.setString(7,subjects); 
                              callable.setString(8,referenceIds); 
             // callable.setDate(9,java.sql.Date.valueOf(DateHelper.stringToSQLdate(ltrDt)));Modified by pradeep for new server on 16.07.2012
              
                       if(ltrDt==null||ltrDt.equals("")){
                              callable.setNull(9,java.sql.Types.DATE);
                        } else {
                             
                               java.util.Date ltrDt1=null;
                                try{
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
                                ltrDt1 = formatter.parse(ltrDt);
                              
                                }catch(Exception e){
                                e.printStackTrace(); 
                                }  
                               // callable.setDate(16,java.sql.Date.valueOf(DateHelper.stringToSQLdate(outwardDt))); Modified by pradeep for new server on 16.07.2012
                                callable.setDate(9,new java.sql.Date(ltrDt1.getTime()));
                            
                            }
                            callable.setString(10,instrumentNo);
              if(instrumentDt==null||instrumentDt.equals("")){
                            callable.setNull(11,Types.DATE);
              } else {
                   //callable.setDate(11,java.sql.Date.valueOf(DateHelper.stringToSQLdate(instrumentDt)));//Modified by pradeep for new server on 16.07.2012
                    java.sql.Date instmtdt=null;
                  java.util.Date dt1=null;
                    try{
                    
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
                    dt1 = formatter.parse(instrumentDt);
                    instmtdt=new java.sql.Date(dt1.getTime());
                 
                    }catch(Exception e){
                    e.printStackTrace(); 
                    }  
               
                   
                           callable.setDate(11,new java.sql.Date(dt1.getTime()));
              }
                          callable.setInt(12,instrumentAmt);
                          callable.setString(13,section);
                          callable.setString(14,userId);
              if(outwardId==null || outwardId.equals("")){
            //   callable.setNull(15,java.sql.Types.INTEGER);
                          callable.setNull(15,Types.VARCHAR);
              } else {
                // callable.setInt(15,Integer.parseInt(outwardId));
                         callable.setString(15,outwardId);
              }
              if(outwardDt==null||outwardDt.equals("")){
                         callable.setNull(16,java.sql.Types.DATE);
              } else {
              
              
                  java.sql.Date outwardDate=null;
                  java.util.Date outwarddt=null;
                  try{
                  
                  SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
                   outwarddt = formatter.parse(outwardDt);
                  outwardDate=new java.sql.Date(outwarddt.getTime());
                  
                  }catch(Exception e){
                  e.printStackTrace(); 
                  }  
              
                 // callable.setDate(16,java.sql.Date.valueOf(DateHelper.stringToSQLdate(outwardDt))); Modified by pradeep for new server on 16.07.2012
                          callable.setDate(16,new java.sql.Date(outwarddt.getTime()));
              
              }
            
           //   System.out.println("userId:"+userId);
           
             if(assignedTo==null || assignedTo.equals("")){
               callable.setNull(17,Types.VARCHAR);
              } else {
                // callable.setInt(15,Integer.parseInt(outwardId));
                callable.setString(17,assignedTo);
              }
           
           if(reasons==null || reasons.equals("")){
               callable.setNull(18,Types.VARCHAR);
              } else {
                // callable.setInt(15,Integer.parseInt(outwardId));
                callable.setString(18,reasons);
              }
           
           if(inwardDt==null || inwardDt.equals("")){
                callable.setNull(19,Types.DATE);
              } else {
              
                  java.sql.Date inwardDate=null;
                  java.util.Date inwDt=null;
                  
                  try{
                  
                  SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
                  inwDt = formatter.parse(inwardDt);
                  inwardDate=new java.sql.Date(inwDt.getTime());
                  
                  }catch(Exception e){
                  e.printStackTrace(); 
                  }  
              
                // callable.setInt(15,Integer.parseInt(outwardId));
               // callable.setDate(19,java.sql.Date.valueOf(DateHelper.stringToSQLdate(inwardDt)));Modified by pradeep for new server on 16.07.2012
                                callable.setDate(19,new java.sql.Date(inwDt.getTime()));
              }        
                                callable.registerOutParameter(20,Types.VARCHAR);

                               callable.execute();

                                errorCode=callable.getInt(1);

                                error=callable.getString(20);

                                Log.log(Log.DEBUG, "IODAO", methodName, "error code and error"+errorCode+","+error) ;
								
                                if(errorCode==Constants.FUNCTION_FAILURE)
                                {
                                        Log.log(Log.ERROR, "IODAO", methodName, error) ;

                                        callable.close();
                                        callable=null;
                                        connection.rollback();

                                        throw new DatabaseException(error);
                                }

                                callable.close();
                                callable=null;
                                connection.commit();
                        
                        }
                        catch(SQLException e)
                        {
                                Log.log(Log.ERROR, "IODAO", methodName, e.getMessage()) ;

                                Log.logException(e);

                                if (newConn)
                                {
                                        try
                                        {
                                                connection.rollback();
                                        }
                                        catch (SQLException ignore)
                                        {
                                        }
                                }

                                throw new DatabaseException("Unable to update Inward Correspondance Details.");

                        }
                        finally
                        {
                                if (newConn)
                                {
                                        DBConnection.freeConnection(connection);
                                }
                        }
                                Log.log(Log.INFO, "IODAO", methodName, "Exited") ;
                }
     
    
       
}