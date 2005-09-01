namespace Nexus.Core.Validators
{
	public class CollectionProcessor : Processor
	{
		public override bool ConvertInput(IProcessorContext incoming)
		{
			incoming.Target = incoming.Source;
			return true;
		}

		public override bool FormatOutput(IProcessorContext outgoing)
		{
			outgoing.Target = outgoing.Source;
			return true;
		}

	}

	#region Notes

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
}