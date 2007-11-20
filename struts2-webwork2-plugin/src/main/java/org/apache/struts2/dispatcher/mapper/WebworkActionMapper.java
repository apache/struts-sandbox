package org.apache.struts2.dispatcher.mapper;

import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.RequestUtils;
import org.apache.struts2.dispatcher.ServletRedirectResult;
import org.apache.struts2.util.PrefixTrie;

import com.opensymphony.xwork2.config.ConfigurationManager;

public class WebworkActionMapper extends DefaultActionMapper {
	static final String METHOD_PREFIX = "method:";
	static final String ACTION_PREFIX = "action:";
	static final String REDIRECT_PREFIX = "redirect:";
	static final String REDIRECT_ACTION_PREFIX = "redirect-action:";

	PrefixTrie prefixTrie = new PrefixTrie() {
		{
			put(METHOD_PREFIX, new ParameterAction() {
				public void execute(String key, ActionMapping mapping) {
					mapping.setMethod(key.substring(METHOD_PREFIX.length()));
				}
			});

			put(ACTION_PREFIX, new ParameterAction() {
				public void execute(String key, ActionMapping mapping) {
					String name = key.substring(ACTION_PREFIX.length());
					int bang = name.indexOf('!');
					if (bang != -1) {
						String method = name.substring(bang + 1);
						mapping.setMethod(method);
						name = name.substring(0, bang);
					}

					mapping.setName(name);
				}
			});

			put(REDIRECT_PREFIX, new ParameterAction() {
				public void execute(String key, ActionMapping mapping) {
					ServletRedirectResult redirect = new ServletRedirectResult();
					redirect.setLocation(key
							.substring(REDIRECT_PREFIX.length()));
					mapping.setResult(redirect);
				}
			});

			put(REDIRECT_ACTION_PREFIX, new ParameterAction() {
				public void execute(String key, ActionMapping mapping) {
					String location = key.substring(REDIRECT_ACTION_PREFIX
							.length());
					ServletRedirectResult redirect = new ServletRedirectResult();
					String extension = getDefaultExtension();
					if (extension != null) {
						location += "." + extension;
					}
					redirect.setLocation(location);
					mapping.setResult(redirect);
				}
			});
		}
	};

	public ActionMapping getMapping(HttpServletRequest request,
			ConfigurationManager manager) {
		ActionMapping mapping = new ActionMapping();
		String uri = getUri(request);

		parseNameAndNamespace(uri, mapping);

		handleSpecialParameters(request, mapping);

		if (mapping.getName() == null) {
			return null;
		}

		// handle "name!method" convention.
		String name = mapping.getName();
		int exclamation = name.lastIndexOf("!");
		if (exclamation != -1) {
			mapping.setName(name.substring(0, exclamation));
			mapping.setMethod(name.substring(exclamation + 1));
		}
		mapping.setParams(new LinkedHashMap(request.getParameterMap()));
		return mapping;
	}

	protected void parseNameAndNamespace(String uri, ActionMapping mapping) {
		String namespace, name;
		int lastSlash = uri.lastIndexOf("/");
		if (lastSlash == -1) {
			namespace = "";
			name = uri;
		} else if (lastSlash == 0) {
			// ww-1046, assume it is the root namespace, it will fallback to
			// default
			// namespace anyway if not found in root namespace.
			namespace = "/";
			name = uri.substring(lastSlash + 1);
		} else {
			namespace = uri.substring(0, lastSlash);
			name = uri.substring(lastSlash + 1);
		}
		mapping.setNamespace(namespace);
		mapping.setName(dropExtension(name));
	}

	String dropExtension(String name) {
		if (extensions == null) {
			return name;
		}
		Iterator it = extensions.iterator();
		while (it.hasNext()) {
			String extension = "." + (String) it.next();
			if (name.endsWith(extension)) {
				name = name.substring(0, name.length() - extension.length());
				return name;
			}
		}
		return null;
	}

	String getUri(HttpServletRequest request) {
		// handle http dispatcher includes.
		String uri = (String) request
				.getAttribute("javax.servlet.include.servlet_path");
		if (uri != null) {
			return uri;
		}

		uri = RequestUtils.getServletPath(request);
		if (uri != null && !"".equals(uri)) {
			return uri;
		}

		uri = request.getRequestURI();
		return uri.substring(request.getContextPath().length());
	}

	interface ParameterAction {
		void execute(String key, ActionMapping mapping);
	}
}
