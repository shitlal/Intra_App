package com.cgtsi.action;

import com.cgtsi.actionform.ProductInvForm;
import com.cgtsi.actionform.QuoteForm;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.validator.DynaValidatorActionForm;
import org.apache.struts.validator.DynaValidatorForm;


public class OrderAction extends BaseAction{

    public ActionForward createQuote(
                    ActionMapping mapping,
                    ActionForm form,
                    HttpServletRequest request,
                    HttpServletResponse response)
                    throws Exception {
      //      QuoteForm form2 = (QuoteForm)form;
        
         //   ProductInvForm bean = new ProductInvForm();
//            bean.setProductid("11");
//            
//            ProductInvForm bean2 = new ProductInvForm();
//            bean2.setProductid("22");
//            
//            ProductInvForm bean3 = new ProductInvForm();
//            bean3.setProductid("33");
//        
//            ArrayList listOfProducts = new ArrayList();
//            listOfProducts.add(bean);
//            listOfProducts.add(bean2);
//            listOfProducts.add(bean3);
//            
//           form2.setProdinvlist(listOfProducts);
                 DynaValidatorForm dynaForm = (DynaValidatorForm)form;
        //    DynaActionForm dynaForm = (DynaActionForm)form;
                 int total = 3;
                 for(int i=1;i<=total;i++){
                 String orderId = "orderId" + i;
                 System.out.println("order id:"+orderId);
                    dynaForm.set(orderId,"10"+i);
                 }
                 request.setAttribute("size",String.valueOf(total));
            
                return mapping.findForward("success");
    }

    public ActionForward saveQuoteDetails(ActionMapping mapping,ActionForm form,
                                            HttpServletRequest request,HttpServletResponse response)throws Exception{
    
//            QuoteForm qouteForm = (QuoteForm)form;
            
//            String orderId1 = qouteForm.getOrderId1();
//            String orderName1 = qouteForm.getOrderName1();
//            double range1 = qouteForm.getOrderRange1();
//            
//            String orderId2 = qouteForm.getOrderId2();
//            String orderName2 = qouteForm.getOrderName2();
//            double range2 = qouteForm.getOrderRange2();
//            
//            String orderId3 = qouteForm.getOrderId3();
//            String orderName3 = qouteForm.getOrderName3();
//            double range3 = qouteForm.getOrderRange3();

              DynaValidatorForm dynaForm = (DynaValidatorForm)form;
      //      DynaValidatorActionForm dynaForm = (DynaValidatorActionForm)form;
       //     DynaActionForm dynaForm = (DynaActionForm)form;
                String size = (String)request.getParameter("size");
                int total = Integer.parseInt(size);
            
                for(int i=1;i<=total;i++){
                    String orderId = "orderId" + i;
                    String orderName = "orderName" + i;
                    String orderRange = "orderRange" + i;
                    
                   String id =(String)dynaForm.get(orderId);
                   String name =(String)dynaForm.get(orderName);
                   double range =(Double)dynaForm.get(orderRange);
                   System.out.println("id:"+id+"---name:"+name+"----range:"+range);
                }
            
              return mapping.findForward("success");
            }

    public OrderAction() {
    }
}
