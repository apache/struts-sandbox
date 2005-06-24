using Nexus.Core;

namespace PhoneBook.Core.Commands
{
	/// <summary>
	/// Base Command for saving an Entity to the persistant store (database).
	/// </summary>
	public class BaseSave : AppCommand
	{
		private string _KeyID = null;
		/// <summary>
		/// The name of the key field.
		/// </summary>
		public string KeyID
		{
			get { return _KeyID; }
			set { _KeyID = value; }
		}

		private string _InsertID = null;
		/// <summary>
		/// The name of the "insert" mapping for the Entity.
		/// </summary>
		public string InsertID
		{
			get { return _InsertID; }
			set { _InsertID = value; }
		}

		private string _UpdateID = null;
		/// <summary>
		/// The name of the "update" mapping for the Entity.
		/// </summary>
		public string UpdateID
		{
			get { return _UpdateID; }
			set { _UpdateID = value; }
		}

		/// <summary>
		/// If the "fieldID" is empty, use the insertID statement, 
		/// otherwise, use the updateID statement.
		/// </summary>
		/// <param name="context">The INexusContext we are processing.</param>
		/// <param name="fieldID">The name of the key field.</param>
		/// <param name="insertID">The name of the "insert" mapping for the Entity.</param>
		/// <param name="updateID">The name of the "update" mapping for the Entity.</param>
		/// <returns>False</returns>
		protected bool Save (IRequestContext context, string fieldID, string insertID, string updateID)
		{
			bool insert = IsEmpty (context [fieldID] as string);

			if (insert)
			{
				context [fieldID] = GuidString ();
				Mapper().Insert (insertID, context);
			}
			else
				Mapper().Update (updateID, context);

			return CONTINUE;
		}

		public override bool RequestExecute (IRequestContext context)
		{
			return Save (context, KeyID, InsertID, UpdateID);
		}

	}
}
