<%@ WebHandler Class="JayrockWeb.PhoneBook"  Language="C#" %>

using System;
using System.Collections;
using Agility.Extras.Spring;
using Jayrock.JsonRpc;
using Jayrock.JsonRpc.Web;
using Nexus.Core;
using Spring.Context;

namespace JayrockWeb
{
    public class PhoneBook : JsonRpcHandler
    {
        protected IRequestCatalog catalog;

        [JsonRpcMethod("last_name_list", Idempotent = true)]
        [JsonRpcHelp("Returns Lastn Name List as an array.")]
        public string[] last_name_list()
        {
            IApplicationContext factory = Objects.Factory();
            catalog = factory.GetObject("Catalog") as IRequestCatalog;
            RequestContext context = (RequestContext) catalog.ExecuteRequest("last_name_list");
            KeyValueList list = context.Outcome as KeyValueList;
            ArrayList names = new ArrayList(list.Count);
            foreach (KeyValue k in list)
            {
                names.Add(k.Value);
            }
            return (string[]) names.ToArray(typeof (String));
        }
    }
}