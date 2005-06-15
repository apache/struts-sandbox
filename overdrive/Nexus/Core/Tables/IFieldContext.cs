using System;
using System.Runtime.Serialization;

namespace Nexus.Core.Tables
{
	/// <summary>
	/// Provide properties common to controls, 
	/// including Alert, Constraints, ControlType, and DataType. 
	/// </summary>
	/// <remark><p>
	/// Validation commands can use the FieldContext properties 
	/// to verify input. 
	/// The FieldContext entries are made available through a FieldTable.
	/// The FieldContext members follow XForms terminology. 
	/// </p>
	/// <p>
	/// XForms [http://www.w3.org/MarkUp/Forms/]. 
	/// XForms Controls [http://www.orbeon.com/ops/doc/processors-xforms].
	/// XPath 2.0 for .NET [http://sourceforge.net/projects/saxondotnet/].
	/// </p>
	/// </remark>
	/// 
	public interface IFieldContext : ISerializable
	{
		/// <summary>
		/// Intitial error message to display when Constraints fail.
		/// </summary>
		/// 
		string Alert { get; set; }

		/// <summary>
		/// Name for the default Control Type.
		/// </summary>
		/// <remarks><p>
		/// Standard control types are: input, secret, textarea, select1, select, submit, upload.
		/// </p><p>
		/// XForms distinguishes between Lists, Radio Buttons, and CheckBoxes through additional 
		/// parameters. For now, all three can be identified as select1 or select.
		/// </p></remarks>
		/// 
		string ControlTypeName { get; set; }

		/// <summary>
		/// Formatting instructions for the value, according to DateType.
		/// </summary>
		/// 
		string DataFormat { get; set; }

		/// <summary>
		/// Type of native data.
		/// </summary>
		/// <remarks>
		/// Standard data types are: Boolean, Byte Char, DateTime, Decimal, 
		/// Double, Int16, Int32, Int64, SByte, Single, String, TimeSpan, 
		/// UInt16, UInt32, UInt64
		/// </remarks>
		/// 
		Type DataType { get; set; }

		/// <summary>
		/// Cannonical name of the DataType.
		/// </summary>
		/// 
		string DataTypeName { get; set; }

		/// <summary>
		/// Help - Gets or sets text for a context-sensitive help screen.
		/// </summary>
		/// 
		string Help { get; set; }

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
		/// The label for the control.
		/// </summary>
		/// 
		string Label { get; set; }

	}
}