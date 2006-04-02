var errors = new Array();
var failures = new Array();
var successes = new Array();

function assertTrue(msg, test) {
  if (test == undefined) {
    test = msg;
    msg = "Test failed";
  }
  if (test != true) {
      this.result = failures.push(msg+" - "+this.curTest);
  } else {
      successes.push(this.curTest);
  }
}

function runTests(name) {
    
    for (id in this) {
        if (id.length > 4 && id.substr(0, 4) == 'test') {
            try {
                if (this.setUp != null) {
                    this.setUp();
                }
                this.curTest = id.substr(4);
                this[id]();
            } catch (e) {
                errors.push(this.curTest + " - "+e+" line:"+e.lineNumber);
            } finally {
                if (this.tearDown != null) {
                    this.tearDown();
                }
            }
        }
    }
    
    printResults(name)
}

function printResults(name) {
    print("\n"+name + " Test Results");
    print("===============");
    print("Successes  - "+successes.length);
    for (x in successes) {
        print("\t"+successes[x]);
    }
    print("Failures   - "+failures.length);
    for (x in failures) {
        print("\t"+failures[x]);
    }
    print("Errors     - "+errors.length);
    for (x in errors) {
        print("\t"+errors[x]);
    }    
}

function print(val) {
    java.lang.System.out.println((val == null ? "null" : val));
}

  
  
