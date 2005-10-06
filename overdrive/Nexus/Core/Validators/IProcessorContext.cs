using System.Collections;
using Agility.Core;
using Nexus.Core.Tables;

namespace Nexus.Core.Validators
{
	/// <summary>
	/// Encapsulate values needed by standard IProcessorCommands [OVR-13].
	/// </summary>
	/// 
	public interface IProcessorContext : IContext
	{
		/// <summary>
		/// Identify the field under validation.
		/// </summary>
		/// 
		string FieldKey { get; set; }

		/// <summary>
		/// Record the source value to process.
		/// </summary>
		/// 
		object Source { get; set; }

		/// <summary>
		/// Record the target value after conversion or formatting.
		/// </summary>
		/// 
		object Target { get; set; }

		/// <summary>
		/// Identify the main IRequestContext being processed.
		/// </summary>
		IRequestContext Context { get; }

		/// <summary>
		/// Identify the set of input/output fields being processed.
		/// </summary>
		IDictionary Criteria { get; }

		/// <summary>
		/// Identify the FieldTable being utilized.
		/// </summary>
		IFieldTable FieldTable { get; }
	}
}