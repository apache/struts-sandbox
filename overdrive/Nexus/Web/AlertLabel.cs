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
		public AlertLabel() : base()
		{
			Suffix = ALERT_SUFFIX;
			View_Hint = false;
			base.EnableViewState = false;
			// http://blogs.msdn.com/scottwil/archive/2005/01/14/353177.aspx
		}

	}
}