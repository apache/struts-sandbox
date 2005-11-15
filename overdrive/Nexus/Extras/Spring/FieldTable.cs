using Nexus.Core.Tables;
using Spring.Context;

namespace Nexus.Extras.Spring
{
	/// <summary>
	/// Companion to Nexus.Extras.Spring.FieldContext.
	/// </summary>
	public class FieldTable : Core.Tables.FieldTable
	{
		private IMessageSource _MessageSource;

		/// <summary>
		/// Identify the message source for this FieldTable.
		/// </summary>
		/// <exception cref="System.InvalidOperationException">
		/// If the context has not been initialized yet.
		/// </exception>
		public IMessageSource MessageSource
		{
			get { return _MessageSource; }
			set { _MessageSource = value; }
		}

		public override IFieldContext NewFieldContext(string id)
		{
			FieldContext field = new FieldContext();
			field.ID = id;
			field.MessageSource = this.MessageSource;
			return field;
		}

	}
}