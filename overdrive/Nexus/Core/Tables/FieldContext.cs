using System;
using Agility.Core;
using Nexus.Core.Validators;

namespace Nexus.Core.Tables
{
	/// <summary>
	/// Concrete IFieldContext implementation.
	/// </summary>
	[Serializable]
	public class FieldContext : Context, IFieldContext
	{
		public FieldContext() : base()
		{
			ControlTypeName = Tokens.CONTROL_INPUT; // Default
		}

		public string Alert
		{
			get
			{
				string alert = null;
				IProcessor processor = Processor;
				if (processor != null) alert = processor.Alert;
				if (alert == null) alert = this[Tokens.Alert] as string;
				if (alert == null) alert = this[Tokens.Alert] as string;
				return alert;
			}
			set { this[Tokens.Alert] = value; }
		}

		public string ControlTypeName
		{
			get { return this[Tokens.ControlTypeName] as string; }
			set { this[Tokens.ControlTypeName] = value; }
		}

		public string Hint
		{
			get { return this[Tokens.Hint] as string; }
			set { this[Tokens.Hint] = value; }
		}

		public string Help
		{
			get { return this[Tokens.Help] as string; }
			set { this[Tokens.Help] = value; }
		}

		public string ID
		{
			get { return this[Tokens.ID] as string; }
			set { this[Tokens.ID] = value; }
		}

		public string Label
		{
			get
			{
				string label = this[Tokens.Label] as string;
				if (label == null) label = ID;
				return label;
			}
			set { this[Tokens.Label] = value; }
		}

		public IProcessor Processor
		{
			get { return this[Tokens.Processor] as IProcessor; }
			set { this[Tokens.Processor] = value; }
		}

		public string Required
		{
			get { return this[Tokens.Required] as string; }
			set { this[Tokens.Required] = value; }
		}

	}
}