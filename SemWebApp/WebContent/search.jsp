<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="servlets.ServletSearch" %>
<%@ page import="QueryConverter.QueryConverter" %>

<%@ page import="java.util.Iterator" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.json.JSONArray" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link href="https://fonts.googleapis.com/css?family=Quicksand:300" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Testing Sparql</title>
    <META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
    <style>
      body, html {
        margin:0;
        padding: 0;
      }

      .navbar {
      	position: fixed;
      	width: 100%;
      	height: 60px;
      	background-color: #515151;
        border-bottom: #9b9b9b 1px solid;
        z-index: 1;
      }
      #div-logo {
      	float: left;
        width: 50px;
        margin-left: 30px;
      }
      #logo-font {
      	font-family: 'Quicksand', sans-serif;
        color:white;
        font-size: 45px;
      }
      .footer {
        width: 100%;
        height: 60px;;
      	background-color: #515151;
        border-top: #9b9b9b 1px solid;
        font-size: 20px !important;
      }
      #footer-font {
      	font-family: 'Quicksand', sans-serif;
        color:white;
        font-size: 20px;
        display: block;
        margin-left: auto;
        margin-right: auto;
      }

	  .proper_ansswer_div {
	  	background-color: #90ed7b;
	  	border-left: 2px solid green;
	  	border-right: 2px solid green;

	  }

      .content {
        position: relative;
        background-color: #ffffff;
        width: 80%;
        min-height: 600px;
        padding-bottom: 35px;
        margin: auto;
      }
      .searchFormContainer {
      	margin-top: 7px;
        position: absolute;
        margin-left: auto;
        margin-right:auto;
        /* flaot:left */
        width: 488px;
        /*background-color: red;*/
      }
      #searchSubmit {
        padding: 0;
        border: #515151 2px solid;
        background-color: white;
        width: 80px;
        height: 44px;
        margin-top: 10px;
        margin: auto;
        font-family: 'Quicksand', sans-serif;
        font-size: 20px;
      }
      #searchSubmit:hover {
        background-color: #515151;
        cursor: pointer;
        color: white;

      }
      #searchField {
        background-color: white;
        border: #515151 2px solid;
        width: 400px;
        height: 40px;
        padding: 0;
        font-size: 20px;
        font-family: 'Quicksand', sans-serif;
      }
      #resultsContainer {
        width: 75%;
        min-height: 100%;
        /* border: 1px solid lightgrey; */

        background-color: f7f7f7;
        margin-left: auto;
        margin-right: auto;
        padding-top:70px;
      }

      .resultListFactual {
      	list-style-type:none;
      	padding:none;
      	margin:none;

      }
      .resultListFactual li{
      	padding: 10px;
      	border-left: 5px solid #40bc5d;
      	border-right: 5px solid #40bc5d;
        background: rgba(221,255,216,0.2);
      	min-height: 80px;
      }
      .resultListFactual li:hover {
      	background-color: #b9d3b1;
      }
      .resultImageContainer {

      	padding:5px;
        height: 100%;
        float: left;
      }
      .resultTextContainer {
        float: left;
        padding-left: 10px;
      }
      .resultImage {
      	width: 50px;
      	height: 80px;
      }
      .resultHeading {
        font-size: 30px;
      }
      
      .resultListEntity {
      	list-style-type:none;
      	padding:none;
      	margin:none;

      }
      .resultListEntity li{
      	padding: 10px;
      	border-left: 5px solid #40bc5d;
      	border-right: 5px solid #40bc5d;
        background: rgba(221,255,216,0.2);
      	min-height: 30px;
      }
      .resultListEntity li:hover {
      	background-color: #b9d3b1;
      }
      
      
      
      .resultListRelational {
      	list-style-type:none;
      	padding:none;
      	margin:none;

      }
      .resultListRelational li{
      	padding: 10px;
      	border-left: 5px solid #40bc5d;
      	border-right: 5px solid #40bc5d;
        background: rgba(221,255,216,0.2);
      	min-height: 30px;
      }
      .resultListRelational li:hover {
      	background-color: #b9d3b1;
      }
      

      .end-float {
      	clear:both;
      }
      tr:nth-child(odd) {background-color: lightgrey;}
    </style>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>

  </head>
  <body>
  	<div class="navbar">
        <div id="div-logo"><span id="logo-font">wo:</span></div>
        <div style="position: absolute; left: 50%;">
	        <div class="searchFormContainer" style="position: relative; left: -50%;">
		        <form action="Search" id="searchForm" method="post">
		          <input type="text" id="searchField" name="searchField" value="<%out.print(request.getAttribute("userQuery")); %>"/>
		          <input type="submit" id="searchSubmit" name="searchSubmit" value="Go!"/>
		        </form>
		    </div>
	    </div>
    </div>
    <div class="content">
      <div id="resultsContainer">
      	
      	<%
      	out.print(request.getAttribute("resultHtml"));
      	%>
      	</table>
      </div>
    
    
    </div>

     
    <div class="footer">
        <!-- <span id="footer-font">&copy; Vasco &amp; Jolan</span> -->
    </div>
    <script>
    $('.resultLink').click(function(){goToOverview(this); return false;});
    
    
    
    console.log("llas");
    
    function goToOverview(e) {
    	var value = $(e).attr("href");
    	console.log(value);
    	$.post('\Details', {uri: value}, function() { window.location.href = '\Details' });
    }
    </script>
  </body>
</html>
