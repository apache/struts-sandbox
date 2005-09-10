using System;
using System.Collections;
using System.Text;
using Nexus.Core.Helpers;

namespace Nexus.Web
{
	/// <summary>
	/// Summary description for WebHelper.
	/// </summary>
	public class WebHelper : ViewHelper
	{
		public override void ExecuteBind(ICollection controls)
		{
			throw new NotImplementedException();
		}

		public override void ReadExecute(ICollection controls)
		{
			throw new NotImplementedException();
		}

		public override void Bind(ICollection controls)
		{
			throw new NotImplementedException();
		}

		public override void Read(ICollection controls)
		{
			throw new NotImplementedException();
		}

		public override string ErrorsText
		{
			get { return HtmlMessageBuilder(Alerts); }
		}

		public override string HintsText
		{
			get { return HtmlMessageBuilder(Hints); }
		}

		#region Message utilities

		/// <summary>
		/// Build a set of messages using HTML markup.
		/// </summary>
		/// <param name="messages">A list of messages</param>
		/// <returns>HTML markup presenting the messages.</returns>
		/// 
		private string HtmlMessageList(IList messages)
		{
			StringBuilder sb = new StringBuilder("<ul>");
			foreach (object o in messages)
			{
				sb.Append("<li>");
				sb.Append(o.ToString());
				sb.Append("</li>");
			}
			sb.Append("</ul>");

			return sb.ToString();
		}

		/// <summary>
		/// Build a set error messages using HTML markup.
		/// </summary>
		/// <param name="store">A context listing errors, if any</param>
		/// <returns>HTML markup presenting the errors.</returns>
		/// 
		private string HtmlMessageBuilder(IDictionary store)
		{
			string messageMarkup = null;
			if (store != null)
			{
				IList messages = new ArrayList();
				ICollection keys = store.Keys;
				foreach (string key in keys)
				{
					IList sublist = store[key] as IList;
					foreach (string message in sublist) messages.Add(message);
				}
				messageMarkup = HtmlMessageList(messages);
			}

			if (messageMarkup != null)
			{
				StringBuilder sb = new StringBuilder(messageMarkup);
				return sb.ToString();
			}
			return null;
		}

		#endregion 
	}
}