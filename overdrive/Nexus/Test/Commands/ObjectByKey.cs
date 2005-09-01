using System;

namespace Nexus.Core.Commands
{
	/// <summary>
	/// Sample model command that adds a result object into the context 
	/// if the correct key is passed.
	/// </summary>
	public class ObjectByKey : RequestCommand
	{
		public const string PK_SOMETHING = "pk_something";
		public const string PK_SOMETHING_VALUE = "12345678-1234-1234-1234-123456789ABC";
		public const string PK_SOMETHING_RESULT = "SomethingResult";
		public const string PK_SOME_DATE = "SomeDate";

		public override bool RequestExecute(IRequestContext context)
		{
			string value = context[PK_SOMETHING] as string;
			// IList list = Mapper.Get ().QueryForObject (ID, key);
			if (PK_SOMETHING_VALUE.Equals(value))
			{
				context[PK_SOMETHING_RESULT] = PK_SOMETHING_RESULT;
				context[PK_SOME_DATE] = DateTime.Now;
			}
			return CONTINUE;
		}

	}
}