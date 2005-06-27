using System;
using Agility.Core;

namespace Nexus.Core.Tables
{
	/// <summary>
	/// Concrete IFieldContext implementation.
	/// </summary>
	[Serializable]
	public class FieldContext : Context, IFieldContext
	{
		public FieldContext () : base ()
		{
			ControlTypeName = Tokens.CONTROL_INPUT; // Default
		}

		public string Alert
		{
			get { return this [Tokens.Alert] as string; }
			set { this [Tokens.Alert] = value; }
		}

		public string ControlTypeName
		{
			get { return this [Tokens.ControlTypeName] as string; }
			set { this [Tokens.ControlTypeName] = value; }
		}

		public string DataFormat
		{
			get { return this [Tokens.DataFormat] as string; }
			set { this [Tokens.DataFormat] = value; }
		}

		public string ProcessorID
		{
			get { return this [Tokens.DataTypeID] as string; }
			set { this [Tokens.DataTypeID] = value; }
		}

		public string Hint
		{
			get { return this [Tokens.Hint] as string; }
			set { this [Tokens.Hint] = value; }
		}

		public string Help
		{
			get { return this [Tokens.Help] as string; }
			set { this [Tokens.Help] = value; }
		}

		public string ID
		{
			get { return this [Tokens.ID] as string; }
			set { this [Tokens.ID] = value; }
		}


		public string Label
		{
			get { return this [Tokens.Label] as string; }
			set { this [Tokens.Label] = value; }
		}

	}
}