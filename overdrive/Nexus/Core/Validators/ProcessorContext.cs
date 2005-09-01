using System.Collections;
using Agility.Core;
using Nexus.Core;
using Nexus.Core.Tables;
using Nexus.Core.Validators;

namespace Agility.Nexus.Validators
{
	/// <summary>
	/// Implement IProcessorContext.
	/// </summary>
	public class ProcessorContext : Context, IProcessorContext
	{
		public ProcessorContext()
		{
		}

		public ProcessorContext(string key, object source)
		{
			FieldKey = key;
			Source = source;
		}

		public ProcessorContext(string key, IRequestContext context)
		{
			FieldKey = key;
			this[Tokens.Context] = context;
		}

		public string FieldKey
		{
			get { return this[Tokens.FieldKey] as string; }
			set { this[Tokens.FieldKey] = value; }
		}

		public object Source
		{
			get { return this[Tokens.Source]; }
			set { this[Tokens.Source] = value; }
		}

		public object Target
		{
			get { return this[Tokens.Target]; }
			set { this[Tokens.Target] = value; }
		}

		public IRequestContext Context
		{
			get { return this[Tokens.Context] as IRequestContext; }
		}

		public IDictionary Criteria
		{
			get { return Context.Criteria; }
		}

		public IFieldTable FieldTable
		{
			get { return Context.FieldTable; }
		}
	}
}