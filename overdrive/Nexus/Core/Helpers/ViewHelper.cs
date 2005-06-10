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
using Nexus.Core.Tables;

namespace Nexus.Core.Helpers
{
	/// <summary>
	/// Standard implementation of IViewHelper.
	/// </summary>
	public abstract class ViewHelper : IViewHelper
	{
		#region private 

		private IRequestContext _Context;
		public IRequestContext Context
		{
			get
			{
				if (_Context==null)
				{
					_Context = Controller.GetContext(Command);
				}				
				return _Context;
			}
		}

		#endregion

		#region Read and Bind 

		public abstract void ExecuteBind (ICollection controls);

		public abstract void ReadExecute (ICollection controls);

		public abstract void Bind (ICollection controls);

		public abstract void Read (ICollection controls);

		public void Execute ()
		{
			Controller.ExecuteView(Context);
		}

		#endregion

		#region Errors ... 

		public IDictionary Errors
		{
			get { return Context.Errors; }
		}

		public bool HasErrors
		{
			get { return Context.HasErrors; }
		}

		public Exception Fault
		{
			get { return Context.Fault; }
		}

		public bool HasFault
		{
			get { return Context.HasFault; }
		}

		public bool IsNominal
		{
			get { return (!HasErrors && !HasFault); }
		}

		public IDictionary Messages
		{
			get { return Context.Messages; }
		}

		public bool HasMessages
		{
			get { return Context.HasMessages; }
		}

		#endregion 

		#region Tables

		public IFieldTable FieldTable
		{
			get { return Context.FieldTable; }
		}
		
		public IList FieldSet
		{
			get { return Context.FieldSet; }
		}
		
		public string Prefix
		{
			get { throw new NotImplementedException (); }
			set { throw new NotImplementedException (); }
		}
		
		public string ListSuffix
		{
			get { throw new NotImplementedException (); }
			set { throw new NotImplementedException (); }
		}

		#endregion 

		#region Properties

		public bool NullIfEmpty
		{
			get { throw new NotImplementedException (); }
			set { throw new NotImplementedException (); }
		}
		public string SelectItemPrompt
		{
			get { throw new NotImplementedException (); }
			set { throw new NotImplementedException (); }
		}
		public IController Controller
		{
			get { throw new NotImplementedException (); }
			set { throw new NotImplementedException (); }
		}
		public IRequestCommand Command
		{
			get { throw new NotImplementedException (); }
			set { throw new NotImplementedException (); }
		
		}

		#endregion 
	}
}