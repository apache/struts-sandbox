using System.Collections;
using Agility.Core;
using Nexus.Core;
using Nexus.Core.Tables;
using Nexus.Core.Validators;

namespace Agility.Nexus.Validators
{
	/// <summary>
	/// Implement IValidatorContext.
	/// </summary>
	public class ValidatorContext : Context, IValidatorContext
	{
		public ValidatorContext ()
		{
		}

		public ValidatorContext (string key, object source)
		{
			FieldKey = key;
			Source = source;
		}

		public ValidatorContext (string key, IRequestContext context)
		{
			FieldKey = key;
			this [Tokens.Context] = context;
		}

		public string FieldKey
		{
			get { return this [Tokens.FieldKey] as string; }
			set { this [Tokens.FieldKey] = value; }
		}

		public object Source
		{
			get { return this [Tokens.Source]; }
			set { this [Tokens.Source] = value; }
		}

		public object Target
		{
			get { return this [Tokens.Target]; }
			set { this [Tokens.Target] = value; }
		}

		public IRequestContext Context
		{
			get { return this [Tokens.Context] as IRequestContext; }
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