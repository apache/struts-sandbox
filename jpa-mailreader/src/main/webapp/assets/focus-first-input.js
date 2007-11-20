  // http://www.codeproject.com/jscript/FocusFirstInput.asp
  var bFound = false;
  for (f=0; f < document.forms.length; f++)
  {
    for(i=0; i < document.forms[f].length; i++)
    {
      if (document.forms[f][i].type != "hidden")
      {
        if (document.forms[f][i].disabled !== true)
        {
            document.forms[f][i].focus();
            bFound = true;
        }
      }
      if (bFound === true) 
      {	
      	break;
      }
    }
    if (bFound === true) 
    {
      break;
    }
  }
