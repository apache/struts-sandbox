using System;
using System.Text;
using Agility.Core;
using IBatisNet.DataMapper;
using Nexus.Core;
using Spring.Objects.Factory;

namespace PhoneBook.Core.Commands
{
	/// <summary>
	/// Provide an abtract extension of BaseNexusCommand 
	/// with shared members for WNE Commands, 
	/// including a SqlMapper factory method.
	/// </summary>
	/// <remarks><p>
	/// Superclasses must implement a NexusExecute method (inherited from BaseNexusCommand.
	/// </p><p>
	/// Concrete subclasses are expected to be created by a IOC Container, like Spring.NET. 
	/// Most concrete subclasses will be "decorator" classes designed to behave 
	/// differently based on what properties are set when the class is created. 
	/// Others may be custom classes with predefined behaviors. 
	/// </p><p>
	/// The Spring catalog provides the versatility we need to mix-and-match 
	/// base commands with custom commands, as needed, 
	/// and call either from the client in exactly the same way.
	/// </p><p>
	/// NOTE that subclasses should NOT use the Outcome property of 
	/// the Nexus context since they may be subcommands in a Chain. 
	/// All BaseCommands should use the idiom 
	/// <code>context[ID] = object</code>
	/// to store output.
	/// </p><p>
	/// NOTE after using the ReShaper code reformatter, 
	/// the Mapper method needs to be edited. 
	/// ReSharper removes an absolute reference that we actually need. 
	/// (Our Mapper method calls the IBatisNet Mapper method.)
	/// The correct line of code is maintained as a comment, 
	/// so that it can <b>copied</b> over the reformatted version. 
	/// </p></remarks>
	public abstract class BaseCommand : RequestCommand, IObjectNameAware
	{
		/// <summary>
		/// If an ID is not provided, default to the ObjectName.
		/// </summary>
		public override string ID
		{
			get
			{
				string _ID = base.ID;
				if (null == _ID) return ObjectName;
				else return _ID;
			}
			set { base.ID = value; }
		}

		private string _ObjectName;

		/// <summary>
		/// Provide a field for the Spring object name (set by Spring).
		/// </summary>
		public string ObjectName
		{
			get { return _ObjectName; }
			set { _ObjectName = value; }
		}

		/// <summary>
		/// Provide a field for the Remark property.
		/// </summary>
		private string _Remark;

		/// <summary>
		/// Accept an arbitrary comment about a command 
		/// -- more for use in the XML document.
		/// </summary>
		/// 
		public string Remark
		{
			get { return _Remark; }
			set { _Remark = value; }
		}

		/// <summary>
		/// Provide a field for the Mapper property.
		/// </summary>
		/// 
		private SqlMapper _Mapper;

		/// <summary>
		/// Expose a preconfigured SqlMapper instance that Commands can use to run statements.
		/// </summary>
		/// <remarks><p>
		/// Commands use Mapper to invoke SqlMap statements, such as 
		/// <code>
		/// object row = Mapper ().QueryForObject (QueryID, context);
		/// </code>.
		/// </p><p>
		/// Any SqlMapper API method may be called. 
		/// </p><p>
		/// The default behavior of BAseNexusCommand is to use the 
		/// command ID if the QueryID is null.
		/// </p></remarks>
		/// <returns>Preconfigured Mapper</returns>
		/// 
		public SqlMapper Mapper
		{
			get { return _Mapper; }
			set { _Mapper = value; }
		}

		/// <summary>
		/// Indicate whether string is null or zero length.
		/// </summary>
		/// <param name="input">Input to validate</param>
		/// <returns>True if string is nyull or zero length</returns>
		/// 
		protected bool IsEmpty(string input)
		{
			return ((input == null) || (input.Equals(String.Empty)));
		}

		/// <summary>
		/// Create new Global Universal Identifer as a formatted string.
		/// </summary>
		/// <returns>String representing a new GUID</returns>
		/// <remarks><p>
		/// No two calls to this method will ever return duplicate strings.
		/// </p></remarks>
		/// 
		protected string GuidString()
		{
			Guid guid = Guid.NewGuid();
			string gs = guid.ToString();
			return gs;
		}

		/// <summary>
		/// Document the wildcard character used by SQL queries.
		/// </summary>
		public const string WILDCARD = "%";

		/// <summary>
		/// Prepare an attribute for use with a SQL wildcard ("LIKE") operation.
		/// </summary>
		/// <remarks><p>
		/// A wildcard is prepended and appended for a full, scan-across match, 
		/// so that the string will found in any part of the field.
		/// </p></remarks>
		/// <param name="context">Context with attribute to escape</param>
		/// <param name="id">Name of attribute to escape</param>
		protected void LikeMe(IContext context, string id)
		{
			string param = context[id] as string;
			if (null != param)
			{
				StringBuilder sb = new StringBuilder(WILDCARD);
				sb.Append(param);
				sb.Append(WILDCARD);
				context[id] = sb.ToString();
			}
		}
	}
}