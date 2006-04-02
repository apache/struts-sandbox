/**
 * Process a JavaScript source file and process special comments
 * to produce an HTML file of documentation, similar to javadoc.
 * @author Norris Boyd
 * @see rhinotip.jar
 * @lastmodified xx
 * @version 1.2 Roland Pennings: Allow multiple files for a function.
 * @version 1.3 Roland Pennings: Removes ../.. from the input directory name
 */

/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * The contents of this file are subject to the Netscape Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/NPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Rhino code, released
 * May 6, 1999.
 *
 * The Initial Developer of the Original Code is Netscape
 * Communications Corporation.  Portions created by Netscape are
 * Copyright (C) 1997-1999 Netscape Communications Corporation. All
 * Rights Reserved.
 *
 * Contributor(s):
 * Norris Boyd
 * Roland Pennings
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the GNU Public License (the "GPL"), in which case the
 * provisions of the GPL are applicable instead of those above.
 * If you wish to allow use of your version of this file only
 * under the terms of the GPL and not to allow others to use your
 * version of this file under the NPL, indicate your decision by
 * deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL.  If you do not delete
 * the provisions above, a recipient may use your version of this
 * file under either the NPL or the GPL.
 */


 
var functionDocArray = [];
var inputDirName = "";
var indexFileArray = [];
var indexFile = "";
var indexFileName = "index_files";
var indexFunctionArray = [];
var indexFunction = "";
var indexFunctionName = "index_functions";
var FileList = [];
var DirList = [];
var outputdir = null;
var debug = 0;



/**
 * Process JavaScript source file <code>f</code>, writing jsdoc to
 * file <code>out</code>.
 * @param f input file
 * @param fname name of the input file (without the path)
 * @param inputdir directory of the input file
 * @param out output file
 */
function processFile(f, fname, inputdir, out) {
	var s;
	var firstLine = true;
	indexFileArray[fname] = "";

    // write the header of the output file
	out.append('<jsdoc><fileName>' + fname + '</fileName>');
	//if (inputdir != null) {
	//  outstr = '<a name=\"_top_\"></a><pre><a href=\"' + indexFile + '\">Index Files</a> ';
	//  outstr += '<a href=\"' + indexFunction + '\">Index Functions</a></pre><hr>';
    //  out.append(outstr);
	//}

    // process the input file
    var lines = f.getLines();
	var comment = "";
    var x = 0;
	while ((s = lines[x++]) != null) {
      var m = s.match(/\/\*\*(.*)/);
	  if (m != null) {
		  // Found a comment start.
		  s = "*" + m[1];
		  do {
			m = s.match(/(.*)\*\//);
			if (m != null) {
			  // Found end of comment.
			  comment += m[1];
			  break;
			}
			// Strip leading whitespace and "*".
			comment += s.replace(/^\s*\*/, "");
			s = lines[x++];
		  } while (s != null);

    	  if (debug)
          print("Found comment " + comment);

		  if (firstLine) {
			// We have a comment for the whole file.
			out.append(processComment(comment,firstLine,fname));
			firstLine = false;
			comment = "";
			continue;
		  }
	  }
	  // match the beginning of the function
	  // NB we also match functions without a comment!
	  // if we have two comments one after another only the last one will be taken
	  m = s.match(/^\s*function\s+((\w+)|(\w+)(\s+))\(([^)]*)\)/);
	  if (m != null)
	  {
			// Found a function start
			var htmlText = processFunction(m[1], m[5], comment); // sjm changed from 2nd to 5th arg
            out.append(htmlText);

			// Save the text in a global variable, so we
			// can write out a table of contents first.
			functionDocArray[functionDocArray.length] = {name:m[1], text:htmlText};

			// Store the function also in the indexFunctionArray
			// so we can have a seperate file with the function table of contents
			if (indexFunctionArray[m[1]]) {
				//  print("ERROR: function: " + m[1] + " is defined more than once!");
				// Allow multiple files for a function
				with (indexFunctionArray[m[1]]) {
					filename = filename + "|" + fname;
					// print("filename = " + filename);
				}
			}
			else {
				indexFunctionArray[m[1]] = {filename:fname};
			}
			//reset comment
			comment = "";
		}
		// match a method being bound to a prototype
	  m = s.match(/^\s*(\w*)\.prototype\.(\w*)\s*=\s*function\s*\(([^)]*)\)/);
	  if (m != null)
	  {
			// Found a method being bound to a prototype.
			var htmlText = processPrototypeMethod(m[1], m[2], m[3], comment);

            out.append(htmlText);
            
			// Save the text in a global variable, so we
			// can write out a table of contents first.
			functionDocArray[functionDocArray.length] = {name:m[1]+".prototype."+m[2], text:htmlText};

			// Store the function also in the indexFunctionArray
			// so we can have a seperate file with the function table of contents
			if (indexFunctionArray[m[1]]) {
				//  print("ERROR: function: " + m[1] + " is defined more than once!");
				// Allow multiple files for a function
				with (indexFunctionArray[m[1]]) {
					filename = filename + "|" + fname;
					// print("filename = " + filename);
				}
			}
			else {
				indexFunctionArray[m[1]] = {filename:fname};
			}
			//reset comment
			comment = "";
		}


		firstLine = false;
	}
    
    /*
    // Write table of contents.
	for (var i=0; i < functionDocArray.length; i++) {
		with (functionDocArray[i]) {
			out.append('function <A HREF=#' + name +
				      '>' + name + '</A><BR>');
		}
	}
	out.append('<HR>');
    

	// Now write the saved function documentation.
	for (i=0; i < functionDocArray.length; i++) {
		with (functionDocArray[i]) {
			out.append('<A NAME=' + name + '>');
			out.append(text);
		}
	}
    */
	out.append('</jsdoc>');

	// Now clean up the doc array
	functionDocArray = [];
}


