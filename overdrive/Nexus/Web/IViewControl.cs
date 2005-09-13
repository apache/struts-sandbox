using System;
using System.Collections;
using System.Web.UI;
using Nexus.Core.Helpers;

namespace Nexus.Web
{
	public interface IViewControl
	{

		/// <summary>
		/// Set Labels and TextBoxes to an empty string 
		/// to ensure inappropriate values are not carried over.
		/// </summary>
		/// 
		void ResetControls();
		
		IViewHelper GetHelperFor(string command);
		IViewHelper Execute(string command);

		void Bind(ControlCollection controls, IDictionary dictionary);
		void Bind(IDictionary dictionary);

		void ExecuteBind(ControlCollection controls, IViewHelper helper);
		void ExecuteBind(IViewHelper helper);

		IViewHelper ExecuteBind(ControlCollection controls, string command);
		IViewHelper ExecuteBind(string command);
		
		IViewHelper Read(string command, bool nullIfEmpty);
		IViewHelper Read(string command);
		
		IViewHelper ReadExecute(string command, bool nullIfEmpty);
		IViewHelper ReadExecute(string command);

		IViewHelper ReadExecute(ControlCollection collection, string command, bool nullIfEmpty);
		IViewHelper ReadExecute(ControlCollection collection, string command);

		IViewHelper Read(string command, IDictionary criteria, bool nullIfEmpty);
		IViewHelper ReadExecute(string command, IDictionary criteria, bool nullIfEmpty);
		IViewHelper ReadExecute(string command, IDictionary criteria);

	}
}