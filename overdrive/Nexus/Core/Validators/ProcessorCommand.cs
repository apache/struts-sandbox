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
	public abstract class ProcessorCommand : RequestCommand, IProcessorCommand
	{
		#region IRequestCommand 

		public override bool RequestExecute (IRequestContext context)
		{
			ProcessRelated (context);
			ProcessRequired (context);
			return context.HasAlerts;
		}

		#endregion

		#region IProcessorCommand

		private string _Template = null;
		public virtual string Required
		{
			get { return _Template; }
			set { _Template = value; }
		}

		/// <summary>
		/// Convert input for fields that do not have a Processor.
		/// </summary>
		/// <remarks>
		/// The default behavior is to pass through the objects, verbatim.
		/// </remarks>
		/// <param name="context">The IProcessorContext</param>
		public virtual bool ConvertInput (IProcessorContext context)
		{
			context.Target = context.Source;
			return true;
		}

		public virtual bool ExecuteConvert (IProcessorContext context)
		{
			bool okay = false;
			string id = context.FieldKey;
			IFieldTable table = context.FieldTable;
			IFieldContext fieldContext = table.GetFieldContext (id); // enforces Strict

			if ((fieldContext == null))
			{
				ConvertInput (context);
				return true;
			}

			string processorID = fieldContext.ProcessorID;
			if (processorID == null)
				okay = ConvertInput (context);
			else
			{
				IProcessor processor = table.GetProcessor (fieldContext.ProcessorID);
				okay = processor.ConvertInput (context);
			}
			return okay;
		}

		/// <summary>
		/// Format output for fields that do not have a Processor.
		/// </summary>
		/// <remarks>
		/// The default behavior is to pass through ICollection types 
		/// and call ToString on everything else.
		/// </remarks>
		/// <param name="context">The IProcessorContext</param>
		public virtual bool FormatOutput (IProcessorContext context)
		{
			Type sourceType = context.Source.GetType ();
			if (IsCollectionType (sourceType)) context.Target = context.Source;
			else context.Target = context.Source.ToString ();
			return true;
		}

		public virtual bool ExecuteFormat (IProcessorContext context)
		{
			bool okay = false;
			string id = context.FieldKey;
			object source = context.Source;
			IFieldTable table = context.FieldTable;
			IFieldContext fieldContext = table.GetFieldContext (id); // Enforces Strict

			if ((fieldContext == null))
			{
				if (source == null)
					context.Target = null;
				else okay = FormatOutput (context);
				return true;
			}

			string processorID = fieldContext.ProcessorID;
			if (processorID == null)
				okay = FormatOutput (context);
			else
			{
				IProcessor processor = table.GetProcessor (fieldContext.ProcessorID);
				okay = processor.FormatOutput (context);
			}
			return okay;
		}

		private bool IsCollectionType (Type dataType)
		{
			bool v = (typeof (ICollection)).IsAssignableFrom (dataType);
			return (v);
		}

		public abstract bool ExecuteProcess (IProcessorContext context);

		#endregion

		#region ProcessRequired

		private void AssertProcessRequired (IRequestContext context)
		{
			IDictionary criteria = context.Criteria;
			if (null == criteria)
				throw new ArgumentNullException ("Criteria==null", "BaseValidator.NexusExecute.AssertProcessRequired");
		}

		private void ProcessRequired (IRequestContext context)
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
					string message = context.FormatTemplate (Required, id);
					context.AddAlert (message, id);
				}
			}
		}

		#endregion 

		#region ProcessRelated

		private void AssertProcessRelated (IRequestContext context)
		{
			AssertProcessRequired (context);

			IFieldTable table = context.FieldTable;
			if (null == table)
				throw new ArgumentNullException ("FieldTable==null", "BaseValidator.NexusExecute.AssertProcessRelated");
		}

		private void ProcessRelated (IRequestContext context)
		{
			AssertProcessRelated (context);

			ICollection related = CombinedIDs (context);
			foreach (string key in related)
			{
				IProcessorContext _context = new ProcessorContext (key, context);
				ExecuteProcess (_context);
			}
		}

		private ICollection CombinedIDs (IRequestContext context)
		{
			IDictionary combined = new Hashtable ();
			IList relatedIDs = context.CommandBin.RelatedIDs; // outer list
			if (relatedIDs != null) foreach (string i in relatedIDs) combined [i] = i;
			IList requiredIDs = context.CommandBin.RequiredIDs; // inner list
			if (requiredIDs != null) foreach (string i in requiredIDs) combined [i] = i;
			return combined.Keys;
		}

		#endregion
	}
}