/**
 * Process function and associated comment.
 * @param name the name of the function
 * @param args the args of the function as a single string
 * @param comment the text of the comment
 * @return a string for the HTML text of the documentation
 */
function processFunction(name, args, comment) {
   if (debug)
    print("Processing " + name + " " + args + " " + comment);
   if (name.charAt(0) != '_') {
        return "<function><name>" + name + "</name>" +
            "<args>" + args + "</args>" +
            processComment(comment,0,name) +
            "</function>";
   } else {
       print("Skipping function "+name);
   }
}

/**
 * Process a method being bound to a prototype.
 * @param proto the name of the prototype
 * @param name the name of the function
 * @param args the args of the function as a single string
 * @param comment the text of the comment
 * @return a string for the HTML text of the documentation
 */
function processPrototypeMethod(proto, name, args, comment) {
   if (debug)
    print("Processing " + proto + ".prototype." + name + " " + args + " " + comment);
   if (name.charAt(0) != '_') {
	return "<prototypeFunction><type>" + proto + "</type><name>" + name + "</name>" +
		"<args>" + args + "</args>" +
		processComment(comment,0,name) +
		"</protytpeFunction>";
   } else {
       print("Skipping prototype function "+name);
   }    
}


/**
 * Process comment.
 * @param comment the text of the comment
 * @param firstLine shows if comment is at the beginning of the file
 * @param fname name of the file (without path)
 * @return a string for the HTML text of the documentation
 */
