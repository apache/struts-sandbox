using Nexus;
using Nexus.Core;
using PhoneBook.Core;

namespace PhoneBook.Web
{
	public class AppGridHelper : GridViewHelper
	{
		public override IEntryList NewEntryList ()
		{
			return new AppEntryList ();
		}

		private bool _HasEditColumn = true;
		public override bool HasEditColumn
		{
			get { return _HasEditColumn; }
			set { _HasEditColumn = value; }
		}

	}
}