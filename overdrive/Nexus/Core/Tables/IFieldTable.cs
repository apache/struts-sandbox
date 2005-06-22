using System.Collections;
using System.Runtime.Serialization;
using Agility.Core;
using Nexus.Core.Validators;

namespace Nexus.Core.Tables
{
	/// <summary>
	/// Inventory of fields and processors used by the application.
	/// </summary>
	/// 
	public interface IFieldTable : IContext, ISerializable
	{
		/// <summary>
		/// Indicate whether this FieldTable must include all fields, 
		/// including strings fields [FALSE].
		/// </summary>
		/// 
		bool Strict { set; get; }

		/// <summary>
		/// Add a field to the set. 
		/// </summary>
		/// 
		IFieldContext AddFieldContext { set; }

		/// <summary>
		/// Add a list of fields to the set.
		/// </summary>
		/// 
		IList AddFieldContexts { set; }

		/// <summary>
		/// Obtain the FieldContext for the given ID, observing Strict setting.
		/// </summary>
		/// <param name="id">FieldContext ID</param>
		/// <returns>FieldContext for ID</returns>
		/// 
		IFieldContext GetFieldContext (string id);

		/// <summary>
		/// Provide the Alert message for a given field id.
		/// </summary>
		/// <param name="id">FieldContext ID</param>
		/// <returns>Alert mesasge for FieldContext ID</returns>
		/// 
		string Alert (string id);

		/// <summary>
		/// Add a Processor to the set.
		/// </summary>
		IProcessor AddProcessor { set; }

		/// <summary>
		/// Add a list of Processors to the set.
		/// </summary>
		IList AddProcessors { set; }

		/// <summary>
		/// Obtain the Processor for the given ID; 
		/// there must be a matching Processor for each 
		/// ProcessorID specified by a FieldContext.
		/// </summary>
		/// <param name="id"></param>
		/// <returns></returns>
		IProcessor GetProcessor (string id);

	}
}