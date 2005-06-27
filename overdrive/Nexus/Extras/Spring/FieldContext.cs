using System;
using Agility.Core;
using Nexus.Core;
using Nexus.Core.Tables;
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
	/// In this implementation, the text properties -- Alert, Hint, Help, and Label -- are read-only 
	/// and cannot be accessed with a MessageSource property. 
	/// When the property is read, 
	/// the method loods for a message resource that shares the same  ID as the FieldContext,
	/// but with a "_property" suffix (_alert, _hint, _help, _label). 
	/// So, if the FieldContext ID is LastName, then reading its Alert will look for a message 
	/// resource named "LastName_alert". 
	/// If the resource is not found, 
	/// then the FieldContext ID (e.g. "LastName") is quietly returned instead. 
	/// </p></remarks>
	[Serializable]
	public class FieldContext : Context, IFieldContext
	{
		public FieldContext () : base ()
		{
			ControlTypeName = Tokens.CONTROL_INPUT; // Default
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

		public string ID
		{
			get { return this [Tokens.ID] as string; }
			set { this [Tokens.ID] = value; }
		}
		public string ProcessorID
		{
			get { return this [Tokens.DataTypeID] as string; }
			set { this [Tokens.DataTypeID] = value; }
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
			get{ return _MessageSource; }
			set{ _MessageSource = value; }
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
		/// <param name="defaultValue">The value to return on error.</param>
		/// <returns>A resolved message or the defaultValue if the message could not be located for any reason.</returns>
		private string GetMessageOrDefault(string name, string defaultValue)
		{
			string _name = null;
			try
			{
				_name = GetMessage(name);
			}
			catch (Exception e)
			{
				e = e; // silly assignment
				_name = defaultValue;
			}
			return _name;
		}

		private string GetText(string root, string suffix)
		{
			return GetMessageOrDefault(root + suffix, root);
		}

		public string Alert
		{
			get { return GetText(this.ID,"_alert"); }
			set { throw new NotSupportedException(); }
		}

		public string Hint
		{
			get { return GetText(this.ID,"_hint"); }
			set { throw new NotSupportedException(); }
		}

		public string Help
		{
			get { return GetText(this.ID,"_help"); }
			set { throw new NotSupportedException(); }
		}

		public string Label
		{
			get { return GetText(this.ID,"_label"); }
			set { throw new NotSupportedException(); }
		}

		#endregion

	}
}