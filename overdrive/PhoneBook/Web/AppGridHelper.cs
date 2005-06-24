using Nexus;
using Nexus.Core;
using PhoneBook.Core;

namespace PhoneBook.Web
{

	public class AppGridHelper : GridViewHelper
	{
		public override IEntryList NewEntryList ()
		{
			return new AppEntryList();
		}
	}
}
