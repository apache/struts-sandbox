<%@ WebHandler Class="JayrockWeb.PhoneBook"  Language="C#" %>

using System;
using System.Collections;
using System.Configuration;
using Agility.Extras.Spring;
using Jayrock.JsonRpc;
using Jayrock.JsonRpc.Web;
using Nexus.Core;
using Nexus.Core.Helpers;
using PhoneBook.Core;
using Spring.Context;

namespace JayrockWeb
{
    public class PhoneBook : JsonRpcHandler
    {

        private IRequestCatalog catalog = null;

        private IRequestCatalog GetCatalog()
        {
            if (catalog == null)
            {
                IApplicationContext factory = Objects.Factory();
                catalog = factory.GetObject(App.CATALOG_KEY) as IRequestCatalog;
                if (catalog == null) 
                    throw new ConfigurationErrorsException("PhoneBook: GetCatalog()==null");
            }
            return catalog;
        }
        
        private RequestContext Execute(string command)
        {
            return (RequestContext) GetCatalog().ExecuteRequest(command);            
        }
        
        [JsonRpcMethod(App.LAST_NAME_LIST, Idempotent = true)]
        [JsonRpcHelp("Returns Last Name List as an array.")]
        public string[] last_name_list()
        {            
            RequestContext context = Execute(App.LAST_NAME_LIST);
            KeyValueList list = context.Outcome as KeyValueList;
            if (list == null) return null; // FIXME: Better error handling
            
            ArrayList names = new ArrayList(list.Count);
            foreach (KeyValue k in list)
            {
                names.Add(k.Value);
            }
            return (string[]) names.ToArray(typeof (String));
        }

        [JsonRpcMethod(App.ENTRY_LIST, Idempotent = true)]
        [JsonRpcHelp("Returns the complete directory as an array of formatted IDictionary objects.")]
        public AppEntryList entry_list()
        {
            IViewHelper helper = GetCatalog().GetHelperFor(App.ENTRY_LIST);
            helper.Execute();
            // if helper.IsNominal ... // FIXME: Better error handling
            AppEntryList list = helper.Outcome as AppEntryList;
            return list;
        }

        [JsonRpcMethod(App.ENTRY, Idempotent = true)]
        [JsonRpcHelp("Returns an entry by key.")]
        public AppEntry entry(string key)
        {
            IViewHelper helper = GetCatalog().GetHelperFor(App.ENTRY);
            helper.Criteria[App.ENTRY_KEY] = key;
            helper.Execute();
            // if helper.IsNominal ... // FIXME: Better error handling
            AppEntry entry = new AppEntry(helper.Criteria);
            return entry;
        }

        [JsonRpcMethod(App.ENTRY_SAVE, Idempotent = true)]
        [JsonRpcHelp("Saves the entry, insert or updating as appropriate.")]
        public AppEntry entry_save(IDictionary input)
        {
            IViewHelper helper = GetCatalog().GetHelperFor(App.ENTRY_SAVE);
            helper.Read(input,true);
            helper.Execute();
            // if helper.IsNominal ... // FIXME: Better error handling
            AppEntry entry = new AppEntry(helper.Criteria);
            return entry;
        }

        [JsonRpcMethod(App.ENTRY_DELETE, Idempotent = true)]
        [JsonRpcHelp("Deletes an entry by key.")]
        public AppEntry entry_delete(string key)
        {
            IViewHelper helper = GetCatalog().GetHelperFor(App.ENTRY_DELETE);
            helper.Criteria[App.ENTRY_KEY] = key;
            helper.Execute();
            // if helper.IsNominal ... // FIXME: Better error handling
            AppEntry entry = new AppEntry(helper.Criteria);
            return entry;
        }

    
    }
}