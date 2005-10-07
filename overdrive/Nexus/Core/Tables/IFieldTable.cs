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
		/// Factory method to create a stub context 
		/// when field is required and Strict is false.
		/// </summary>
		/// <remarks><p>
		/// The IFieldContext instance should provide a default for Required.
		/// </p></remarks>
		/// <param name="id">The fieldname</param>
		/// <returns>New default context for ID</returns>
		IFieldContext NewFieldContext(string id);

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
		IFieldContext GetFieldContext(string id);

		/// <summary>
		/// Add a Processor to the set.
		/// </summary>
		IProcessor AddProcessor { set; }

		/// <summary>
		/// Add a list of Processors to the set.
		/// </summary>
		IList AddProcessors { set; }

		/// <summary>
		/// Provide the Alert message for a given field id.
		/// </summary>
		/// <param name="id">FieldContext ID</param>
		/// <returns>Alert message for FieldContext ID</returns>
		/// 
		string Alert(string id);

		/// <summary>
		/// Provide the Label message for a given field id.
		/// </summary>
		/// <param name="id">FieldContext ID</param>
		/// <returns>Alert message for FieldContext ID</returns>
		/// 
		string Label(string id);

		/// <summary>
		/// Provide the Required message for a given field id.
		/// </summary>
		/// <param name="id">FieldContext ID</param>
		/// <returns>Required message for FieldContext ID</returns>
		/// 
		string Required(string id);

	}
}