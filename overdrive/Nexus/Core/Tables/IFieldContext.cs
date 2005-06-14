using System;
using System.Runtime.Serialization;

namespace Nexus.Core.Tables
{
	/// <summary>
	/// Properties common to controls, including Alert, Constraints, 
	/// ControlType, and DataType. 
	/// </summary>
	/// <remark><p>
	/// Validation commands can use the FieldContext properties to verify 
	/// input. 
	/// The FieldContext entries are made available through a FieldTable.
	/// </p></remark>
	/// 
	public interface IFieldContext : ISerializable
	{
		/// <summary>
		/// Intitial error message to display when Constraints fail.
		/// </summary>
		/// 
		string Alert { get; set; }

		/// <summary>
		/// Cannonical name of the Control Type (input, select, et al.).
		/// </summary>
		/// 
		string ControlTypeName { get; set; }

		/// <summary>
		/// Formatting instructions for the value, according to DateType.
		/// </summary>
		/// 
		string DataFormat { get; set; }

		/// <summary>
		/// Type of native data - Boolean, Byte Char, DateTime, Decimal, 
		/// Double, Int16, Int32, Int64, SByte, Single, String, TimeSpan, 
		/// UInt16, UInt32, UInt64.
		/// </summary>
		/// 
		Type DataType { get; set; }

		/// <summary>
		/// Cannonical name of the DataType.
		/// </summary>
		/// 
		string DataTypeName { get; set; }

		/// <summary>
		/// Hint - Gets or sets an onscreen or hover hint.
		/// </summary>
		/// 
		string Hint { get; set; }

		/// <summary>
		/// Name of the control (also dictionary key).
		/// </summary>
		/// 
		string ID { get; set; }

		/// <summary>
		/// the label for the control.
		/// </summary>
		/// 
		string Label { get; set; }

	}
}