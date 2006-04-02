importPackage(Packages.xjavadoc);
importClass(java.io.File);
importClass(Packages.xjavadoc.filesystem.FileSourceSet);

function processFile(xclass, out) {

    // write the header of the output file
    doc = xclass.doc;
    out.append("<jsdoc>");
    out.append('<fileName>' + out.name + '</fileName>');
    out.append("<title>Variable '"+doc.getTag("jsname").value+"'</title>");
    out.append(processComment(doc));
    
    var scriptable = false;
    for (x in xclass.interfaces) {
        var inf = xclass.interfaces[x];
        if (inf.name.equals("Scriptable")) {
            scriptable = true;
            break;
        }
    }
    for (x in xclass.methods) {
        xmethod = xclass.methods[x];
        
        if (xmethod.name.startsWith('jsGet_') && scriptable) {
            name = xmethod.name.substring(6);
            out.append(processProperty(name, xmethod));
        } else if (xmethod.name.startsWith('jsFunction_') && scriptable) {
            name = xmethod.name.substring(11);
            out.append(processMethod(name, xmethod));
        } else if (!scriptable) {
            out.append(processMethod(xmethod.name, xmethod));
        }
    }    
	out.append('</jsdoc>');
}


function processProperty(name, xmethod) {
    var doc = xmethod.doc;
        
	return "<property><name>" + name + "</name>" +
        "<type>" + xmethod.returnType.type.name + "</type>" +
		processComment(doc) +
        "</property>";
}

function processMethod(name, xmethod) {
    var doc = xmethod.doc;
        
	out = "<function><name>" + name + "</name>";
    
    out += "<type>" + xmethod.returnType.type.name + "</type>";
    out += "<args>";
    var params = xmethod.parameters;
    for (p in params) {
        var param = params[p];
        out += param.type + " " + param.name;
        if (p != params.length - 1) {
            out += ", ";
        }
    }
    out += "</args>"; 
    out += processComment(doc) + "</function>";
    return out;
}

function processComment(doc) {

	var out = "<comment><text><![CDATA[" + doc.commentText + "]]></text>";
    out += "<firstSentence><![CDATA[" + doc.firstSentence + "]]></firstSentence>";
    
    tags = doc.getTags("param");
    for (t in tags) {
        tag = tags[t];
		var m = new String(tag.value).match(/(\w+)\s+(.*)/);
		out += '<param><name>' + m[1] + '</name>';
        out += '<description><![CDATA[' + m[2] + ']]></description>';
        out += "</param>";
    }
    
    for (t in doc.tags) {
        tag = doc.tags[t];
        if (tag.name != "param") {
            out += '<' + tag.name + '><![CDATA[' + tag.value + ']]></' + tag.name + '>';
        }
    }
    out += "</comment>";
	return out;
}

function createOutputFile(outputdir,htmlfile)
{
    var separator = Packages.java.io.File.separator;
    var outname = outputdir + separator + htmlfile.substring(htmlfile.lastIndexOf(separator),htmlfile.length);
  print("output file: " + outname);
  var f = new java.io.File(outname);
  f.remove();
  f.getParentFile().mkdirs();
  return f;
}

function processFiles(xjavadoc, dir, outputdir) {
    var f;
    files = dir.listFiles();
    for (f=0; f<files.length; f++) {
        name = new String(files[f].getName());
        if (name.match(/\.java$/) != null) {
            name = name.replace(/[\/\\]/g, ".");
            name = name.substring(name.indexOf("org.apache"), name.indexOf(".java"));
            //print("looking up class: "+name);
            xclass = xjavadoc.getXClass(name);
            if (xclass.doc.hasTag("jsname")) {
                var xmlfile = xclass.name + ".xml";
                
                var out = createOutputFile(outputdir,xmlfile);
                processFile(xclass, out);
            }
        }
    }
    
    /* Doesn't seem to work correctly
    print("leng:"+xjavadoc.getSourceClasses().size());
    i = xjavadoc.sourceClasses.iterator();
    while (i.hasNext()) {
        xclass = i.next();
        print("class:"+xclass.name);
        if (xclass.getDoc().hasTag("jsname")) {
        
            // create the output file
            var xmlfile = xclass.name + ".xml";
        
            var out = createOutputFile(outputdir,xmlfile);
            processFile(xclass, out);
        }
    }
    */
}


function print(val) {
    java.lang.System.out.println((val == null ? "null" : val));
}


// Main Script
// first read the arguments

xjavadoc = new XJavaDoc();
dir = new java.io.File(arguments[0]);
fs = new FileSourceSet(dir);
xjavadoc.addSourceSet(fs);

outputdir = new File(arguments[1]);

processFiles(xjavadoc, dir, outputdir);



