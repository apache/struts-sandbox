<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@taglib prefix="s" uri="/struts-tags" %>

<div id="left-nav">
	<h3><a href="#">Spring</a></h3>
	<div>
		<ul>
            <li><s:a namespace="/crud" action="person-list">CRUD Application</s:a></li>
		</ul>
	</div>
	<h3><a href="#">Validation</a></h3>
	<div>
		<ul>
            <li><s:a namespace="/crud" action="person-list">CRUD Application</s:a></li>
		</ul>
	</div>
	<h3><a href="#">Convention</a></h3>
	<div>
		<ul>
            <li><s:a namespace="/crud" action="person-list">CRUD Application</s:a></li>
		</ul>
	</div>
	<h3><a href="#">Section 4</a></h3>
	<div>
		<p>
		Cras dictum. Pellentesque habitant morbi tristique senectus et netus
		et malesuada fames ac turpis egestas. Vestibulum ante ipsum primis in
		faucibus orci luctus et ultrices posuere cubilia Curae; Aenean lacinia
		mauris vel est.
		</p>
		<p>
		Suspendisse eu nisl. Nullam ut libero. Integer dignissim consequat lectus.
		Class aptent taciti sociosqu ad litora torquent per conubia nostra, per
		inceptos himenaeos.
		</p>
	</div>
</div>
