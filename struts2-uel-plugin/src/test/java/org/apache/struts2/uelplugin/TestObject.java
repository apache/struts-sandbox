package org.apache.struts2.uelplugin;

import java.util.Date;
import java.util.Map;


public class TestObject {
        private String value;
        private int age;
        private Date date;
        private TestObject inner;
        private Map parameters;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public TestObject getInner() {
            return inner;
        }

        public void setInner(TestObject inner) {
            this.inner = inner;
        }

        public Map getParameters() {
            return parameters;
        }

        public void setParameters(Map parameters) {
            this.parameters = parameters;
        }
    }
