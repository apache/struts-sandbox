using System;

namespace Nexus.Core.Helpers
{
	/// <summary>
	/// EventArgs type with a IViewHelper property.
	/// </summary>
	public class ViewArgs : EventArgs
	{
		private IViewHelper _Helper;

		/// <summary>
		/// Helper instance to encapsulate.
		/// </summary>
		public IViewHelper Helper
		{
			set { _Helper = value; }
			get { return _Helper; }
		}

		/// <summary>
		/// Default constructor.
		/// </summary>
		public ViewArgs()
		{
		}

		/// <summary>
		/// Convenience constructor to set helper. 
		/// </summary>
		/// <param name="helper"></param>
		public ViewArgs(IViewHelper helper)
		{
			Helper = helper;
		}
	}
}