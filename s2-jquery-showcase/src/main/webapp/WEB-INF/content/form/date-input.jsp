<%@ taglib prefix="sjx" uri="/struts-jquery-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Date Picker</title>
    <sjx:head/>

    <script type="text/javascript">
        var ops = {"maxDate" : "+1m"};        
    </script>
    <style type="text/css">
        .someClass {
            background-color: #c3d9ff;
        }
    </style>
</head>
<body>
<s:form id="indexForm" method="post" namespace="/form" action="date" theme="simple">
    Tooltip from a Date: <sjx:datepicker name="date" displayFormat="mm dd yy" theme="jquery-simple" readonly="true" imageTooltip="Select a date"/>
    Read only from a Calendar: <sjx:datepicker name="date1" displayFormat="mm dd yy" theme="jquery-simple" value="%{date}" readonly="true"/>
    CSS Style: <sjx:datepicker name="date2" theme="jquery-simple" value="%{calendar}" cssStyle="background-color:#e5ecf9"/>
    CSS Class from String Object: <sjx:datepicker name="date3" theme="jquery-simple" value="%{shortFormat}" cssClass="someClass"/>
    Short Format: <sjx:datepicker name="date4" theme="jquery-simple" value="%{'3/10/09'}"/>
    Medium Format: <sjx:datepicker name="date5" theme="jquery-simple" value="%{'Mar 10, 2009'}" />
    Large Format: <sjx:datepicker name="date6" theme="jquery-simple" value="%{'March 10, 2009'}" />
    Unparsable Date: <sjx:datepicker name="date7" theme="jquery-simple" value="%{'Not a Date'}" />
    Options from an inline map:<sjx:datepicker name="date8" theme="jquery-simple" options='{"maxDate" : "+1m"}'/>
    Options from an object:<sjx:datepicker name="date9" theme="jquery-simple" options="ops"/>
    No Month or Year dropdown:<sjx:datepicker name="date10" theme="jquery-simple" changeMonth="false" changeYear="false"/>
    <s:submit />
</s:form>
</body>
</html>