using System.Collections;
using System.Runtime.Serialization;
using Agility.Core;
using Nexus.Validators;

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
		/// If true, requires that all fields, including strings fields, 
		/// be specified [FALSE].
		/// </summary>
		/// 
		bool Strict { set; get; }

		/// <summary>
		/// For a given field id, return the Alert message.
		/// </summary>
		/// <param name="id">FieldContext ID</param>
		/// <returns>Alert mesasge for FieldContext ID</returns>
		/// 
		string Alert (string id);

		/// <summary>
		/// Utilitizing the FieldContext settings, convert and copy values 
		/// from the Criteria into the main Context.
		/// </summary>
		/// <param name="context">Context to process (including Criteria)</param>
		/// <returns>True if nominal</returns>
		/// 
		bool Convert (IValidatorContext context);

		/// <summary>
		/// Utilizing the FieldContext settings, format and copy values from 
		/// the main Context into the Criteria.
		/// </summary>
		/// <param name="context">Context to process (including Criteria)</param>
		/// <returns>True if nominal</returns>
		/// 
		bool Format (IValidatorContext context);

		/// <summary>
		/// Determine if the control is a simple value or a rich 
		/// control, like a drop down list. 
		/// </summary>
		/// <param name="name">ID for Control</param>
		/// <returns>True if control is a multivalue control,
		/// like a list</returns>
		/// 
		bool IsRichControl (string name);

		/// <summary>
		/// Obtains the FieldContext for the given ID.
		/// </summary>
		/// <param name="id">FieldContext ID</param>
		/// <returns>FieldContext for ID</returns>
		/// 
		IFieldContext Get (string id);
	}
}