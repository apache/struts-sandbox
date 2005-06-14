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
	/// 
	public abstract class ViewHelper : IViewHelper
	{
		#region private 

		private IRequestContext _Context;
		public IRequestContext Context
		{
			get
			{
				if (_Context == null)
					_Context = Catalog.GetContext (Command);
				return _Context;
			}
		}

		#endregion

		#region Read and Bind (abstract)

		public abstract void ExecuteBind (ICollection controls);

		public abstract void ReadExecute (ICollection controls);

		public abstract void Bind (ICollection controls);

		public abstract void Read (ICollection controls);

		public void Execute ()
		{
			Catalog.ExecuteView (Context);
		}

		#endregion

		#region Messages

		public IDictionary Alerts
		{
			get { return Context.Alerts; }
		}

		public bool HasAlerts
		{
			get { return Context.HasAlerts; }
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
			get { return (!HasAlerts && !HasFault); }
		}

		public IDictionary Hints
		{
			get { return Context.Hints; }
		}

		public bool HasHints
		{
			get { return Context.HasHints; }
		}

		#endregion 

		#region Tables

		public IFieldTable FieldTable
		{
			get { return Context.FieldTable; }
		}

		private IList _FieldSet;
		public IList FieldSet
		{
			get { return _FieldSet; }
			set { _FieldSet = value; }
		}

		public string Prefix
		{
			get { return Context[Tokens.Prefix] as string; }
			set { Context[Tokens.Prefix] = value; }
		}

		public string ListSuffix
		{
			get { return Context[Tokens.ListSuffix] as string; }
			set { Context[Tokens.ListSuffix] = value; }
		}

		#endregion 

		#region Properties

		public bool NullIfEmpty
		{
			get
			{
				bool v = (Boolean) Context[Tokens.NullIfEmpty];				
				return v;
			}
			set
			{	Boolean b = new Boolean();
				bool v = b.Equals (true) ? true : false ;
				Context[Tokens.NullIfEmpty] = v;
			}
		}

		public string SelectItemPrompt
		{
			get { return Context[Tokens.SelectItemPrompt] as string; }
			set { Context[Tokens.SelectItemPrompt] = value; }
		}

		public IRequestCatalog Catalog
		{
			get { return Context[Tokens.Catalog] as IRequestCatalog; }
			set { Context[Tokens.Catalog] = value; }
		}

		public IRequestCommand Command
		{
			get { return Context[Tokens.Command] as IRequestCommand; }
			set { Context[Tokens.Command] = value; }
		}

		#endregion 
	}
}