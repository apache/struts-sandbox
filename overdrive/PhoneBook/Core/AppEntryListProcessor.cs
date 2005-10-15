using Nexus.Core;
using Nexus.Core.Validators;

namespace PhoneBook.Core
{
	/// <summary>
	/// Implement Nexus.Core.Validators.EntryListProcess for AppEntryList.
	/// </summary>
	/// 
	public class AppEntryListProcessor : EntryListProcessor
	{
		public override IEntryList NewEntryList()
		{
			return new AppEntryList();
		}
	}
}