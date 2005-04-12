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
using Agility.Core;

namespace Nexus.Core
{
	/// <summary>
	/// Abstract IRequestCommand; subclass must implement RequestExecute.
	/// </summary>
	public abstract class RequestCommand : IRequestCommand
	{
		/// <summary>
		/// Return STOP if a Command is part of a Chain.
		/// </summary>
		public const bool STOP = true;

		/// <summary>
		/// Return CONTINUE if another Command can run.
		/// </summary>
		public const bool CONTINUE = false;

		private string _ID = null;
		public virtual string ID
		{
			get { return _ID; }
			set { _ID = value; }
		}

		public virtual IRequestContext NewContext ()
		{
			// Return a new instance on each call.
			return new RequestContext (ID);
		}

		public abstract bool RequestExecute (IRequestContext context);

		public virtual bool Execute (IContext _context)
		{
			IRequestContext context = _context as IRequestContext;
			return RequestExecute (context);
		}
	}
}