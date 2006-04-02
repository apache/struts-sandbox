importPackage(Packages.xjavadoc);
importClass(java.io.File);
importClass(Packages.xjavadoc.filesystem.FileSourceSet);

function processFile(xclass, out) {

    // write the header of the output file
    doc = xclass.doc;
    out.append("<jsdoc>");
    out.append('<fileName>' + out.name + '</fileName>');
    out.append("<title>"+doc.getTag("targetClass").value+" Extensions</title>");
    out.append(processComment(doc));
    for (x in xclass.methods) {
        xmethod = xclass.methods[x];
        if (xmethod.doc.hasTag("propReturn")) {
            out.append(processProperty(xmethod.name, xmethod));
        } else if (xmethod.doc.hasTag("funcReturn")) {
            out.append(processMethod(xmethod.name, xmethod));
        }
    }    
	out.append('</jsdoc>');
}


function processProperty(name, xmethod) {
    var doc = xmethod.doc;
        
	return "<property><name>" + name + "</name>" +
        "<type>" + doc.getTag("propReturn").value + "</type>" +
		processComment(doc) +
        "</property>";
}

function processMethod(name, xmethod) {
    var doc = xmethod.doc;
        
	out = "<function><name>" + name + "</name>";
    
    out += "<type>" + doc.getTag("funcReturn").value + "</type>";
    out += "<args>" + doc.getTag("funcParams").value + "</args>"
    out += processComment(doc) + "</function>";
    return out;
}

function processComment(doc) {

	var out = "<comment><text><![CDATA[" + doc.commentText + "]]></text>";
    out += "<firstSentence><![CDATA[" + doc.firstSentence + "]]></firstSentence>";
    
    tags = doc.getTags("funcParam");
    for (t in tags) {
        tag = tags[t];
		var m = new String(tag.value).match(/(\w+)\s+(.*)/);
		out += '<param><name>' + m[1] + '</name>';
        out += '<description><![CDATA[' + m[2] + ']]></description>';
        out += "</param>";
    }
    
    for (t in doc.tags) {
        tag = doc.tags[t];
        out += '<' + tag.name + '><![CDATA[' + tag.value + ']]></' + tag.name + '>';
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
            if (xclass.doc.hasTag("targetClass")) {
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



