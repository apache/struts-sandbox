namespace Nexus.Web
{
	/// <summary>
	/// Extend MessageLabel to ignore runtime events,  
	/// require an entry, and use an alternate suffix.
	/// </summary>
	public class TextLabel : ViewLabel
	{
		/// <summary>
		/// Default suffix for NameLabel IDs ["_label"].
		/// </summary>
		public const string TEXT_SUFFIX = "_label";

		/// <summary>
		/// Set the defaults for this subclass.
		/// </summary>
		public TextLabel()
		{
			Suffix = TEXT_SUFFIX;
			Required = true;
			View_Hint = false;
			View_Alert = false;
		}
	}
}