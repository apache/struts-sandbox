using Agility.Core;

namespace Nexus.Validators
{
	/// <summary>
	/// Encapsulate values needed by standard IValidatorCommands [OVR-13].
	/// </summary>
	/// 
	public interface IValidatorContext : IContext
	{
		/// <summary>
		/// The identifer for the field under validation.
		/// </summary>
		/// 
		string FieldKey { get; set; }

		/// <summary>
		/// The source value that we to process.
		/// </summary>
		/// 
		object Source { get; set; }

		/// <summary>
		/// The target value after conversion or formatting.
		/// </summary>
		/// 
		object Target { get; set; }
	}
}