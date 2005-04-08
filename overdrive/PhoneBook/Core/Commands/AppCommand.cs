using IBatisNet.DataMapper;
using Nexus.Core;

namespace PhoneBook.Core.Commands
{
	/// <summary>
	/// Add data access methods to RequestCommand.
	/// </summary>
	public abstract class AppCommand : RequestCommand
	{
		public SqlMapper Mapper ()
		{
			// return IBatisNet.DataMapper.Mapper.Instance();
			return IBatisNet.DataMapper.Mapper.Instance();
		}
	}
}