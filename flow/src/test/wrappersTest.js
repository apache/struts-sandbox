function testMapIndex() {
  map = new java.util.HashMap();
  map.put("foo", "bar");
  assertTrue("brackets didn't work", map["foo"] == "bar");
  assertTrue("dot notation didn't work", map.foo == "bar");
  assertTrue("normal map get() didn't work", map.get("foo") == "bar");
}

function testMapFuncPropCollide() {
  map = new java.util.HashMap();
  map.put("foo", "bar");
  map.put("size", "100");
  assertTrue("Size not right", map.size() == 2);
  assertTrue("Size property not called", map.get("size") == "100");
}

function testBeanIndex() {
  bean = new Packages.org.apache.commons.beanutils.LazyDynaBean();
  bean.set("foo", "bar");
  bean["jim"] = "bar";
  bean.sara = "friend";
  
  assertTrue("set didn't work", bean["foo"] == "bar");
  assertTrue("dot notation didn't work", bean.jim == "bar");
  assertTrue("normal map get() didn't work", bean.get("sara") == "friend");
  
  jimFound = false;
  for (x in bean) {
      if (x == "jim") jimFound = true;
  }
  assertTrue("jim not found in for..in", jimFound);
}

function testDynaBeanFuncPropCollide() {
  bean = new Packages.org.apache.commons.beanutils.LazyDynaBean();
  bean.set("foo", "bar");
  bean.set("get", "100");
  assertTrue("Get function shouldn't be overridden", bean.get("foo") == "bar");
  assertTrue("Get property not called", bean.get("get") == "100");
}


function testListIndex() {
  list = new java.util.ArrayList();
  list.add("foo");
  list.add("bar");
  assertTrue("Index didn't work", list[0] == "foo");
}

function testListForIn() {
  list = new java.util.ArrayList();
  list.add("foo");
  list.add("bar");
  count = 0;
  for (x in list) {
      if (x == 0) assertTrue("first value wrong", list[x] == "foo");
      if (x == 1) assertTrue("second value wrong", list[x] == "bar");
  }
}
