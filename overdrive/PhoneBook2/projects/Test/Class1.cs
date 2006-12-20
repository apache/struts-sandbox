using PhoneBook2.Core;
using NUnit.Framework;

namespace PhoneBook2.Core
{
    [TestFixture]
    public class Class1Fixture
    {

        Class1 class1;
        
        [SetUp]
        public void SetUp()
        {
            class1 = new Class1();            
        }
        
        [Test]
        public void Class1Test ()
        {
            Assert.IsNotNull(class1,"SetUp failed!");
        }
    }
}
