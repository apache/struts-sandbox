using Agility.Core;
using Nexus.Core.Validators;

namespace Agility.Nexus.Validators
{
	/// <summary>
	/// Concrete IValidatorContext implementation.
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

		private const string _FieldKey = "_FieldKey";
		public string FieldKey
		{
			get { return this [_FieldKey] as string; }
			set { this [_FieldKey] = value; }
		}

		private const string _Source = "_Source";
		public object Source
		{
			get { return this [_Source]; }
			set { this [_Source] = value; }
		}

		private const string _Target = "_Target";
		public object Target
		{
			get { return this [_Target]; }
			set { this [_Target] = value; }
		}
	}
}