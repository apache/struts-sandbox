using System.Collections;
using System.Runtime.Serialization;
using Agility.Core;
using Nexus.Core.Validators;

namespace Nexus.Core.Tables
{
	/// <summary>
	/// Inventory of fields used by the application.
	/// </summary>
	/// 
	public interface IFieldTable : IContext, ISerializable
	{
		/// <summary>
		/// Add a field to the set. 
		/// </summary>
		/// 
		IFieldContext AddField { set; }

		/// <summary>
		/// Add a list of fields to the set.
		/// </summary>
		/// 
		IList AddFields { set; }

		/// <summary>
		/// Indicate whether this FieldTable must include all fields, 
		/// including strings fields [FALSE].
		/// </summary>
		/// 
		bool Strict { set; get; }

		/// <summary>
		/// Provide the Alert message for a given field id.
		/// </summary>
		/// <param name="id">FieldContext ID</param>
		/// <returns>Alert mesasge for FieldContext ID</returns>
		/// 
		string Alert (string id);

		/// <summary>
		/// Convert and copy values from the Criteria into the main Context, 
		/// utilitizing the FieldContext settings.
		/// </summary>
		/// <param name="context">Context to process (including Criteria)</param>
		/// <returns>True if nominal</returns>
		/// 
		bool Convert_Execute (IValidatorContext context);

		/// <summary>
		/// Format and copy values from the main Context into the Criteria,
		/// utilizing the FieldContext settings.
		/// </summary>
		/// <param name="context">Context to process (including Criteria)</param>
		/// <returns>True if nominal</returns>
		/// 
		bool Format_Execute (IValidatorContext context);

		/// <summary>
		/// Obtain the FieldContext for the given ID, observering Strict setting.
		/// </summary>
		/// <param name="id">FieldContext ID</param>
		/// <returns>FieldContext for ID</returns>
		/// 
		IFieldContext GetField (string id);
	}
}