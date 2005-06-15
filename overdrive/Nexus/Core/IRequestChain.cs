using Agility.Core;

namespace Nexus.Core
{
	/// <summary>
	/// Composite IChain with IRequestCommand.
	/// </summary>
	public interface IRequestChain: IChain, IRequestCommand
	{
	}
}
