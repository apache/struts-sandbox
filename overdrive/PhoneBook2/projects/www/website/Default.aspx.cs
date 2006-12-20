using System;
using System.IO;
using PhoneBook2.Core;

public partial class _Default : System.Web.UI.Page 
{
    protected void Page_Load(object sender, EventArgs e)
    {
        Class1 class1 = new Class1();
        if (class1 == null) throw new InvalidDataException();
    }
}
