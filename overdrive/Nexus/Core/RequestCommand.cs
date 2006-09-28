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
using Agility.Core;

namespace Nexus.Core
{
	/// <summary>
	/// Implement IRequestCommand, leaving RequestExecute abstract.
	/// </summary>
	/// 
	public abstract class RequestCommand : IRequestCommand
	{
		/// <summary>
		/// Execute should return STOP if problem occurs, 
		/// so that a Chain can exit processing on error. 
		/// </summary>
		/// 
		public const bool STOP = true;

		/// <summary>
		/// Return CONTINUE if another Command could run.
		/// </summary>
		/// 
		public const bool CONTINUE = false;

		private string _ID = null;

		public virtual string ID
		{
			get { return _ID; }
			set { _ID = value; }
		}

		private string _QueryID = null;

		public virtual string QueryID
		{
			get
			{
				if (null == _QueryID) return ID;
				else return _QueryID;
			}
			set { _QueryID = value; }
		}

		public virtual IRequestContext NewContext()
		{
			// Return a new instance on each call.
			// ISSUE: Spring?
			return new RequestContext(ID);
		}

		private IList _RequiredIDs = null;

		public virtual IList RequiredIDs
		{
			get
			{
				return _RequiredIDs;
			}
			set { _RequiredIDs = value; }
		}

		public IList AddRequiredIDs
		{
			set
			{
				if (value == null)
					throw new ArgumentNullException("BaseNexusCommand.AddRequiredIDs", "value");
				IList list = RequiredIDs;
				if (null == list)
					RequiredIDs = value;
				else
				{
					IEnumerator elements = value.GetEnumerator();
					while (elements.MoveNext())
					{
						string i = elements.Current as string;
						bool need = (list.IndexOf(i) < 0);
						if (need) list.Add(i);
					}
				}
			}
		}

		private IList _RelatedIDs = null;

		public virtual IList RelatedIDs
		{
			get
			{
				if (_RelatedIDs==null) 
					_RelatedIDs = new ArrayList();
				return _RelatedIDs;
			}
			set { _RelatedIDs = value; }
		}

		public IList AddRelatedIDs
		{
			set
			{
				if (value == null)
					throw new ArgumentNullException("BaseNexusCommand.AddRelatedIDs", "value");
				IList list = RelatedIDs;
				if (null == list)
					RelatedIDs = value;
				else
				{
					IEnumerator elements = value.GetEnumerator();
					while (elements.MoveNext())
					{
						string i = elements.Current as string;
						bool need = (list.IndexOf(i) < 0);
						if (need) list.Add(i);
					}
				}
			}
		}

		private IList _RuntimeIDs = null;

		public virtual IList RuntimeIDs
		{
			get { return _RuntimeIDs; }
			set { _RuntimeIDs = value; }
		}

		public IList AddRuntimeIDs
		{
			set
			{
				if (value == null)
					throw new ArgumentNullException("BaseNexusCommand.AddRuntimeIDs", "value");
				IList list = RuntimeIDs;
				if (null == list)
					RuntimeIDs = value;
				else
				{
					IEnumerator elements = value.GetEnumerator();
					while (elements.MoveNext())
					{
						string i = elements.Current as string;
						bool need = (list.IndexOf(i) < 0);
						if (need) list.Add(i);
					}
				}
			}
		}

		public abstract bool RequestExecute(IRequestContext context);

		public virtual bool Execute(IContext _context)
		{
			IRequestContext context = _context as IRequestContext;
			return RequestExecute(context);
		}
	}
}