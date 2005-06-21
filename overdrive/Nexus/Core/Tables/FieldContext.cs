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
			ControlTypeName = Tokens.INPUT_CONTROL; // Default
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

		public Type DataType
		{
			get
			{
				Type v = this [Tokens.DataType] as Type;
				if (v==null) v = typeof(String);
				return v;
			}
			set
			{
				this [Tokens.DataType] = value;
				this [Tokens.DataTypeName] = value.FullName;
			}
		}

		public string DataTypeName
		{
			get { return this [Tokens.DataTypeName] as string; }
			set
			{
				this [Tokens.DataType] = Type.GetType (value);
				this [Tokens.DataTypeName] = value;
			}
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