namespace Nexus.Core.Validators
{
	/// <summary>
	/// Provide a message Template to use when validation fails [OVR-13].
	/// </summary>
	public interface IValidatorCommand : IRequestCommand
	{
		string Template { get; set; }
		bool Continue { get; set; }
		bool ProcessExecute (IValidatorContext context);

	}
}