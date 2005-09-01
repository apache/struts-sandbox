namespace Nexus.Core.Validators
{
	/// <summary>
	/// Convert or Format a standard or custom DataType.
	/// </summary>
	public interface IProcessor
	{
		string Alert { get; set; }
		string DataFormat { get; set; }
		string ID { get; set; }

		bool ConvertInput(IProcessorContext incoming);
		bool FormatOutput(IProcessorContext outgoing);
	}
}