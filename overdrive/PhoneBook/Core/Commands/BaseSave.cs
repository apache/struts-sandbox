using Nexus.Core;

namespace PhoneBook.Core.Commands
{
	/// <summary>
	/// Store an entity for future reference.
	/// </summary>
	/// 
	public class BaseSave : BaseMapper
	{
		/// <summary>
		/// Provide a field for KeyID property.
		/// </summary>
		/// 
		private string _KeyID = null;

		/// <summary>
		/// Record the unique identifier for the entity.
		/// </summary>
		/// 
		public string KeyID
		{
			get { return _KeyID; }
			set { _KeyID = value; }
		}

		/// <summary>
		/// Provide a field for InsertID property.
		/// </summary>
		private string _InsertID = null;

		/// <summary>
		/// Record the name of the "insert" mapping for the entity.
		/// </summary>
		/// 
		public string InsertID
		{
			get { return _InsertID; }
			set { _InsertID = value; }
		}

		/// <summary>
		/// Provide a field for UpdateID property.
		/// </summary>
		/// 
		private string _UpdateID = null;

		/// <summary>
		/// Record the name of the "update" mapping for the entity.
		/// </summary>
		/// 
		public string UpdateID
		{
			get { return _UpdateID; }
			set { _UpdateID = value; }
		}

		/// <summary>
		/// Insert or update an entity to the persistent store. 
		/// </summary>
		/// <remark><p>
		/// If the "fieldID" is empty, use the insertID statement, 
		/// otherwise, use the updateID statement.
		/// </p></remark>
		/// <param name="context">The INexusContext we are processing.</param>
		/// <param name="fieldID">The name of the key field.</param>
		/// <param name="insertID">The name of the "insert" mapping for the Entity.</param>
		/// <param name="updateID">The name of the "update" mapping for the Entity.</param>
		/// <returns>False</returns>
		/// 
		protected bool Save(IRequestContext context, string fieldID, string insertID, string updateID)
		{
			bool insert = IsEmpty(context[fieldID] as string);

			if (insert)
			{
				context[fieldID] = GuidString();
				Mapper.Insert(insertID, context);
			}
			else
				Mapper.Update(updateID, context);

			return CONTINUE;
		}

		public override bool RequestExecute(IRequestContext context)
		{
			return Save(context, KeyID, InsertID, UpdateID);
		}

	}
}