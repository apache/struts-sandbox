/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
using System;
using System.Collections;
using System.Text;
using Agility.Core;
using Nexus.Core.Profile;
using Nexus.Core.Tables;

namespace Nexus.Core
{
	/// <summary>
	/// Implement IRequestContext.
	/// </summary>
	/// 
	public class RequestContext : Context, IRequestContext
	{
		/// <summary>
		/// Express state as a key=value list.
		/// </summary>
		/// <returns>Formatted string representing state.</returns>
		public override string ToString()
		{
			StringBuilder sb = new StringBuilder();
			foreach (DictionaryEntry e in this)
			{
				sb.Append("[");
				sb.Append(e.Key);
				sb.Append("=");
				sb.Append(e.Value.ToString());
				sb.Append("], ");
			}
			return sb.ToString();
		}

		/// <summary>
		/// Convenience constructor to set Command on instantiation.
		/// </summary>
		/// <param name="command">Name of Command processing this Context.</param>
		/// 
		public RequestContext(string command)
		{
			Command = command;
		}

		public RequestContext(IDictionary entries)
		{
			foreach (DictionaryEntry entry in entries)
			{
				base.Add(entry.Key, entry.Value);
			}
		}

		/// <summary>
		/// Default, no argument constructor.
		/// </summary>
		/// 
		public RequestContext()
		{
		}

		public string Command
		{
			get { return this[Tokens.Command] as string; }
			set { this[Tokens.Command] = value; }
		}

		public IRequestCommand CommandBin
		{
			get { return this[Tokens.CommandBin] as IRequestCommand; }
			set { this[Tokens.CommandBin] = value; }
		}

		public IFieldTable FieldTable
		{
			get { return this[Tokens.FieldTable] as IFieldTable; }
			set { this[Tokens.FieldTable] = value; }
		}

		public IProfile Profile
		{
			get { return this[UserProfile.USER_PROFILE] as IProfile; }
			set
			{
				this[UserProfile.USER_PROFILE] = value;
				IProfile profile = value;
				if (null != profile)
					this[UserProfile.USER_ID] = profile.UserId;
			}
		}

		public bool HasOutcome
		{
			get { return Contains(Command); }
		}

		public object Outcome
		{
			get { return this[Command]; }
			set { this[Command] = value; }
		}

		/// <summary>
		/// Instantiate Criteria, if needed.
		/// </summary>
		private void LazyCriteria()
		{
			// Naive, but we expect a Context instance to be single-threaded.
			if (this[Tokens.Criteria] == null)
				this[Tokens.Criteria] = new Hashtable(); // TODO: Spring?
		}

		public IDictionary Criteria
		{
			get
			{
				LazyCriteria();
				return this[Tokens.Criteria] as IDictionary;
			}
			set { this[Tokens.Criteria] = value; }
		}

		public bool HasCriteria()
		{
			return ContainsKey(Tokens.Criteria);
		}


		/// <summary>
		/// Convenience method to lazily instantiate a message store.
		/// </summary>
		/// <param name="template">Message template to add to the queue.</param>
		/// <param name="queue">Token for queue of messages within the 
		/// store.</param>
		/// <param name="key">Token for message store.</param>
		/// 
		private void AddStore(string template, string queue, string key)
		{
			IDictionary store = this[key] as IDictionary;
			if (null == store)
			{
				store = new Hashtable(); // ISSUE: Spring?
				this[key] = store;
			}
			IList list;
			if (store.Contains(queue))
				list = store[queue] as IList;
			else
			{
				list = new ArrayList(); // ISSUE: Spring?
				store[queue] = list;
			}
			list.Add(template);
		}

		public string FormatTemplate(string template, string value)
		{
			StringBuilder sb = new StringBuilder();
			sb.AppendFormat(template, value);
			return sb.ToString();
		}

		public IDictionary Alerts
		{
			get { return this[Tokens.Alerts] as IDictionary; }
			set { this[Tokens.Alerts] = value; }
		}

		public void AddAlert(string template)
		{
			AddStore(template, Tokens.GenericMessage, Tokens.Alerts);
		}

		public void AddAlert(string template, string queue)
		{
			AddStore(template, queue, Tokens.Alerts);
		}

		public void AddAlertForField(string key)
		{
			string message = FormatTemplate(FieldTable.Alert(key), FieldTable.Label(key));
			AddAlert(message, key);
		}

		public void AddAlertRequired(string key)
		{
			string message = FormatTemplate(FieldTable.Required(key), FieldTable.Label(key));
			AddAlert(message, key);
		}

		public bool HasAlerts
		{
			get { return ContainsKey(Tokens.Alerts); }
		}

		public Exception Fault
		{
			get { return this[Tokens.Fault] as Exception; }
			set
			{
				Exception e = value;
				this[Tokens.Fault] = e;
				AddAlert(e.Message);
			}
		}

		public bool HasFault
		{
			get { return ContainsKey(Tokens.Fault); }
		}

		public bool IsNominal
		{
			get { return (!HasAlerts && !HasFault); }
		}

		public IDictionary Hints
		{
			get { return this[Tokens.Hints] as IDictionary; }
			set { this[Tokens.Hints] = value; }
		}

		public void AddHint(string template)
		{
			AddStore(template, Tokens.GenericMessage, Tokens.Hints);
		}

		public void AddHint(string template, string queue)
		{
			AddStore(template, queue, Tokens.Hints);
		}

		public bool HasHints
		{
			get { return ContainsKey(Tokens.Hints); }
		}

	}
}