using System;
using Agility.Core;

namespace Nexus.Core
{
	/// <summary>
	/// Exchange data between business and presentation layers per [OVR-7]. 
	/// </summary>
	/// <remarks>
	/// <p>
	/// An IRequestContext can predefine whatever properties we need for storing input, 
	/// output, messages, and other common attributes, including Locale (or Culture) 
	/// and user credentials. 
	/// </p>
	/// </remarks>
	public interface IRequestContext : IContext
	{

		/// <summary>
		/// Return true if this context contains all of the given keys,.
		/// </summary>
		/// <returns>True if this context contains all of the given keys.</returns>
		bool ContainsKeys (string[] keys);
		
		/// <summary>
		/// Return true if an Outcome object is present.
		/// </summary>
		/// <returns>True if an Outcome context is present.</returns>
		bool HasOutcome { get; }

		/// <summary>
		/// Return a IList stored under the Command ID, if any.
		/// </summary>
		/// <remarks>
		/// <p>
		/// Some Commands returns List of values.
		/// So that Commands can work together as part of a Chain, 
		/// list-based Commands are expected to store the 
		/// list under their own Command ID.
		/// Outcome is a convenient method to access the 
		/// initial or "outermost" Command or Chain ID. 
		/// </p>
		/// <p>
		/// To allow use as subcommands in a Chain, 
		/// INexusCommand implementations should prefer the idiom 
		/// <code>Context[ID] = object</code>
		/// to using the Outcome directly. 
		/// Since they might not be the initial Command,
		/// but rather a subcommand, or link, in a Chain.
		/// </p>
		/// <p>
		/// Outcome is more convenient to presentation layer clients, 
		/// who are looking for the top-level output, 
		/// rather than output of a particular subcommand.
		/// </p>
		/// <p>
		/// As mentioned, both Outcome and the context[ID] idiom 
		/// can be used by Command that return lists of values. 
		/// Commands that return a single set of fields 
		/// can store the result directly in the main Context. 
		/// This strategy allows one Command to obtain field values 
		/// to be used by another Command 
		/// (like piping output between Unix shell commands.)
		/// </p>
		/// <p>
		/// Note that "Outcome" is an "alias" to an entry in 
		/// this context. 
		/// Unlike FieldState, Outcome is not a subcontext 
		/// in its own right. 
		/// </p>
		///	</remarks>
		object Outcome { get; set; }

		/// <summary>
		/// A list of error messages, keyed by the field causing the error, or to a magic global key.
		/// </summary>
		/// <remark>
		/// TODO: Refactor as NameValueCollection ?
		/// </remark>
		IContext Errors { get; set; }
		
		/// <summary>
		/// Add an error message under the "global" key.
		/// </summary>
		/// <param name="template">Message template.</param>
		void AddError (string template);

		/// <summary>
		/// Indicate whether errors exist.
		/// </summary>
		/// <returns>True if there are errors. False otherwise.</returns>
		bool HasErrors { get; }

		/// <summary>
		/// An Exception, if thrown.
		/// </summary>
		/// <remark>
		/// A IViewContext is readonly, 
		/// but another interface (e.g. IHelperContext) may extend to add a setter, if needed.
		/// </remark>
		Exception Fault { get; set; }

		/// <summary>
		/// Indicate whether an Exception was caught.
		/// </summary>
		/// <returns>True if an Exception was caught.</returns>
		bool HasFault { get; }

		/// <summary>
		/// Indicate whether context is free of faults and errors.
		/// </summary>
		/// <returns>True if there are no faults or errors.</returns>
		bool IsNominal { get; }
	}
}
