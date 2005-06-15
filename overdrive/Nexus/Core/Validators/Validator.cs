using System;
using System.Collections;
using Agility.Nexus.Validators;
using Nexus.Core.Tables;

namespace Nexus.Core.Validators
{
	/// <summary>
	/// Provide a base class to use when implementating Validators. 
	/// </summary>
	/// <remarks>
	/// Subclasses must provide a NexusExecute method.
	/// </remarks>
	public class Validator : RequestCommand, IValidatorCommand
	{
		#region Properties

		private string _Template = null;
		public virtual string Template
		{
			get { return _Template; }
			set { _Template = value; }
		}

		private bool _Continue = false;
		public virtual bool Continue
		{
			get { return _Continue; }
			set { _Continue = value; }
		}

		public const bool MODE_INPUT = false;
		public const bool MODE_OUTPUT = true;

		private bool _Mode = MODE_INPUT;
		public virtual bool Mode
		{
			get { return _Mode; }
			set { _Mode = value; }
		}

		#endregion

		public ICollection CombinedIDs (IRequestContext context)
		{
			IDictionary combined = new Hashtable ();
			IList relatedIDs = context.CommandBin.RelatedIDs; // outer list
			if (relatedIDs != null) foreach (string i in relatedIDs) combined [i] = i;
			IList requiredIDs = context.CommandBin.RequiredIDs; // inner list
			if (requiredIDs != null) foreach (string i in requiredIDs) combined [i] = i;
			return combined.Keys;
		}

		#region ProcessRelated

		public void AssertProcessRelated (IRequestContext context)
		{
			AssertProcessRequired (context);

			IFieldTable table = context.FieldTable;
			if (null == table)
				throw new ArgumentNullException ("FieldTable", "BaseValidator.NexusExecute.AssertProcessRelated");
		}

		public virtual void ProcessRelated (IRequestContext context, bool mode)
		{
			AssertProcessRelated (context);

			IDictionary fields = context.Criteria;
			IFieldTable table = context.FieldTable;
			ICollection related = CombinedIDs (context);
			foreach (string key in related)
			{
				bool have = false;
				bool okay = false;

				switch (mode)
				{
					case MODE_INPUT:
						{
							have = (fields.Contains (key));
							if (have)
							{
								string source = fields [key] as string;
								// TODO: Spring?
								IValidatorContext _context = new ValidatorContext (key, source);
								okay = table.Convert (_context);
								if (okay)
									// set to main context
									context [key] = _context.Target;
							}
							break;
						}

					case MODE_OUTPUT:
						{
							have = (context.Contains (key));
							if (have)
							{
								object source = context [key];
								// TODO: Spring?
								IValidatorContext _context = new ValidatorContext (key, source);
								okay = table.Format (_context);
								if (okay)
									// set to field buffer
									fields [key] = _context.Target;
							}
							break;
						}
				}

				if ((have) && (!okay))
					context.AddAlertForField (key);

			} // end while		
		}

		#endregion

		#region ProcessRequired

		public void AssertProcessRequired (IRequestContext context)
		{
			IDictionary criteria = context.Criteria;
			if (null == criteria)
				throw new ArgumentNullException ("Criteria", "BaseValidator.NexusExecute.AssertProcessRequired");
		}

		public virtual void ProcessRequired (IRequestContext context, bool mode)
		{
			IList requiredIDs = context.CommandBin.RequiredIDs; // inner list
			if (requiredIDs == null) return;

			IList runtimeIDs = context.CommandBin.RuntimeIDs; // inner list
			if (runtimeIDs != null)
			{
				IEnumerator runtime = runtimeIDs.GetEnumerator ();
				while (runtime.MoveNext ())
				{
					string id = runtime.Current as string;
					requiredIDs.Remove (id);
				}
			}

			IEnumerator required = requiredIDs.GetEnumerator ();
			while (required.MoveNext ())
			{
				string id = required.Current as string;
				bool okay = (context.Contains (id) && (null != context [id]) && (!String.Empty.Equals (context [id].ToString ())));
				if (!okay)
				{
					string message = context.FormatTemplate (Template, id);
					context.AddAlert (message, id);
				}
			}
		}

		#endregion 

		public override bool RequestExecute (IRequestContext context)
		{
			ProcessRelated (context, Mode);
			ProcessRequired (context, Mode);
			if (Continue) return CONTINUE;
			return context.HasAlerts;
		}

	}
}