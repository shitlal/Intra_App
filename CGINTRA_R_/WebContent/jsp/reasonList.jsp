
<html>
    <head>
        <meta http-equiv="Content-Type"
              content="text/html; charset=windows-1252"></meta>
        <title>reasonList</title>
 </head>
    <LINK href="<%=request.getContextPath()%>/css/StyleSheet.css" rel="stylesheet" type="text/css">
    <body><script type="text/javascript">
    
        function postData(){           
            var cid = document.getElementById('test').value;
            //alert(cid);
           // var id = 'F#CL14OR120682701';
            var data = '';
            var cb = document.getElementsByName('cb');
            var td = document.getElementsByTagName('td');
            //alert(cb.length);
            
           for(var i = 0;i< cb.length;i++){
                if(cb[i].type == "checkbox" && cb[i].checked){
                var j = i*2+1;
                    //if(td[j].type){
                    data = data + td[j].innerText+'#';
                    //}
                }
            } 
            var more = document.getElementById('more').value; 
            data = data + more;
            //alert(data);
            opener.findObj("reasonData("+cid+")").value = data;
            window.close();
        }
    </script><table style="text-align:center;" border="1" cellpadding="0"
                    cellspacing="0">
            <tbody>
                <tr>
                    <th class="SubHeading">
                        &nbsp;
                    </th>
                    <th class="SubHeading">Rejection Reason</th>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">Possession of secured assets not taken as per sec 13(4) of SARFAESI Act.</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">Legal action taken is before NPA date.</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">Action taken under legal forum not valid/recognised as eligible under CGS.</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">Legal action is not initiated against the borrower within the due date (i.e last date of claim lodgment).</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">Suit no. not provided.</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">Repayment and/or recovery is more than the o/s amount/sanctioned amt/guaranteed amount.</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">Account was bad/doubtful of recovery as on the material date.</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">Online claim not lodged within the stipulated time period.</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">The proposal is not internally rated to determine the investment grade which is  mandatory for guarantee approvals for credit facilities above Rs. 50 lakh.</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">Third party guarantee/collateral security obtained.</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">ASF not paid/paid after due date.</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">Interest rate charged is more than 3% over Prime Lending Rate (PLR)/4% over Base rate.</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">Irregularities/ fault/negligence pointed out by internal/external auditor of the MLI.</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">Credit facility  is additional covered under ECGC/ any other insurance company etc.</td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="cb"/>
                    </td>
                    <td align="left">Activity of the borrower not eligible under CGS.</td>
                </tr>
                
                
                <tr>
                    <td>
                        <!--<input type="checkbox" name="cb"/>-->
                    </td>
                    <td align="left">
                        <textarea cols="80" rows="2"  name="more" id="more"></textarea>
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td><input type="hidden" id="test"/></td>
                </tr>
                <tr>
                    <td>                       
                    </td>
                    <td>
                        <input type="button" value="Save"
                               onclick="postData();"/>
                    </td>
                </tr>
            </tbody>
        </table></body>
</html>