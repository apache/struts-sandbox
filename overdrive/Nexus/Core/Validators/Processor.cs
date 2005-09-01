namespace Nexus.Core.Validators
{
	/// <summary>
	/// Implement common properties.
	/// </summary>
	public abstract class Processor : IProcessor
	{
		private string _Alert;

		public string Alert
		{
			get { return _Alert; }
			set { _Alert = value; }
		}

		private string _DataFormat;

		public string DataFormat
		{
			get { return _DataFormat; }
			set { _DataFormat = value; }
		}

		private string _ID;

		public string ID
		{
			get { return _ID; }
			set { _ID = value; }
		}

		public abstract bool ConvertInput(IProcessorContext incoming);

		public abstract bool FormatOutput(IProcessorContext outgoing);
	}
}