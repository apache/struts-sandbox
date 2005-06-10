using System;
using System.Collections;
using System.Text;
using Nexus.Core.Helpers;

namespace Nexus.Web.Helpers
{
	/// <summary>
	/// Summary description for WebViewHelper.
	/// </summary>
	public class WebViewHelper : ViewHelper
	{

		#region Error Builders

		/// <summary>
		/// Build a set of error messages using HTML markup.
		/// </summary>
		/// <param name="errors">A list of error messages</param>
		/// <returns>HTML markup presenting the errors.</returns>
		public static string HtmlErrorList (IList errors)
		{
			StringBuilder sb = new StringBuilder ("<ul>");
			foreach (object o in errors)
			{
				sb.Append ("<li>");
				sb.Append (o.ToString ());
				sb.Append ("</li>");
			}
			sb.Append ("</ul>");

			return sb.ToString ();
		}

		/// <summary>
		/// Build a set error messages using HTML markup.
		/// </summary>
		/// <param name="fault">An exception instance, if any</param>
		/// <param name="store">A context listing errors, if any</param>
		/// <returns>HTML markup presenting the errors.</returns>
		public string HtmlErrorBuilder (Exception fault, IDictionary store)
		{
			string errorMarkup = null;
			if (store != null)
			{
				IList errors = new ArrayList ();
				ICollection keys = Context.Keys;
				foreach (string key in keys)
				{
					IList sublist = store [key] as IList;
					foreach (string message in sublist) errors.Add (message);
				}
				errorMarkup = HtmlErrorList (errors);
			}

			if (errorMarkup != null)
			{
				StringBuilder sb = new StringBuilder (errorMarkup);
				return sb.ToString ();
			}
			return null;
		}

		public string HtmlErrorBuilder ()
		{
			return HtmlErrorBuilder (Fault, Errors);
		}

		#endregion 

		#region IViewHelper

		public override void ExecuteBind (ICollection controls)
		{
			throw new NotImplementedException ();
		}

		public override void ReadExecute (ICollection controls)
		{
			throw new NotImplementedException ();
		}

		public override void Bind (ICollection controls)
		{
			throw new NotImplementedException ();
		}

		public override void Read (ICollection controls)
		{
			throw new NotImplementedException ();
		}

		#endregion
	}
}
