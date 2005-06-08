using NUnit.Extensions.Asp;
using NUnit.Extensions.Asp.AspTester;
using NUnit.Framework;

namespace WNE.Core.Forms
{
	[TestFixture]
	public class DirectoryTest : WebFormTestCase
	{
		
		 PanelTester pnlFind;
		 DropDownListTester lstSelect;
		 TextBoxTester txtFind;
		 ButtonTester cmdFind;

		 PanelTester pnlList;
		 DataGridTester repList;
		 ButtonTester cmdAdd;

		protected override void SetUp ()
		{
			base.SetUp ();
			
			pnlFind = new PanelTester("pnlFind", CurrentWebForm);
			lstSelect = new DropDownListTester("lstSelect", CurrentWebForm);
			txtFind = new TextBoxTester("txtFind",CurrentWebForm);
			cmdFind = new ButtonTester("cmdFind",CurrentWebForm);

			pnlList = new PanelTester("pnlList",CurrentWebForm);
			repList = new DataGridTester("repList",CurrentWebForm);
			cmdAdd = new ButtonTester("cmdAdd",CurrentWebForm);

			Browser.GetPage ("http://localhost/PhoneBook/Forms/Directory.aspx");
		}

		[Test]
		public void FindControls()
		{
			WebAssert.Visible(pnlFind);
			WebAssert.Visible(lstSelect);
			WebAssert.Visible(txtFind);
			WebAssert.Visible(cmdFind);

			WebAssert.Visible(pnlList);
			WebAssert.Visible(repList);
			WebAssert.Visible(cmdAdd);
		}

	}
}