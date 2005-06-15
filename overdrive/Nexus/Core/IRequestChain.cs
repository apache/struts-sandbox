using Agility.Core;

namespace Nexus.Core
{
	/// <summary>
	/// Composite IChain and IRequestCommand.
	/// </summary>
	public interface IRequestChain : IChain, IRequestCommand
	{
	}
}