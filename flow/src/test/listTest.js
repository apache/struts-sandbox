function setUp() {
    this.list = new java.util.ArrayList();
    this.list.add("foo");
    this.list.add("bar");
}

function testEach() {
  foo = false;
  bar = false;
  list.each(function(val) { eval(val+" = true;");});
  assertTrue("Didn't iterate over both", foo == true && bar == true);
}

function testAsImmutable() {
    lst = list.asImmutable();
    passed = false;
    try {
        lst.add("foo");
    } catch (e) {
        passed = true;
    }
    assertTrue("Shouldn't have allowed an add", passed);
}

function testPop() {
  val = list.pop();
  assertTrue("Wrong top value", val == "bar");
  assertTrue("Didn't reduce size", list.size() == 1);
}

function testSort() {
  list.sort();
  assertTrue("Didn't sort, bar should be first", list[0] == "bar");
}

function testSortEach() {
  list.sortEach(function(val1, val2) {
    if (val1 == "bar") return -1;
    else return 1;
   });
  assertTrue("Didn't sort, bar should be first", list[0] == "bar");
}

function testLength() {
    assertTrue("Length should be 2", list.length == 2);   
}

function testFind() {
  result = list.find(function(val) { return (val == "bar" ? val : null);});
  assertTrue("Didn't find bar", result == "bar");
}

function testFindAll() {
  list.add("bar");
  result = list.findAll(function(val) { return (val == "bar" ? val : null);});
  assertTrue("Didn't find bars", result.size() == 2);
}

