using System.Runtime.Serialization;
using Nexus.Core.Validators;

namespace Nexus.Core.Tables
{
	/// <summary>
	/// Provide properties common to controls, 
	/// including Alert, ControlType, and Processor. 
	/// </summary>
	/// <remark><p>
	/// The FieldContext entries are made available through a FieldTable.
	/// The FieldContext members follow XForms terminology. 
	/// </p><p>
	/// XForms [http://www.w3.org/MarkUp/Forms/]. 
	/// XForms Controls [http://www.orbeon.com/ops/doc/processors-xforms].
	/// XPath 2.0 for .NET [http://sourceforge.net/projects/saxondotnet/].
	/// </p><p>
	/// To globalize an application, 
	/// utilize a IFieldContext implementation that supports localization. 
	/// A localized implementation can access a message resource to obtain text and messages, 
	/// rather than a simple property. 
	/// Localized implementations should also dissallow setting the text properties, 
	/// since those values would be provided through a message resource. 
	/// </p><p>
	/// For a localized implementation, see Nexus.Core.Extras.Spring.FieldContext.
	/// </p></remark>
	/// 
	public interface IFieldContext : ISerializable
	{
		/// <summary>
		/// Provide a message to display when input validation fails.
		/// </summary>
		/// <remarks>
		/// If the Processor also provides an Alert, 
		/// the Processor's Alert should be returned instead. 
		/// </remarks>
		/// 
		string Alert { get; set; }

		/// <summary>
		/// Identify name of the default Control Type.
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
		/// Provide text to display for a context-sensitive help screen.
		/// </summary>
		/// 
		string Help { get; set; }

		/// <summary>
		/// Record an onscreen or hover hint.
		/// </summary>
		/// 
		string Hint { get; set; }

		/// <summary>
		/// Identify this field with a unique name.
		/// </summary>
		/// 
		string ID { get; set; }

		/// <summary>
		/// Provide a label for the control.
		/// </summary>
		/// <remarks><p>
		/// If the Label is null, the ID should  be returned instead. 
		/// </p></remarks>
		/// 
		string Label { get; set; }

		/// <summary>
		/// Provide the processor for this field context.
		/// </summary>
		/// 
		IProcessor Processor { get; set; }

		/// <summary>
		/// Provide a message to display when required input is missing.
		/// </summary>
		/// 
		string Required { get; set; }

	}
}