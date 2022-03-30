// Decompiled Using: FrontEnd Plus v2.03 and the JAD Engine
// Available From: http://www.reflections.ath.cx
// Decompiler options: packimports(3) 
// Source File Name:   RpHelper.java

package com.cgtsi.receiptspayments;

import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import java.sql.Connection;

// Referenced classes of package com.cgtsi.receiptspayments:
//            RpDAO, DemandAdvice

public class RpHelper
{

    public RpHelper()
    {
    }


    public String generatePaymentId()
    {
        return null;
    }

    public String generateCGDANId(String bankId, String zoneId, String branchId,String danType, Connection connection)
        throws DatabaseException
    {
        String cgdanNo = "";
        RpDAO rpDAO = new RpDAO();
        Log.log(4, "RpHelper", "generateCGDANId", "Before calling RpDAO's method, Bank Id : " + bankId);
        cgdanNo = rpDAO.getDANId(danType, bankId, connection);
        return cgdanNo;
    }
    

    public String generateCGDANId(String bankId, String zoneId, String branchId, Connection connection)
        throws DatabaseException
    {
        String cgdanNo = "";
        RpDAO rpDAO = new RpDAO();
        Log.log(4, "RpHelper", "generateCGDANId", "Before calling RpDAO's method, Bank Id : " + bankId);
        cgdanNo = rpDAO.getDANId("CG", bankId, connection);
        return cgdanNo;
    }

    public String generateCGDANId(DemandAdvice demandAdvice, Connection connection)
        throws DatabaseException
    {
        String cgdanNo = "";
        RpDAO rpDAO = new RpDAO();
        
        if("GF".equals(demandAdvice.getDanType())){
            cgdanNo = rpDAO.getDANId("GF", demandAdvice.getBankId(), connection);
        }else{
            Log.log(4, "RpHelper", "generateCGDANId", "Before calling RpDAO's method, Bank Id : " + demandAdvice.getBankId());
            cgdanNo = rpDAO.getDANId("CG", demandAdvice.getBankId(), connection);
        }
        return cgdanNo;
    }

    public String generateSFDANId(String bankId, String zoneId, String branchId, Connection connection)
        throws DatabaseException
    {
        String methodName = "generateCGDANId";
        String sfdanNo = "";
        RpDAO rpDAO = new RpDAO();
        Log.log(4, "RpHelper", methodName, "Before calling RpDAO's method, Bank Id : " + bankId);
        sfdanNo = rpDAO.getDANId("SF", bankId, connection);
        return sfdanNo;
    }

    public String generateSFDANId(DemandAdvice demandAdvice, Connection connection)
        throws DatabaseException
    {
        String sfdanNo = "";
        RpDAO rpDAO = new RpDAO();
        Log.log(4, "RpHelper", "generateCGDANId", "Before calling RpDAO's method, Bank Id : " + demandAdvice.getBankId());
        sfdanNo = rpDAO.getDANId("SF", demandAdvice.getBankId(), connection);
        return sfdanNo;
    }

    public String generateSHDANId(String bankId, String zoneId, String branchId, Connection connection)
        throws DatabaseException
    {
        String methodName = "generateSHDANId";
        String shdanNo = "";
        RpDAO rpDAO = new RpDAO();
        Log.log(4, "RpHelper", methodName, "Before calling RpDAO's method, Bank Id : " + bankId);
        shdanNo = rpDAO.getDANId("SH", bankId, connection);
        return shdanNo;
    }

    public String generateSHDANId(DemandAdvice demandAdvice, Connection connection)
        throws DatabaseException
    {
        String methodName = "generateSHDANId";
        String shdanNo = "";
        RpDAO rpDAO = new RpDAO();
        Log.log(4, "RpHelper", "generateSHDANId", "Before calling RpDAO's method, Bank Id : " + demandAdvice.getBankId());
        shdanNo = rpDAO.getDANId("SH", demandAdvice.getBankId(), connection);
        return shdanNo;
    }

    public String generateCLDANId()
    {
        return null;
    }

    private final String className = "RpHelper";
}
