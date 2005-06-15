using Nexus.Core;

namespace Nexus.Core.Validators
{
	/// <summary>
	/// A IValidator command is a NexusCommand that provides a message Template to use 
	/// when validation fails.
	/// </summary>
	public interface IValidatorCommand : IRequestCommand
	{
		string Template { get; set; }
		bool Continue { get; set; }
	}
}