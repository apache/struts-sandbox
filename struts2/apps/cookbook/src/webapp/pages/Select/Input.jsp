<%@ taglib prefix="ww" uri="/webwork" %>
<html>
<head>
    <title>Cookbook - Complex Input Form using Select Controls</title>
    <ww:head/>
</head>

<body>

<ww:form method="POST">
<ww:textfield
        label="Name"
        name="name"
        tooltip="Enter your Name here"/>

<ww:datepicker
        tooltip="Select Your Birthday"
        label="Birthday"
        name="birthday"/>

<ww:textarea
        tooltip="Enter your Biography"
        label="Biograph"
        name="bio"
        cols="20"
        rows="3"/>

<ww:select
        tooltip="Choose Your Favourite Color"
        label="Favorite Color"
        list="{'Red', 'Blue', 'Green'}"
        name="favoriteColor"
        emptyOption="true"
        headerKey="None"
        headerValue="None"/>

<ww:select
        tooltip="Choose Your Favourite Language"
        label="Favourite Language"
        list="favouriteLanguages"
        name="favouriteLanguage"
        listKey="key"
        listValue="description"
        emptyOption="true"
        headerKey="None"
        headerValue="None"/>

<ww:checkboxlist
        tooltip="Choose your Friends"
        label="Friends"
        list="{'Patrick', 'Jason', 'Jay', 'Toby', 'Rene'}"
        name="friends"/>

<ww:checkbox
        tooltip="Confirmed that your are Over 18"
        label="Age 18+"
        name="legalAge"/>

<ww:doubleselect
        tooltip="Choose Your State"
        label="State"
        name="region" list="{'North', 'South'}"
        value="'South'"
        doubleValue="'Florida'"
        doubleList="top == 'North' ? {'Oregon', 'Washington'} : {'Texas', 'Florida'}"
        doubleName="state"
        headerKey="-1"
        headerValue="---------- Please Select ----------"
        emptyOption="true"/>

<ww:doubleselect
        tooltip="Choose your Vehicle"
        label="Favourite Vehical"
        name="favouriteVehicalType"
        list="vehicalTypeList"
        listKey="key"
        listValue="description"
        value="'MotorcycleKey'"
        doubleValue="'YamahaKey'"
        doubleList="vehicalSpecificList"
        doubleListKey="key"
        doubleListValue="description"
        doubleName="favouriteVehicalSpecific" headerKey="-1"
        headerValue="---------- Please Select ----------"
        emptyOption="true"/>

<ww:file
        tooltip="Upload Your Picture"
        label="Picture"
        name="picture"/>

<ww:optiontransferselect
        tooltip="Select Your Favourite Cartoon Characters"
        label="Favourite Cartoons Characters"
        name="leftSideCartoonCharacters"
        leftTitle="Left Title"
        rightTitle="Right Title"
        list="{'Popeye', 'He-Man', 'Spiderman'}"
        multiple="true"
        headerKey="headerKey"
        headerValue="--- Please Select ---"
        emptyOption="true"
        doubleList="{'Superman', 'Mickey Mouse', 'Donald Duck'}"
        doubleName="rightSideCartoonCharacters"
        doubleHeaderKey="doubleHeaderKey"
        doubleHeaderValue="--- Please Select ---"
        doubleEmptyOption="true"
        doubleMultiple="true"/>

<ww:submit onclick="alert('Don't Panic! (Just press OK to continue)');"/>
</ww:form>

</body>
</html>