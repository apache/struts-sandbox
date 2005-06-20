using NUnit.Extensions.Asp;
using NUnit.Extensions.Asp.AspTester;
using NUnit.Framework;

namespace WNE.Core.Forms
{
	[TestFixture]
	public class DirectoryTest : WebFormTestCase
	{

		PanelTester pnlFind;
		DropDownListTester lstLastName;
		DropDownListTester lstFirstName;
		DropDownListTester lstExtension;
		DropDownListTester lstUserName;
		DropDownListTester lstHireDate;
		DropDownListTester lstHours;
		DropDownListTester lstEditor;

		ButtonTester cmdListAll;
		ButtonTester cmdPrint;

		PanelTester pnlList;
		DataGridTester repList;
		ButtonTester cmdAdd;

		private DropDownListTester[] GetLists ()
		{
			DropDownListTester[] lists = {lstLastName,lstFirstName,lstExtension,lstUserName,lstHireDate,lstHours,lstEditor};
			return lists;
		}

		protected override void SetUp ()
		{
			base.SetUp ();
			
			pnlFind = new PanelTester("pnlFind", CurrentWebForm);
			lstLastName = new DropDownListTester("lstLastName",CurrentWebForm);
			lstFirstName = new DropDownListTester("lstFirstName",CurrentWebForm);
			lstExtension = new DropDownListTester("lstExtension",CurrentWebForm);
			lstUserName = new DropDownListTester("lstUserName",CurrentWebForm);
			lstHireDate = new DropDownListTester("lstHireDate",CurrentWebForm);
			lstHours = new DropDownListTester("lstHours",CurrentWebForm);
			lstEditor = new DropDownListTester("lstEditor",CurrentWebForm);
			cmdListAll = new ButtonTester("cmdListAll",CurrentWebForm);
			cmdPrint = new ButtonTester("cmdPrint",CurrentWebForm);

			pnlList = new PanelTester("pnlList",CurrentWebForm);
			repList = new DataGridTester("repList",CurrentWebForm);
			cmdAdd = new ButtonTester("cmdAdd",CurrentWebForm);

			Browser.GetPage ("http://localhost/PhoneBook/Forms/Directory.aspx");
		}

		[Test]
		public void FindControls()
		{
			WebAssert.Visible(pnlFind);
			foreach (DropDownListTester list in GetLists())
			{
				WebAssert.Visible (list);
			}
			WebAssert.Visible(cmdListAll);
			WebAssert.Visible(cmdPrint);
			WebAssert.Visible(pnlList);
			WebAssert.Visible(repList);
			WebAssert.NotVisible (cmdAdd);// Visible if Editor
		}

	}
}