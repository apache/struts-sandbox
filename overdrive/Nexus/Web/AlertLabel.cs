namespace Nexus.Web
{
	/// <summary>
	/// Extend MessageLabel to respond only to the View_Alert event 
	/// and use an alternate suffix.
	/// </summary>
	public class AlertLabel : ViewLabel
	{
		/// <summary>
		/// Default suffix for NameLabel IDs ["_alert"].
		/// </summary>
		/// 
		public const string ALERT_SUFFIX = "_alert";

		/// <summary>
		/// Set the defaults for this subclass.
		/// </summary>
		/// 
		public AlertLabel()
		{
			Suffix = ALERT_SUFFIX;
			View_Hint = false;
			EnableViewState = false;
		}

	}
}