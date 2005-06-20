/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
using System.Collections;

namespace PhoneBook.Core
{
	/// <summary>
	/// Adapt an IDictionary to a properties class 
	/// so that there is a public property for every UI attribute 
	/// exposed by the application.
	/// </summary>
	/// 
	public class AppContext : Hashtable
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
			get { return this [App.FIRST_NAME] as string; }
			set { this [App.FIRST_NAME] = value; }
		}

		public string last_name
		{
			get { return this [App.LAST_NAME] as string; }
			set { this [App.LAST_NAME] = value; }
		}

		public string extension
		{
			get { return this [App.EXTENSION] as string; }
			set { this [App.EXTENSION] = value; }
		}

		public string user_name
		{
			get { return this [App.USER_NAME] as string; }
			set { this [App.USER_NAME] = value; }
		}

		public string hired
		{
			get { return this [App.HIRED] as string; }
			set { this [App.HIRED] = value; }
		}

		public string hours
		{
			get { return this [App.HOURS] as string; }
			set { this [App.HOURS] = value; }
		}

	}
}