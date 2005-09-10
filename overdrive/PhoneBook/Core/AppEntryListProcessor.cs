using Nexus.Core;
using Nexus.Core.Validators;

namespace PhoneBook.Core
{
	public class AppEntryListProcessor : EntryListProcessor
	{
		public override IEntryList NewEntryList()
		{
			return new AppEntryList();
		}
	}
}