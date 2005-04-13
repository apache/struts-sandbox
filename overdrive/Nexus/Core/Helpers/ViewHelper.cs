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

namespace Nexus.Core.Helpers
{
	/// <summary>
	/// Standard implementation of IViewHelper.
	/// </summary>
	public class ViewHelper : IViewHelper
	{
		public ViewHelper ()
		{
			Context = new RequestContext ();
		}

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
		public static string HtmlErrorBuilder (Exception fault, IContext store)
		{
			string errorMarkup = null;
			if (store != null)
			{
				IList errors = new ArrayList ();
				ICollection keys = store.Keys;
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

		public static string HtmlErrorBuilder (IViewHelper helper)
		{
			return HtmlErrorBuilder (helper.Fault, helper.Errors);
		}

		private IRequestContext _Context;
		public IRequestContext Context
		{
			get { return _Context; }
			set { _Context = value; }
		}

		public IContext Errors
		{
			get { return Context [Tokens.ERRORS] as IContext; }
		}

		public bool HasErrors
		{
			get { return Context.Contains (Tokens.ERRORS); }
		}

		public Exception Fault
		{
			get { return Context [Tokens.FAULT] as Exception; }
		}

		public bool HasFault
		{
			get { return Context.Contains (Tokens.FAULT); }
		}

		public bool IsNominal
		{
			get { return (!HasErrors && !HasFault); }
		}
	}
}