function processComment(comment,firstLine,fname) {
	var tags = {};
	// Use the "lambda" form of regular expression replace,
	// where the replacement object is a function rather
	// than a string. The function is called with the
	// matched text and any parenthetical matches as
	// arguments, and the result of the function used as the
	// replacement text.
	// Here we use the function to build up the "tags" object,
	// which has a property for each "@" tag that is the name
	// of the tag, and whose value is an array of the
	// text following that tag.
	comment = comment.replace(/@(\w+)\s+([^@]*)/g,
				  function (s, name, text) {
					var a = tags[name] || [];
					a.push(text);
					tags[name] = a;
					return "";
				  });

	// if we have a comment at the beginning of a file
	// store the comment for the index file
	if (firstLine) {
	  indexFileArray[fname] = comment;
	}

	var out = "<comment><text><![CDATA[" + comment + "]]></text>";
	if (tags["param"]) {
		// Create a table of parameters and their descriptions.
		var array = tags["param"];
		for (var i=0; i < array.length; i++) {
			var m = array[i].match(/(\w+)\s+(.*)/);
			out += '<param><name>' + m[1] + '</name>';
            out += '<description>' + m[2] + '</description>';
            out += "</param>";
		}
		
	}
    for (tag in tags) {
        if (tag != "param" && tag != "title") {
            var array = tags[tag];
            if (array != null && array.length > 0) {
                for (var i=0; i < array.length; i++) {
                    out += '<' + tag + '><![CDATA[' + array[i] + ']]></' + tag + '>';
                }
            }
        }
    }
    /*
	if (tags["lastmodified"]) {
	    // Shows a last modified description with client-side js.
	    out += '<DT><B>Last modified:</B><DD>';
		out += '<script><!--\n';
		out += 'document.writeln(document.lastModified);\n';
		out += '// ---></script>\n';
		out += '</DL><P>';
	}
    */
    out += "</comment>";
    if (tags["title"]) {
        out += "<title>" + tags["title"][0] + "</title>";
    }
    if (tags["returnType"]) {
        out += "<type>" + tags["returnType"][0] + "</type>";
    }

	// additional tags can be added here (i.e., "if (tags["see"])...")
	return out;
}

/**
 * Create an html output file
 * @param outputdir directory to put the file
 * @param htmlfile name of the file
*/
function CreateOutputFile(outputdir,htmlfile)
{
  if (outputdir==null)
  {
    var outname = htmlfile;
  }
  else
  {
    var separator = Packages.java.io.File.separator;
    var outname = outputdir + separator + htmlfile.substring(htmlfile.lastIndexOf(separator),htmlfile.length);
  }
  print("output file: " + outname);
  f = new java.io.File(outname);
  f.remove();
  f.getParentFile().mkdirs();
  return f;
}

/**
 * Process a javascript file. Puts the generated HTML file in the outdir
 * @param filename name of the javascript file
 * @inputdir input directory of the file (default null)
 */
function processJSFile(filename,inputdir)
{
  if (debug) print("filename = " + filename + " inputdir = " + inputdir);

  if (!filename.match(/\.js$/)) {
	print("Expected filename to end in '.js'; had instead " +
	  filename + ". I don't treat the file.");
  } else {
    if (inputdir==null)
	{
	  var inname = filename;
    }
	else
	{
      var separator = Packages.java.io.File.separator;
      var inname = inputdir + separator + filename;
    }
    print("Processing file " + inname);

	var f = new java.io.File(inname);

    // create the output file
    var htmlfile = filename.replace(/\.js$/, ".xml");

	var out = CreateOutputFile(outputdir,htmlfile);

    processFile(f, filename, inputdir, out);
  }
}

/**
 * Generate index files containing links to the processed javascript files
 * and the generated functions
 */
