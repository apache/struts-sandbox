using System;
using System.Collections;
using Nexus.Core;
using PhoneBook.Core;

namespace PhoneBook.Core.Commands
{
	/// <summary>
	/// Run the select query indicated by QueryID, 
	/// first escaping any LikeIDs and extending any date ranges,
	/// and return the result as an IList, 
	/// </summary>
	public class BaseList : BaseCommand
	{
		private IList _LikeIDs = null;

		/// <summary>
		/// Names of key fields to escape for a "like" search.
		/// </summary>
		public IList LikeIDs
		{
			get { return _LikeIDs; }
			set { _LikeIDs = value; }
		}

		/// <summary>
		/// Generate an array from the IDs.
		/// </summary>
		/// <returns>Array of LikeIDs</returns>
		protected string[] GetArray(string IDs)
		{
			string csv = IDs;
			bool noLike = ((null == csv) || (string.Empty.Equals(csv)));
			if (noLike)
				return new string[0];
			else
			{
				string[] _Array = csv.Split(',');
				return _Array;
			}
		}

		/// <summary>
		/// Document the last second of the day 
		/// so as to calculate values for concepts like today and tomorrow.
		/// </summary>
		private string LAST_TICK = " 23:59";

		/// <summary>
		/// If ThruDate is set to the minimum time (00:00), 
		/// set to the maximum time instead (23:59). 
		/// </summary>
		/// <param name="context"></param>
		private void ExtendThruDate(IRequestContext context)
		{
			bool have = context.Contains(App.THRU_DATE);
			if (!have) return;
			DateTime thruDate = DateTime.MinValue;
			try
			{
				thruDate = (DateTime) context[App.THRU_DATE];
			}
			catch (Exception e)
			{
				if (e != null) return; // Placate Resharper
			}
			bool defaultTime = (thruDate.Hour == 0) && (thruDate.Minute == 0) && (thruDate.Second == 0) &&
				(thruDate.Millisecond == 0);
			if (defaultTime)
			{
				string strThruDate = thruDate.ToShortDateString() + LAST_TICK;
				DateTime newThruDate = Convert.ToDateTime(strThruDate);
				context.Remove(App.THRU_DATE);
				context.Add(App.THRU_DATE, newThruDate);
			}
		}

		/// <summary>
		/// Prepare special attributes for the query.
		/// </summary>
		/// <param name="context">The context we are processing</param>
		protected void PreProcess(IRequestContext context)
		{
			// Escape any "like" fields
			IList likes = LikeIDs;
			if (likes != null)
			{
				IEnumerator e = likes.GetEnumerator();
				while (e.MoveNext())
					LikeMe(context, e.Current as string);
			}

			// Extend any "thru_date" to midnight
			ExtendThruDate(context);

			// Ensure that list_item and list_offset are numeric
			object limit = context[App.ITEM_LIMIT];
			if (limit != null)
			{
				context[App.ITEM_LIMIT] = Convert.ToInt32(limit);
				object offset = context[App.ITEM_OFFSET];
				context[App.ITEM_OFFSET] = Convert.ToInt32(offset);
			}
		}

		/// <summary>
		/// Provide a default, convenience implementation that will run a "QueryForList" 
		/// using a data mapping statement of the same ID as the Command; 
		/// override as needed.
		/// </summary>
		/// <param name="context">The INexusContext we are processing.</param>
		/// <returns>CONTINUE, if an Exception is not thrown.</returns>
		public override bool RequestExecute(IRequestContext context)
		{
			PreProcess(context);

			IList rows = Mapper.QueryForList(QueryID, context);
			context[ID] = rows;
			return CONTINUE;
		}
	}
}