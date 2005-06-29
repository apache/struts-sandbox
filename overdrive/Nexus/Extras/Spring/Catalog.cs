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
using Nexus.Core;
using Nexus.Core.Helpers;
using Nexus.Core.Tables;
using Spring.Context;

namespace Nexus.Extras.Spring
{
	/// <summary>
	/// Implement IRequestCatalog  
	/// using Spring as an IOC container [OVR-8].
	/// </summary>
	/// 
	public class Catalog : IRequestCatalog, IApplicationContextAware
	{

		#region Messages 

		private const string msg_ADD_COMMAND = "This catalog instance is created through dependency injection.";
		private const string msg_MISSING = "Object is not found in Factory.";
		private const string msg_NULL = "Object ID cannot be null.";
		private const string msg_TYPE = "Command is not a IRequestCommand or IRequestChain.";
		private const string msg_TYPE_HELPER = "Object is not a IViewHelper.";
		private const string msg_CATALOG_CONTEXT_NULL = "Catalog: Context cannot be null!";
		private const string msg_CATALOG_COMMAND_NULL = "Catalog: Command within Context cannot be null! -- Was Context retrieved from Catalog?";

		#endregion

		#region IApplicationContextAware 

		private IApplicationContext _Factory = null;

		public IApplicationContext ApplicationContext
		{
			get { return _Factory; }
			set { _Factory = value; }
		}

		#endregion

		#region IRequestCatalog

		public object GetObject (string name)
		{
			if (null == name)
			{
				Exception e = new Exception (msg_NULL);
				throw(e);
			}
			object o = Factory ().GetObject (name);
			if (o == null)
			{
				Exception e = new Exception (msg_MISSING);
				throw(e);
			}
			return o;
		}

		/// <summary>
		/// Not implemented as Catalog is expected to be created by an IOC framework.
		/// </summary>
		/// <param name="name">ID for command</param>
		/// <param name="command">Command instance</param>
		public void AddCommand (string name, ICommand command)
		{
			throw new NotImplementedException (msg_ADD_COMMAND); // OK
		}

		/// <summary>
		/// Obtain Command and verify that instance is a IRequestCommand.
		/// </summary>
		/// <param name="name">Command ID</param>
		/// <returns>IRequestCommand instance for name</returns>
		/// <exception cref="Exception">
		/// Throws Exception if name is null, 
		/// name is not in catalog, 
		/// or if instance for name is not a IRequestCommand
		/// </exception>
		public ICommand GetCommand (string name)
		{
			object o = GetObject (name);
			IRequestCommand command = o as IRequestCommand;
			if (command == null)
			{
				Exception e = new Exception (msg_TYPE);
				throw(e);
			}
			return command;
		}

		public IEnumerator GetNames ()
		{
			string[] names = _Factory.GetObjectDefinitionNames ();
			IEnumerator enu = names.GetEnumerator ();
			return enu;
		}

		#endregion

		#region IRequestCatalog

		/// <summary>
		/// Default constructor.
		/// </summary>
		public Catalog ()
		{
		}

		/// <summary>
		/// Construct object and set ApplicationContext.
		/// </summary>
		/// <param name="value">Our ApplicationContext</param>
		public Catalog (IApplicationContext value)
		{
			ApplicationContext = value;
		}

		/// <summary>
		/// Provide the IApplicationContext instance.
		/// </summary>
		/// <returns>IApplicationContext instance</returns>
		public IApplicationContext Factory ()
		{
			return _Factory;
		}


		private IFieldTable _FieldTable = null;
		/// <summary>
		/// Access method for the Catalog's FieldTable.
		/// </summary>
		/// <returns>FieldTable for this Catalog</returns></returns>
		/// 
		private IFieldTable GetFieldTable ()
		{
			if (_FieldTable == null)
				_FieldTable = GetObject (Tokens.ID_FIELD_TABLE) as IFieldTable;
			return _FieldTable;
		}

		public IViewHelper GetHelper (string name)
		{
			object o = GetObject (name);
			IViewHelper helper = o as IViewHelper;
			if (helper == null)
			{
				Exception e = new Exception (msg_TYPE_HELPER);
				throw(e);
			}
			return helper;
		}

		public IRequestContext GetRequest (string name)
		{
			ICommand _command = GetCommand (name);
			IRequestCommand _rc = _command as IRequestCommand;
			return GetRequest (_rc);
		}

		public IRequestContext GetRequest (IRequestCommand command)
		{
			IRequestContext context = null;
			try
			{
				context = command.NewContext ();
				context [Tokens.CommandBin] = command;
				context [Tokens.FieldTable] = GetFieldTable ();
				// TODO: MessageTable
			}
			catch (Exception e)
			{
				context = new RequestContext ();
				context.Fault = e;
				// ISSUE: Log exception(faults) (Log all errors in verbose mode?)
				// ISSUE: Provide an alternate location on fault? -- Declarative exception handing
			}
			return context;

		}

		public IRequestContext GetRequest (string name, IDictionary input)
		{
			IRequestContext context = GetRequest (name);
			context.Criteria = input;
			return context;
		}

		/// <summary>
		/// Confirm that the Context is not null, and the Context's Command is not null.
		/// </summary>
		/// <param name="context">IRequestContext to verify</param>
		/// <returns>The non-null Command for this Context</returns>
		private IRequestCommand VerifyRequest (IRequestContext context)
		{
			if (null == context)
			{
				context = new RequestContext ();
				context.AddAlert (msg_CATALOG_CONTEXT_NULL);
			}

			IRequestCommand command = context [Tokens.CommandBin] as IRequestCommand;

			if (null == command)
				context.AddAlert (msg_CATALOG_COMMAND_NULL);

			return command;
		}

		public IRequestContext ExecuteRequest (string name)
		{
			IRequestContext context = GetRequest (name);
			ExecuteRequest (context);
			return context;
		}

		public void ExecuteRequest (IRequestContext context)
		{
			IRequestCommand command = VerifyRequest (context);
			if (context.IsNominal)
			{
				try
				{
					command.Execute (context);
				}
				catch (Exception e)
				{
					context.Fault = e;
				}
			}

			// ISSUE: Log exception(faults) (Log all errors in verbose mode?)
			// ISSUE: Provide an alternate location on fault? -- Declarative exception handing?
		}

		public void ExecuteView (IRequestContext context)
		{
			IRequestCommand command = VerifyRequest (context);
			if (context.IsNominal)
			{
				IChain chain = new Chain ();
				if (_PreOp!=null) chain.AddCommand (_PreOp);
				chain.AddCommand (command);
				if (_PostOp!=null) chain.AddCommand (_PostOp);
				try
				{
					chain.Execute (context);
				}
				catch (Exception e)
				{
					context.Fault = e;
				}
			}
		}

		private IRequestCommand _PreOp;
		public IRequestCommand PreOp
		{
			get { return _PreOp; }
			set { _PreOp = value; }
		}

		private IRequestCommand _PostOp;
		public IRequestCommand PostOp
		{
			get { return _PostOp; }
			set { _PostOp = value; }
		}

		#endregion
	}
}