using Agility.Core;
using Nexus.Core;
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
	}
}