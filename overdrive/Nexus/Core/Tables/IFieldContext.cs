using System.Runtime.Serialization;

namespace Nexus.Core.Tables
{
	/// <summary>
	/// Provide properties common to controls, 
	/// including Alert, Constraints, ControlType, and Processor. 
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
		/// Custom error message to display when input validation fails.
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
		/// Provide custom formatting instructions for the value, 
		/// overriding any defaults for the Processor..
		/// </summary>
		/// 
		string DataFormat { get; set; }

		/// <summary>
		/// Identify the processor for this field.
		/// </summary>
		/// 
		string ProcessorID { get; set; }

		/// <summary>
		/// Record the text for a context-sensitive help screen.
		/// </summary>
		/// 
		string Help { get; set; }

		/// <summary>
		/// Record an onscreen or hover hint.
		/// </summary>
		/// 
		string Hint { get; set; }

		/// <summary>
		/// Identify the unique name of this field.
		/// </summary>
		/// 
		string ID { get; set; }

		/// <summary>
		/// Record a label for the control.
		/// </summary>
		/// 
		string Label { get; set; }

	}
}