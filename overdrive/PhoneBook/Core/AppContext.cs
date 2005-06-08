using System;
using System.Collections;
using Nexus.Core;

namespace PhoneBook.Core
{
	/// <summary>
	/// Summary description for AppContext.
	/// </summary>
	public class AppContext: Hashtable
	{

		#region Constructors

		/// <summary>
		/// Instantiate with zero parameters.
		/// </summary>
		public AppContext ()
		{
		}

		/// <summary>
		/// Instantiate from a IDictionary.
		/// </summary>
		/// <param name="dictionary">Values for new object</param>
		public AppContext (IDictionary dictionary)
		{
			IEnumerator keys = dictionary.Keys.GetEnumerator ();
			while (keys.MoveNext ())
			{
				string key = keys.Current as string;
				this.Add (key, dictionary [key].ToString ());
			}
		}

		/*
		/// <summary>
		/// Instantiate from an IDictionary, 
		/// formatting each entry using the FieldTable from a INexusContext, 
		/// and reporting any conversion or formatting errors in the INexusContext.
		/// </summary>
		/// <remarks><p>
		/// The result of a query will come back as a list of IDictionaries, 
		/// using native, unformatted data types. 
		/// This constructor can be used to loop through a list of IDictionaires, 
		/// create a AppContext for each entry, and formatting any values 
		/// along the way. (Dates being the best example.) 
		/// The result is a AppContextList that can be used as a DataGrid 
		/// DataSource (or whatever). 
		/// </p></remarks>
		/// <param name="dictionary">Values for new object</param>
		/// <param name="context">Context with FieldTable and error handler</param>
		public AppContext (IDictionary dictionary, IRequestContext context)
		{
			#region Assert parameters

			if (null == dictionary) throw new ArgumentNullException ("dictionary", "AppContext(IDictionary,INexusContext");
			if (null == context) throw new ArgumentNullException ("context", "AppContext(IDictionary,INexusContext");
			IFieldTable table = context.FieldTable;
			if (null == table) throw new ArgumentNullException ("FieldTable", "AppContext(IDictionary,INexusContext");

			#endregion

			IEnumerator keys = dictionary.Keys.GetEnumerator ();
			while (keys.MoveNext ())
			{
				string key = keys.Current as string;
				IValidatorContext input = new ValidatorContext (); // ISSUE: Spring? [WNE-63]
				input.FieldKey = key;
				input.Source = dictionary [key];
				bool okay = table.Format (input);
				if (!okay)
					// OR, do we just want to push convert/format(id) up?
					context.AddAlertForField (key);
				this.Add (key, input.Target);
			}
		}
		*/
		#endregion
		
		
		/*
		public string property
		{
			get { return this[App.PROPERTY] as string; }
			set { this[App.PROPERTY] = value; }
		}
		*/

		public string first_name
		{
			get { return this[App.FIRST_NAME] as string; }
			set { this[App.FIRST_NAME] = value; }
		}

		public string last_name
		{
			get { return this[App.LAST_NAME] as string; }
			set { this[App.LAST_NAME] = value; }
		}

		public string extension
		{
			get { return this[App.EXTENSION] as string; }
			set { this[App.EXTENSION] = value; }
		}

		public string user_name
		{
			get { return this[App.USER_NAME] as string; }
			set { this[App.USER_NAME] = value; }
		}

		public string hired
		{
			get { return this[App.HIRED] as string; }
			set { this[App.HIRED] = value; }
		}

		public string hours
		{
			get { return this[App.HOURS] as string; }
			set { this[App.HOURS] = value; }
		}

	}
}
