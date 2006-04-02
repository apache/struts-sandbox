function setUp() {
    this.file = java.io.File.createTempFile("test", ".tmp");
}

function testAppend() {
  file.append("this is a test");
  txt = new java.io.BufferedReader(new java.io.FileReader(file)).readLine();
  assertTrue("Didn't append text - "+txt, txt == "this is a test");
}

function testGetText() {
  writer = new java.io.FileWriter(file);
  writer.write("foo\nbar");
  writer.close();
  txt = file.getText();
  assertTrue("Didn't get text - "+txt, txt == "foo\nbar");
}

function testGetLines() {
  writer = new java.io.FileWriter(file);
  writer.write("foo\nbar");
  writer.close();
  txt = file.getLines();
  assertTrue("Wrong number of lines - "+txt.length, txt.length == 2);
  assertTrue("Wrong second line - "+txt[1], txt[1] == "bar");
}

function testRemove() {
  writer = new java.io.FileWriter(file);
  writer.write("foo\nbar");
  writer.close();
  ret = file.remove();
  assertTrue("File should be removed", ret);
  assertTrue("File should really be removed", !file.exists());
}

function testEachLine() {
  writer = new java.io.FileWriter(file);
  writer.write("foo\nbar");
  writer.close();
  foo = false;
  bar = false;
  file.eachLine(function(line) {eval(line+" = true");});
  assertTrue("Didn't read both lines", foo == true && bar == true);
}


