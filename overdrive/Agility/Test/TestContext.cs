namespace Agility.Core
{
	/// <summary>
	/// Summary description for TestContext.
	/// </summary>
	public class TestContext : Context
	{
		private string _InputKey = "input";

		public string InputKey
		{
			get { return _InputKey; }
			set { _InputKey = value; }

		}

		private string _OutputKey = "output";

		public string OutputKey
		{
			get { return _OutputKey; }
			set { _OutputKey = value; }
		}
	}
}