function GenerateIndex(dirname)
{
  // construct the files index file
  var out = CreateOutputFile(outputdir,indexFile);

  // write the beginning of the file
  out.append('<jsdoc type="index"><directory>' + dirname + '</directory>');
  var separator = Packages.java.io.File.separator;

  // sort the index file array
  var SortedFileArray = [];
  for (var fname in indexFileArray)
    SortedFileArray.push(fname);
  SortedFileArray.sort();

  for (var i=0; i < SortedFileArray.length; i++) {
    var fname = SortedFileArray[i];
  	//var htmlfile = fname.replace(/\.js$/, ".html");
    out.append('<file><name>' + fname + '</name>');
    
	if (indexFileArray[fname])
	  out.append('<description>' + indexFileArray[fname] + '</description>');
	out.append('</file>\n');
  }
  out.append('</jsdoc>');

  // construct the functions index file
  var out = CreateOutputFile(outputdir,indexFunction);

  // write the beginning of the file
  out.append('<HTML><HEADER><TITLE>Function Index - directory: ' + dirname + '</TITLE><BODY>');
  out.append('<H1>Function Index - directory: ' + dirname + '</H1>\n');
  out.append('<TABLE WIDTH="90%" BORDER=1>');
  out.append('<TR BGCOLOR=0xdddddddd>');
  out.append('<TD><B>Function</B></TD>');
  out.append('<TD><B>Files</B></TD></TR>');

  // sort the function array
  var SortedFunctionArray = [];
  for (var functionname in indexFunctionArray)
    SortedFunctionArray.push(functionname);
  SortedFunctionArray.sort();

  for (var j=0; j < SortedFunctionArray.length; j++) {
    var funcname = SortedFunctionArray[j];
    with (indexFunctionArray[funcname]) {
	 var outstr = '<TR><TD>' + funcname + '</TD><TD>';
	 var filelst = filename.split("|");
	 for (var i in filelst) {
	   var htmlfile = filelst[i].replace(/\.js$/, ".html");
	   outstr += '<A HREF=\"' + htmlfile + '#' + funcname + '\">' + filelst[i] + '</A>&nbsp;';
	 }
	 outstr += '</TD></TR>';
	 out.append(outstr);
    }
  }
  out.append('</TABLE></BODY></HTML>');
}


/**
 * prints the options for JSDoc
*/
function PrintOptions()
{
  print("You can use the following options:\n");
  print("-d: specify an output directory for the generated html files\n");
  print("-i: processes all files in an input directory (you can specify several directories)\n");
  quit();
}

function print(val) {
    java.lang.System.out.println((val == null ? "null" : val));
}


// Main Script
// first read the arguments
if (! arguments)
  PrintOptions();

for (var i=0; i < arguments.length; i++) {
  if (debug) print("argument: + \'" + arguments[i] + "\'");
  if (arguments[i].match(/^\-/)) {
   if (String(arguments[i])=="-d"){
    // output directory for the generated html files

    outputdir = String(arguments[i+1]);
	if (debug) print("outputdir: + \'" + outputdir + "\'");
    //if (outputdir.match(/.*\/|\\$/)) {
    //    outputdir = outputdir.substring(0, outputdir.length);
    //}
    i++;
   }
   else if (String(arguments[i])=="-i"){
    // process all files in an input directory

    DirList.push(String(arguments[i+1]));
if (debug) print("inputdir: + \'" + arguments[i+1] + "\'");
     i++;
   }
   else {
    print("Unknown option: " + arguments[i] + "\n");
	PrintOptions();
   }
  }
  else
  {
    // we have a single file
	if (debug) print("file: + \'" + arguments[i] + "\'");

	FileList.push(String(arguments[i]));
  }
}

// first handle the single files
for (var i in FileList)
  processJSFile(FileList[i],null);

// then handle the input directories
for (var j in DirList) {
  var inputdir = String(DirList[j]);

  print("Process input directory: " + inputdir);

  // clean up index arrays
  var indexFileArray = [];
  var indexFunctionArray = [];

  // for the directory name get rid of ../../ or ..\..\
  inputDirName = inputdir.replace(/\.\.\/|\.\.\\/g,"");

  indexFile = indexFileName + "_" + inputDirName + ".html";
  indexFunction = indexFunctionName + "_" + inputDirName + ".html";

print("indexFile = " + indexFile);
print("indexFunction = " + indexFunction);

  // read the files in the directory
  var DirFile = new java.io.File(inputdir);
  var lst = DirFile.list();
  var separator = Packages.java.io.File.separator;

  for (var i=0; i < lst.length; i++)
  {
    processJSFile(String(lst[i]),inputdir);
  }

  // generate the index files for the input directory
  //GenerateIndex(inputDirName);
}



