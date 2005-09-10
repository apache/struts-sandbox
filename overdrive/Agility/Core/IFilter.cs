/*
 * Copyright 2003-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
using System;

namespace Agility.Core
{
	/// <summary>
	/// A {@link Filter} is a specialized {@link Command} that also expects the {@link Chain} that is executing it to call the <code>postprocess()</code> method if it called the <code>execute()</code> method.  
	/// </summary>
	/// <remarks>
	/// <p>This contract must be fulfilled regardless of any possible exceptions thrown by the <code>Execute</code> method of this {@link ICommand}, or any subsequent {@link ICommand} whose <code>Execute</code> method is called.  
	/// The owning {@link IChain} must call the <code>PostProcess</code> method of each {@link IFilter} in a {@link IChain} in reverse order of the invocation of their <code>Execute</code> methods.</p>
	/// <p>The most common use case for a {@link IFilter}, as opposed to a {@link ICommand}, is where potentially expensive resources must be acquired and held until the processing of a particular request has been completed, even if execution is delegated to a subsequent {@link Command} via the <code>Execute</code> returning <code>false</code>.  
	/// A {@link IFilter} can reliably release such resources in the <code>PostProcess</code> method, which is guaranteed to be called by the owning {@link IChain}.</p>
	/// <p>@version $Revision$ $Date$</p>
	/// </remarks>
	///  
	public interface IFilter : ICommand
	{
		/// <summary>
		/// Execute any cleanup activities, such as releasing resources that were acquired during the <code>execute()</code> method of this {@link Filter} instance.
		/// </summary>
		/// <param name="context">The {@link Context} to be processed by this {@link Filter}</param>
		/// <param name="exception">The <code>Exception</code> (if any) that was thrown  by the last {@link Command} that was executed; otherwise <code>null</code></param>
		/// <returns>If a non-null <code>exception</code> was "handled" by this  method (and therefore need not be rethrown), return <code>true</code>;  otherwise return <code>false</code></returns>
		bool PostProcess(IContext context, Exception exception);
	}
}