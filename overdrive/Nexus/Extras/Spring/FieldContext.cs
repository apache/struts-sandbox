using System;
using Agility.Core;
using Nexus.Core;
using Nexus.Core.Tables;
using Nexus.Core.Validators;
using Spring.Context;

namespace Nexus.Extras.Spring
{
	/// <summary>
	/// Concrete IFieldContext implementation that uses Spring MessageSource to resolve text .
	/// </summary>
	/// <remarks><p>
	/// This implementation *requires* that a Spring MessageSource be exposed to each the FieldContext member. 
	/// The simplest way to do that is by using a base FieldContext in the Spring configuration file. 
	/// The MessageSource property can be set once in the base and inherited by the others. 
	/// </p><p>
	/// In this implementation, 
	/// the text properties -- Alert, Hint, Help, Label, and Required -- are read-only 
	/// and cannot be accessed with a MessageSource property. 
	/// When the property is read, 
	/// the method looks for a message resource that shares the same  ID as the FieldContext,
	/// but with a "_property" suffix (_alert, _hint, _help, _label, _required). 
	/// So, if the FieldContext ID is LastName, then reading its Alert will look for a message 
	/// resource named "LastName_alert". 
	/// </p><p>
	/// In the case of an Alert, a message may also be provided by the Processor. 
	/// If so, then the Processor message supercedes the default message. 
	/// In this way, you can set a default for all the controls, 
	/// and then override the default for specific processors.
	/// </p><p>
	/// To provide a default Alert or Required to use when no other is provided, 
	/// provide a Message Resource entry in the form: "_alert" or "_required".
	/// </p><p>
	/// If a Label message is not found, the FieldContext ID is returned instead. 
	/// Otherwise, if no message is found, 
	/// then the FieldContext ID and property tag (e.g. "LastName_required") 
	/// is returned. 
	/// </p></remarks>
	/// 
	[Serializable]
	public class FieldContext : Context, IFieldContext
	{
		public FieldContext() : base()
		{
			ControlTypeName = Tokens.CONTROL_INPUT; // Default
		}

		public string ControlTypeName
		{
			get { return this[Tokens.ControlTypeName] as string; }
			set { this[Tokens.ControlTypeName] = value; }
		}

		public string ID
		{
			get { return this[Tokens.ID] as string; }
			set { this[Tokens.ID] = value; }
		}

		public IProcessor Processor
		{
			get { return this[Tokens.Processor] as IProcessor; }
			set { this[Tokens.Processor] = value; }
		}

		#region text properties

		private IMessageSource _MessageSource;

		/// <summary>
		/// Identify the message source for this FieldContext.
		/// </summary>
		/// <exception cref="System.InvalidOperationException">
		/// If the context has not been initialized yet.
		/// </exception>
		public IMessageSource MessageSource
		{
			get { return _MessageSource; }
			set { _MessageSource = value; }
		}

		/// <summary>
		/// Resolve the message.
		/// </summary>
		/// <param name="name">The name of the resource to get.</param>
		/// <returns>
		/// The resolved message if the lookup was successful. Otherwise, it either throws
		/// an exception or returns the resource name, depending on the implementation.
		/// </returns>
		private string GetMessage(string name)
		{
			return MessageSource.GetMessage(name);
		}

		/// <summary>
		/// Resolve the message or return null.
		/// </summary>
		/// <param name="name">The name of the resource to get.</param>
		/// <returns>A resolved message or null if the message could not be located for any reason.</returns>
		private string GetMessageOrNull(string name)
		{
			string _name = null;
			try
			{
				_name = GetMessage(name);
			}
			catch (Exception e)
			{
				e = e; // silly assignment
				_name = null;
			}
			return _name;
		}

		/// <summary>
		/// Return the message for the Processor, 
		/// or the message for the FieldContext, 
		/// or the message for the suffix,
		/// or the FieldContext ID and suffix verbatim, 
		/// if all else fails.
		/// </summary>
		/// <param name="root">FieldContext ID</param>
		/// <param name="suffix">Message type</param>
		/// <returns>A message or the root+suffix</returns>
		private string GetText(string root, string suffix)
		{
			string text = null;
			IProcessor processor = Processor;
			string id = null;
			if (processor != null) id = processor.ID;
			if (id != null) text = GetMessageOrNull(id + suffix);
			if (text == null)
			{
				text = GetMessageOrNull(root + suffix);
			}
			if (text == null)
			{
				text = GetMessageOrNull(suffix);
			}
			if (text == null) text = root + suffix;

			return text;
		}

		public string Alert
		{
			get { return GetText(this.ID, "_alert"); }
			set { throw new NotSupportedException(); }
		}

		public string Hint
		{
			get { return GetText(this.ID, "_hint"); }
			set { throw new NotSupportedException(); }
		}

		public string Help
		{
			get { return GetText(this.ID, "_help"); }
			set { throw new NotSupportedException(); }
		}

		public string Label
		{
			get
			{
				string label = GetMessageOrNull(this.ID + "_label");
				if (label == null) label = ID;
				return label;
			}
			set { throw new NotSupportedException(); }
		}

		public string Required
		{
			get { return GetText(this.ID, "_required"); }
			set { throw new NotSupportedException(); }
		}

		#endregion
	}
}