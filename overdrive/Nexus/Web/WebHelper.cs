using System.Collections;
using System.Text;
using Nexus.Core.Helpers;

namespace Nexus.Web
{
	public class WebHelper : ViewHelper
	{
		public override string AlertsFor(string id)
		{
			string alert = null;
			IList list = Alerts[id] as IList;
			if (list == null) return alert;

			if (list.Count == 1)
			{
				alert = HtmlMessage(list[0]);
			}
			else
			{
				alert = HtmlMessageList(list);
			}
			return alert;
		}

		public override string AlertsText
		{
			get { return HtmlMessageBuilder(Alerts); }
		}

		public override string HintsFor(string id)
		{
			return HtmlMessageList(Hints[id] as IList);
		}

		public override string HintsText
		{
			get { return HtmlMessageBuilder(Hints); }
		}

		#region Message utilities

		/// <summary>
		/// Build a  message using HTML markup.
		/// </summary>
		/// <param name="message">A message</param>
		/// <returns>HTML markup presenting the messages.</returns>
		/// 
		private string HtmlMessage(object message)
		{
			StringBuilder sb = new StringBuilder("<p>");
			sb.Append(message.ToString());
			sb.Append("</p>");
			return sb.ToString();
		}

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